(ns clojure.automerge-clj.generate-interface
  (:require
   [clojure.java.io :as io]
   [clojure.java.shell :as sh]
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.set :as set]
   [camel-snake-kebab.core :as csk])
  (:import [org.automerge ChangeHash ;; To generate type hint for the array of ChangeHash
            ]))

(defonce special-case-functions 
  "(defonce +object-type-map+ ObjectType/MAP)
(defonce +object-type-list+ ObjectType/LIST)
(defonce +object-type-text+ ObjectType/TEXT)

(defonce +expand-mark-before+ ExpandMark/BEFORE)
(defonce +expand-mark-after+ ExpandMark/AFTER)
(defonce +expand-mark-both+ ExpandMark/BOTH)
(defonce +expand-mark-none+ ExpandMark/NONE)

(defn short? [n]
  (and (int? n)
       (<= Short/MIN_VALUE n Short/MAX_VALUE)))

(defn long? [n]
  (and (int? n)
       (or (< Integer/MAX_VALUE n)
           (> Integer/MIN_VALUE n))))

(defn array-instance? [c o]
  (and (-> o class .isArray)
       (= (-> o class .getComponentType)
          c)))")

;; clj-fn-entry method tclass class clj-fn return args opts
(defn- clj-fn-entry [method tclass class clj-fn return args {:keys [static? constructor?]}]
  ;; constructor
  ;; {:return return :fn clj-fn :fn-args fn-args :method method :method-args m-args}
  (let [fn-args (map second args)]
    {:return return
     :fn clj-fn
     :fn-args (if (or constructor? static?)
                fn-args
                (cons (csk/->kebab-case-string tclass) fn-args))
     :method (if static?
               (str class "/" method)
               method)
     :method-args (if (or constructor? static?)
                    args
                    (cons [class (csk/->kebab-case-string tclass)] args))
     :constructor? constructor?
     :static? static?}))

(defn def-file->build-fn-def-map [files]
  (loop [[f & more-f] files def-map {} all-classes []]
    (if f
      (let [[classes defs-map]
            (with-open [reader (clojure.java.io/reader f)] ; Open the file for reading
              (let [reader (java.io.PushbackReader. reader)]
                (let [[k arrow [tclass & subclasses]]
                      (list (read reader false :eof) (read reader false :eof) (read reader false :eof))]
                  (assert (and (= :classes k) (= '=> arrow) (symbol? tclass)) "File format must be valid")
                  (loop [method (read reader false :eof)
                         arrow (read reader false :eof)
                         class-cljfn-return-args-opts (read reader false :eof)
                         result {}]
                    (if (= :eof method)
                      [(cons tclass subclasses) result]
                      (let [[class clj-fn return args & opts] class-cljfn-return-args-opts]
                        (assert (and (symbol? method) (= '=> arrow) tclass class clj-fn)
                                "Entry format must be valid")
                        (->> opts
                             (clj-fn-entry method tclass class clj-fn return args)
                             (update result
                                     clj-fn
                                     (fnil conj []))
                             (recur (read reader false :eof)
                                    (read reader false :eof)
                                    (read reader false :eof)))))))))]
        (recur more-f (into def-map defs-map) (conj all-classes classes)))
      [all-classes def-map])))

(defn- group-by-n-args [methods]
  (group-by #(count (:fn-args %)) methods))

(defn- args->type-check-exps [args]
  (map (fn [[type arg]]
         (if (contains? #{"int" "long" "float" "double" "short" "boolean" "char"}
                        type)
           (format "(%s? %s)" type arg)
           (format "(instance? %s %s)" type arg)))
       args))

(defn make-cond-exp [args]
  (let [type-check-exps (args->type-check-exps args)]
    (if (= type-check-exps 1)
      type-check-exps
      (pp/cl-format nil "(and ~{~A~^ ~})" type-check-exps))))

(defn args->type-hint-exps [args]
  (map (fn [[type arg]]
         (if (contains? #{"int" "long" "float" "double" "short" "boolean" "char"}
                        type)
           (format "(%s %s)" type arg)
           (format "^%s %s" type arg)))
       args))

(defn make-fn-body [mv]
  (letfn [(call-exp [{:keys [return method method-args constructor? static?]}]
            (let [type-hint-args (args->type-hint-exps method-args)
                  method-type-pos (cond constructor? 0
                                        static? 1
                                        :else 2)]
              (pp/cl-format nil "~%~3T^~:[void~;~:*~A~] (~[~A.~;~A~;.~A~] ~{~A~^ ~})" 
                            return
                            method-type-pos
                            method
                            type-hint-args)))]
    (if (= (count mv) 1)
      [(call-exp (first mv))]
      (map (fn [{:keys [return method method-args] :as def}]
             (pp/cl-format nil "~%~3T~A ~A" (make-cond-exp method-args) (call-exp def)))
           mv))))

(defn- canonical-type [type]
  (cond (nil? type) "void"
        (symbol? type) (str "" type)

        (= '[:array-of int] type)  "ints"
        (= '[:array-of long] type) "longs"
        (= '[:array-of float] type) "floats"
        (= '[:array-of double] type) "dboubles"
        (= '[:array-of short] type) "shorts"
        (= '[:array-of boolean] type) "booleans"
        (= '[:array-of byte] type) "bytes"        
        (= '[:array-of char] type) "chars"
        (= '[:array-of object] type) "objects"

        (= (first type) :array-of)
        (try 
          (format "\"%s\"" (.getName (class (make-array (resolve (second type)) 0))))
          (catch Exception e
            (println ">>>" type)))

        :else (throw (ex-info "Unknown type" {:type type}))))

(defn update-body-args [m-list new-args]
  (if new-args
    (map #(update % :method-args
                  (fn [old new]
                    (map (fn [[t a] na]
                           [(canonical-type t) na]) old new))
                  new-args)
         m-list)
    m-list))

(defn def-map->clj-def-str-list [m]
  (letfn [(make-fn-args [fn-args m-list]
            (if (= 1 (count m-list)) ;; main class object
              fn-args
              (cons (first fn-args) ;; main class object
                    (->> (count fn-args)
                         dec
                         range
                         (map #(str "arg" %))))))]
    (map (fn [[fn def-list]]
           (let [grouped (group-by-n-args def-list)
                 [_ defs] (first grouped)
                 proto-def (first defs)
                 fn-args (make-fn-args (:fn-args proto-def) defs)]
             (pp/cl-format nil "~%(defn ~A~%~3T([~{~A~^ ~}]~{~A~^ ~}))"
                           (:fn proto-def)
                           fn-args
                           (-> (update-body-args defs fn-args)
                               (make-fn-body)))))
         m)))

(defn def-str-list->file [file classes str-list]
  (with-open [o (io/writer file)]
    (binding [*out* o]
      (println ";;;\n;;; Generated file, do not edit\n;;;\n")
      (pp/cl-format *out* "(ns clojure.automerge-clj.automerge-interface
      (:import [java.util Optional List HashMap Date Iterator ArrayList]
               [org.automerge ObjectType ExpandMark
                ~{~A~^ ~}]))~2&~A~&"
                    `(~@(map first classes)
                      ~@(for [[c & sl] classes
                              s sl]
                          (str c "$" s)))
                    special-case-functions)
      (run! #(println %) str-list))))

(defn java->clojure-interface-file [java-files clj-file]
  (run! #(sh/sh "bash" "-c" (str "cd src/python && source python-env/bin/activate && python3 parse-java.py " %))
        java-files)
  (let [clj-files (map #(let [fname (-> (str/split % #"\/")
                                        last
                                        (str/split #"\.")
                                        first)]
                          (str "/tmp/" fname ".clj"))
                       java-files)
        [classes def-map] (def-file->build-fn-def-map clj-files)]
    (->>  (def-map->clj-def-str-list def-map)
          (def-str-list->file clj-file classes))))



(comment

  (defn array-instance? [c o]
    (and (-> o class .isArray)
         (= (-> o class .getComponentType)
            c)))

  (let [arr (make-array ChangeHash 5)]
    (array-instance? ChangeHasharr))

  (java->clojure-interface-file ["~/Work/automerge-java/lib/src/main/java/org/automerge/Document.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Transaction.java"
                                 ;; Counter is package class!
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Counter.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/ChangeHash.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Cursor.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/PatchLog.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/SyncState.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/NewValue.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/AmValue.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/ObjectId.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Patch.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/PatchAction.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Mark.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Prop.java"


                                 ;; "~/Work/automerge-java/lib/src/main/java/org/automerge/ObjectType.java"

                                 ]
                                "src/clojure/automerge_clj/automerge_interface.clj")




  (set! *warn-on-reflection* true)
  (set! *print-meta* true)
  (let [^String s (apply str "hello")] (.charAt s 3))
  (let [s ^String (apply str "hello")] (.charAt s 3))

  (loop [i 0 c 0]
    (if (== i 5)
      c
      (recur (inc i) (apply + i [c]))))

  (defn sqrt-long ^Double [^Long n]
    (Math/sqrt n))
  (meta (-> (meta #'sqrt-long) :arglists first))

  (defn sqrt-long ^double [^long n]
    (Math/sqrt n))
  (meta (-> (meta #'sqrt-long) :arglists first))

  (.getName (class (make-array String 1 2 3)))
  (.getName (class (make-array Integer/TYPE 1  2 3)))

  (let [arr ^longs (make-array Long/TYPE 3)
        c (aget arr 1)]
    (aset arr 0 ^long c))

  (defn ^Optional document-get-all
    ([^Document document arg1 arg2]
     (cond (and (instance? ObjectId arg1) (instance? String arg2))
           (.getAll document ^ObjectId arg1 ^String arg2)
           (and (instance? ObjectId arg1) (integer? arg2))
           (.getAll document ^ObjectId arg1 ^Integer (int arg2))
           :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
    ([^Document document arg1 arg2 arg3]
     (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
           (.getAll document ^ObjectId arg1 ^String arg2 ^"[Lorg.automerge.ChangeHash;" arg3)
           (and (instance? ObjectId arg1) (integer? arg2) (array-instance? ChangeHash arg3))
           (.getAll document ^ObjectId arg1 ^Integer (int arg2) ^"[Lorg.automerge.ChangeHash;" arg3)
           :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

  )
