(ns clojure.automerge-clj.generate-interface
  (:require
   [clojure.java.io :as io]
   [clojure.java.shell :as sh]
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.set :as set]
   [camel-snake-kebab.core :as csk])
  (:import [java.util Optional List HashMap Date Iterator ArrayList]
           [org.automerge ObjectId ObjectType ExpandMark Conflicts
            Document Transaction ChangeHash Cursor PatchLog SyncState NewValue
            AmValue ObjectId Patch PatchAction Mark Prop Prop$Key Prop$Index
            PatchAction$PutMap PatchAction$PutList PatchAction$Increment PatchAction$FlagConflict
            PatchAction$Mark
            PatchAction$PutList PatchAction$Insert PatchAction$SpliceText PatchAction$DeleteList
            AmValue$Map AmValue$List AmValue$Text AmValue$UInt AmValue$Int AmValue$Bool AmValue$Bytes
            AmValue$Str AmValue$F64 AmValue$Counter AmValue$Timestamp AmValue$Null AmValue$Unknown
            Counter]))

(defonce ^:dynamic *fout* nil)

(defn- simple-case? [defs]
  (let [num-arg-list (map #(count (nth % 4)) defs)]
    (or (empty? num-arg-list)
        (= (count num-arg-list)
           (count (dedupe num-arg-list))))))

(defn- split-type+args [type+args]
  (loop [[[type arg] & more-defs] type+args types [] args []]
    (cond (nil? type) [types args]
          :else
          (recur more-defs (conj types type) (conj args arg)))))

(defn- canonical-type [type]
  (cond (nil? type) nil

        (= 'int type) "^Integer"
        (= 'double type) "^Double"
        (= 'long type) "^Long"

        (= 'boolean type) "^Boolean"

        (symbol? type) (str "^" type)

        (= '[:array-of byte] type) "^bytes"

        (= (first type) :array-of)
        (format "^\"%s\"" (.getName (class (make-array (resolve (second type)) 0))))

        :else (throw (ex-info "Unknown type" {:type type}))))

(defn- make-main-instance-arg [java-class & {:keys [static? constructor?]}]
  (when-not (or static? constructor?)
    [(canonical-type java-class) (symbol (str/lower-case java-class))]))

(defn- maybe-call-converter [args]
  (vec (mapcat (fn [[tp arg]]
                 (case tp
                   "^Long" `[~tp (~'long ~arg)]
                   "^Integer" `[~tp (~'int ~arg)]
                   "^Double" `[~tp (~'double ~arg)]
                   "^Boolean" `[~tp (~'boolean ~arg)]
                   [tp arg]))
               (partition 2 args))))

(defn- make-java-call-signature [java-class java-fn args & {:keys [static? constructor?]}]
  (let [new-args (maybe-call-converter args)]
    (cond constructor? `(~(str java-class \.) ~@new-args)
          static? `(~(str java-class "/" java-fn) ~@new-args)
          :else `(~(str \. java-fn) ~(str/lower-case java-class) ~@new-args))))

(defn- java->clj-fname+main-arg [tclass class method]
  (let [clj-fn (csk/->kebab-case method)]
    (cond (= tclass class method) [(str "make-" clj-fn) tclass] ;; ctor
          (= tclass class) [clj-fn tclass]
          :else [clj-fn class])))

(defn- generate-a-function [[tclass class java-fn return-type type+args &
                             {:keys [static? constructor?] :as opts}]]
  (let [[fn-name java-class] (java->clj-fname+main-arg tclass class java-fn)
        [types args] (split-type+args type+args)
        canonicalized-args (mapcat (fn [type arg]
                                     (list (canonical-type type) arg))
                                   types args)
        fn-args (take-nth 2 (rest canonicalized-args))]
    (pp/cl-format *fout* "~2&(defn ~{~A~} ~A [~{~A~^ ~}]~%~2T(~{~A~^ ~}))~&"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  `(~@(make-main-instance-arg java-class opts)
                    ~@fn-args)
                  (make-java-call-signature java-class java-fn canonicalized-args opts))))

(defn- generate-simple-multiple-arity-functions [defs]
  (assert (apply = (map #(nth % 3) defs)) "Return type must be same")
  (let [[tclass class java-fn return-type type+args & {:keys [static? constructor?] :as opts}] (first defs)
        [fn-name java-class] (java->clj-fname+main-arg tclass class java-fn)]
    (pp/cl-format *fout* "~2&(defn ~{~A~} ~A~%~{~A~^~%~})~&"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  (run! (fn [[_ _ _ _ type+args]]
                          (let [[types args] (split-type+args type+args)
                                canonicalized-args (mapcat (fn [type arg]
                                                             (list (canonical-type type) arg))
                                                           types args)
                                fn-args (take-nth 2 (rest canonicalized-args))]
                            (pp/cl-format nil "~2T([~{~A~^ ~}]~%~2T (~{~A~^ ~}))"
                                          `(~@(make-main-instance-arg java-class opts)
                                            ~@fn-args)
                                          (make-java-call-signature java-class java-fn canonicalized-args opts))))
                        defs))))
;;;
;;; Multiple arity case
;;;
(defn- group-by-arity [methods]
  "Group methods by number of arguments"
  (group-by #(count (nth % 4)) methods))

(defn clj-type-predicate [type-spec]
  (case type-spec
    int 'integer?
    long 'integer?
    double 'double?
    boolean 'boolean?
    false))

(defn- type-check-expr [arg-num type-spec]
  (let [arg (symbol (str "arg" arg-num))]
    (cond (symbol? type-spec)
          (if-let [predicate (clj-type-predicate type-spec)]
            `(~predicate ~arg)
            `(~'instance? ~type-spec ~arg))

          (and (vector? type-spec) (= :array-of (first type-spec)))
          (let [type (second type-spec)]
            (cond (= type 'byte) `(~'bytes? ~arg)
                  :else
                  `(~'array-instance? ~(second type-spec) ~arg)))

          :else (throw (ex-info "Uknown type-spec" {:type-spec type-spec})))))

(defn- make-cond-clause [java-class java-fn obj [tclass class fname mreturn-type args & opts]]
  (let [conditions (map-indexed
                    (fn [idx [type _]]
                      (type-check-expr (inc idx) type))
                    args)
        arg-names (map-indexed
                   (fn [idx [type name]]
                     [type (symbol (str "arg" (inc idx)))])
                   args)
        call-signature (make-java-call-signature java-class
                                                 java-fn
                                                 `(~@(mapcat (fn [[type arg]] [(canonical-type type) arg]) arg-names))
                                                 opts)]
    (case (count conditions)
      0 call-signature
      1 `(~@conditions ~call-signature)
      `((~'and ~@conditions)
        ~(make-java-call-signature java-class
                                   java-fn
                                   `(~@(mapcat (fn [[type arg]] [(canonical-type type) arg]) arg-names))
                                   opts)))))

(defn- make-arity-method [java-fn java-class methods arity opts]
  (let [arg-symbols (map #(symbol (str "arg" (inc %))) (range arity))
        [main-obj-type main-obj] (make-main-instance-arg java-class opts)
        cond-clauses (mapcat #(make-cond-clause java-class java-fn main-obj %) methods)]
    (if (zero? arity)
      (pp/cl-format nil "[~{~A~^ ~}]~%~3T~A"
                    `(~main-obj-type ~main-obj ~@arg-symbols) cond-clauses)
      (pp/cl-format nil "[~{~A~^ ~}]~%~2T(cond ~{~A~^~%~8T~}~%~8T:else (throw (ex-info \"Type error\" ~S)))"
                    `(~main-obj-type ~main-obj ~@arg-symbols) cond-clauses (zipmap (map #(keyword (str "arg" (inc %)))
                                                                                        (range arity))
                                                                                   arg-symbols)))))

(defn- generate-complex-multiple-arity-functions [defs]
  (let [[tclass class java-fn return-type _ & opts] (first defs)
        [fn-name java-class] (java->clj-fname+main-arg tclass class java-fn)
        grouped (group-by-arity defs)
        arity-methods (map (fn [[arity methods]]
                             (make-arity-method java-fn java-class methods arity opts))
                           grouped)]
    (pp/cl-format *fout* "~2&(defn ~{~A~} ~A~%~{~2T(~A)~^~%~})~&"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  arity-methods)))

(defn- generate-multy-arity-functions [defs]
  (cond (= (count defs) (->> defs (map first) dedupe count))
        (run! #(generate-a-function %) defs)

        (simple-case? defs) (generate-simple-multiple-arity-functions defs)

        :else (generate-complex-multiple-arity-functions defs)))

(def +unusable-classes+ #{'Counter})

(defn remove-unusable-methods [defs]
  (filter (fn [[_ _ _ _ params & _]]
            (-> (map first params)
                (set)
                (set/intersection  +unusable-classes+)
                (empty?)))
          defs))

(defn generate-functions [clj-defs]
  (let [filtered-defs (remove-unusable-methods clj-defs)]
    (if (= (count clj-defs) 1)
      (generate-a-function (first filtered-defs))
      (generate-multy-arity-functions filtered-defs))))

(defn generate-clojure-interface [filename]
  "Reads the file, processes each line, and returns a map of function names to their Clojure definitions."
  (with-open [reader (clojure.java.io/reader filename)]  ; Open the file for reading
    (let [reader (java.io.PushbackReader. reader)
          [k arrow tclass] (list  (read reader false :eof) (read reader false :eof) (read reader false :eof))]

      (assert (and (= :java-name k) (= '=> arrow) (symbol? tclass)) "File format must be valid")

      (->> (loop [result {}]
             (let [class (read reader false :eof)
                   arrow (read reader false :eof)
                   class-fn-return-args (read reader false :eof)]
               (when-not (= :eof class)
                 (assert (and (symbol? class) (= '=> arrow) (list? class-fn-return-args))
                         "Entry format must be valid"))
               (if (= :eof class)
                 result
                 (recur (update result
                                (second class-fn-return-args) ;; fn is the key
                                (fnil conj [])
                                (cons tclass class-fn-return-args))))))
           (map (fn [[fname tclass-class-fn-return-args]]
                  (generate-functions tclass-class-fn-return-args)))))))

;; (defn long? [x]
;;   (and (integer? x)
;;        (or (> x Integer/MAX_VALUE)
;;            (< x Integer/MIN_VALUE))))


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

(defn- interface-header [classes-to-import]
  (binding [*out* *fout*]
    (println ";;;\n;;; Generated file, do not edit\n;;;\n")
    (pp/cl-format *fout* "(ns clojure.automerge-clj.automerge-interface
        (:import [java.util Optional List HashMap Date Iterator ArrayList]
                 [org.automerge ObjectId ObjectType ExpandMark~%~{~A~^ ~}]))~2&~A~2&"
                  classes-to-import
                  special-case-functions)))

(defn generate-clojure-interface [filename]
  "Reads the file, processes each line, and returns a map of function names to their Clojure definitions."
  (with-open [reader (clojure.java.io/reader filename)]  ; Open the file for reading
    (let [reader (java.io.PushbackReader. reader)
          [k arrow tclass] (list  (read reader false :eof) (read reader false :eof) (read reader false :eof))]

      (assert (and (= :java-name k) (= '=> arrow) (symbol? tclass)) "File format must be valid")

      (->> (loop [result {}]
             (let [class (read reader false :eof)
                   arrow (read reader false :eof)
                   class-fn-return-args (read reader false :eof)]
               (when-not (= :eof class)
                 (assert (and (symbol? class) (= '=> arrow) (list? class-fn-return-args))
                         "Entry format must be valid"))
               (if (= :eof class)
                 result
                 (recur (update result
                                (second class-fn-return-args) ;; fn is the key
                                (fnil conj [])
                                (cons tclass class-fn-return-args))))))
           (map (fn [[fname tclass-class-fn-return-args]]
                  (generate-functions tclass-class-fn-return-args)))))))

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
  (loop [[f & more-f] files
         result {}]
    (if f
      (recur more-f
             (into result
                   (with-open [reader (clojure.java.io/reader f)] ; Open the file for reading
                     (let [reader (java.io.PushbackReader. reader)]
                       (let [[k arrow tclass] (list  (read reader false :eof) (read reader false :eof) (read reader false :eof))]
                         (assert (and (= :java-name k) (= '=> arrow) (symbol? tclass)) "File format must be valid")
                         (loop [method (read reader false :eof)
                                arrow (read reader false :eof)
                                class-cljfn-return-args-opts (read reader false :eof)
                                result {}]
                           (if (= :eof method)
                             result
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
                                           (read reader false :eof)))))))))))
      result)))

(defn- group-by-n-args [methods]
  ;;{:keys [return fn fn-args method method-args]}
  (group-by #(count (:fn-args %)) methods))

;; (cond (nil? type) "void"
;;       (symbol? type) (str "" type)

;;       (= '[:array-of int] type)  "ints"
;;       (= '[:array-of long] type) "longs"
;;       (= '[:array-of float] type) "floats"
;;       (= '[:array-of double] type) "dboubles"
;;       (= '[:array-of short] type) "shorts"
;;       (= '[:array-of boolean] type) "booleans"
;;       (= '[:array-of byte] type) "bytes"        
;;       (= '[:array-of char] type) "chars"
;;       (= '[:array-of object] type) "objects"

;;       (= (first type) :array-of)
;;       (format "\"%s\"" (.getName (class (make-array (resolve (second type)) 0))))

;;       :else (throw (ex-info "Unknown type" {:type type})))



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
              (pp/cl-format nil "~%~3T^~:[void~;~:*~A~] (~[~A.~;~A~;.~A~] ~{~A ~^~})" 
                            return
                            method-type-pos
                            method
                            type-hint-args)
              #_
              (if return
                (pp/cl-format nil "~%~3T^~A (~[~A.~;~A~;.~A~] ~{~A ~^~})" 
                              return
                              method-type-pos
                              method
                              type-hint-args)
                (pp/cl-format nil "~%~3T^void (~:[~A.~;~A~;.~A~] ~{~A ~^~})" 
                              method-type-pos
                              method
                              type-hint-args))))]
    (if (= (count mv) 1)
      (call-exp (first mv))
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
        (format "\"%s\"" (.getName (class (make-array (resolve (second type)) 0))))

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
             [(pp/cl-format nil "~%(defn ~A~%~3T([~{~A~^ ~}]~{~A~^~}))"
                            (:fn proto-def)
                            fn-args
                            (-> (update-body-args defs fn-args)
                                (make-fn-body)))]))
         m)))

(defn def-str-list->file [file str-list]
  (with-open [o (io/writer file)]
    (binding [*out* o]
      (run! #(apply println %) str-list))))

(defn java->clojure-interface-file [java-files clj-file]
  (run! #(sh/sh "bash" "-c" (str "cd src/python && source python-env/bin/activate && python3 parse-java.py " %))
        java-files)
  (let [clj-files (map #(let [fname (-> (str/split % #"\/")
                                        last
                                        (str/split #"\.")
                                        first)]
                          (str "/tmp/" fname ".clj"))
                       java-files)]
    (->>  clj-files
          def-file->build-fn-def-map
          def-map->clj-def-str-list
          (def-str-list->file clj-file))))



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
                                 ;;"~/Work/automerge-java/lib/src/main/java/org/automerge/Counter.java"
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
