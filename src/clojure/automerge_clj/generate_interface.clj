(ns clojure.automerge-clj.generate-interface
  (:require
   [clojure.java.io :as io]
   [clojure.java.shell :as sh]
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.set :as set])
  (:import
   (org.automerge ChangeHash Document ObjectId)))

(defn init-lib []
  (let [doc (Document.)]   ;; Native lib init happens
    ObjectId/ROOT          ;; Access lazy field
    (.free doc)))

(defn- simple-case? [defs]
  (let [num-arg-list (map #(count (nth %  2)) defs)]
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
        (= 'long type) "^Long"

        (= 'boolean type) "^Boolean"

        (symbol? type) (str "^" type)

        (= '[:array-of byte] type) "^bytes"

        (= (first type) :array-of)
        (format "^\"%s\"" (.getName (class (make-array (resolve (second type)) 0 0 0))))

        :else (throw (ex-info "Unknown type" {:type type}))))

(defn- make-main-instance-arg [java-class & {:keys [static? constructor?]}]
  (when-not (or static? constructor?)
    [(canonical-type java-class) (symbol (str/lower-case java-class))]))

(defn- make-java-call-signature [java-class java-fn args & {:keys [static? constructor?]}]
  (cond constructor? `(~(str java-class \.) ~@args)
        static? `(~(str java-class "/" java-fn) ~@args)
        :else `(~(str \. java-fn) ~(str/lower-case java-class) ~@args)))

(defn- generate-a-function [java-fn java-class def]
  (let [[fn-name return-type type+args & {:keys [static? constructor?] :as opts}] def
        [types args] (split-type+args type+args)]
    (pp/cl-format nil "(defn ~{~A~} ~A [~{~A~^ ~}]~%~2T(~{~A~^ ~}))~&"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  `(~@(make-main-instance-arg java-class opts)
                    ~@(mapcat (fn [type arg]
                                (list (canonical-type type) arg))
                              types args))
                  (make-java-call-signature java-class java-fn args opts))))

;; (clojure.core/defn
;;   document-generate-sync-message
;;   Optional
;;   ([SyncState sync-state]))
(defn- generate-simple-multiple-arity-functions [java-fn java-class defs]
  (assert (apply = (map second defs)) "Return type must be same")
  (let [[fn-name return-type type+args & {:keys [static? constructor?] :as opts}] (first defs)]
    (pp/cl-format nil "(defn ~{~A~} ~A~%~{~A~^~%~})~&"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  (for [[_ _ type+args] defs
                        :let [[types args] (split-type+args type+args)]]
                    (pp/cl-format nil "~2T([~{~A~^ ~}]~%~2T (~{~A~^ ~}))"
                                  `(~@(make-main-instance-arg java-class opts)
                                    ~@(mapcat (fn [type arg]
                                                (list (canonical-type type) arg))
                                              types args))
                                  (make-java-call-signature java-class java-fn args opts))))))
;;;
;;; Multiple arity case
;;;
(defn- group-by-arity [methods]
  "Group methods by number of arguments"
  (group-by #(count (nth % 2)) methods))

(def +clj->java-types+ {:int 'Integer :long 'Long :double 'Double :boolean 'Boolean})

(defn- type-check-expr [arg-num type-spec]
  (let [arg (symbol (str "arg" arg-num))]
    (cond (symbol? type-spec)
          `(~'instance? ~(get +clj->java-types+ (keyword type-spec) type-spec) ~arg)

          (and (vector? type-spec) (= :array-of (first type-spec)))
          (let [type (second type-spec)]
            (cond (= type 'byte) `(~'bytes? ~arg)
                  :else
                  `(~'array-instance? ~(second type-spec) ~arg)))

          :else (throw (ex-info "Uknown type-spec" {:type-spec type-spec})))))

(defn- make-cond-clause [java-class java-fn obj [mname mreturn-type args opts]]
  (let [conditions (map-indexed
                    (fn [idx [type _]]
                      (type-check-expr (inc idx) type))
                    args)
        arg-names (map-indexed
                   (fn [idx [type name]]
                     [type (symbol (str "arg" (inc idx)))])
                   args)]
    `(~@(if (> (count conditions) 1)
          `((~'and ~@conditions))
          conditions)
      ~(make-java-call-signature java-class
                                 java-fn
                                 `(~@(mapcat (fn [[type arg]] [(canonical-type type) arg]) arg-names))
                                 opts))))

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

(defn- generate-complex-multiple-arity-functions [java-fn java-class defs]
  (let [return-types (->> (map second defs) (filter identity))]
    (when-not (empty? return-types)
      (assert (apply = return-types) (str "Return type must be same, except nil(voide)" java-fn defs)))
    (let [[fn-name _ type+args & opts] (first defs)
          grouped (group-by-arity defs)
          arity-methods (map (fn [[arity methods]]
                               (make-arity-method java-fn java-class methods arity opts))
                             grouped)]
      (pp/cl-format nil "(defn ~{~A~} ~A~%~{~2T(~A)~^~%~})~&"
                    (when-let [result (canonical-type (first return-types))]
                      result)
                    fn-name
                    arity-methods))))

(defn- generate-multy-arity-functions [java-fn java-class defs]
  (if (simple-case? defs)
    (generate-simple-multiple-arity-functions java-fn java-class defs)
    (generate-complex-multiple-arity-functions java-fn java-class  defs)))

(def +unusable-classes+ #{'Counter})

(defn remove-unusable-methods [defs]
  (filter (fn [[_ _ params & _]]
            (-> (map first params)
                (set)
                (set/intersection  +unusable-classes+)
                (empty?)))
          defs))

(defn generate-functions [java-class [java-fn clj-defs]]
  (let [filtered-defs (remove-unusable-methods clj-defs)]
    (if (= (count clj-defs) 1)
      (generate-a-function java-fn java-class (first filtered-defs))
      (generate-multy-arity-functions java-fn java-class filtered-defs))))

(defn generate-clojure-interface [filename]
  "Reads the file, processes each line, and returns a map of function names to their Clojure definitions."
  (with-open [reader (clojure.java.io/reader filename)]  ; Open the file for reading
    (let [reader (java.io.PushbackReader. reader)
          [k arrow java-name] (list  (read reader false :eof) (read reader false :eof) (read reader false :eof))]

      (assert (and (= :java-name k) (= '=> arrow) (symbol? java-name)) "File format must be valid")

      (->> (loop [result {}]
             (let [java-name (read reader false :eof)
                   arrow (read reader false :eof)
                   clj-name-return-args (read reader false :eof)]
               (when-not (= :eof java-name)
                 (assert (and (symbol? java-name) (= '=> arrow) (list? clj-name-return-args))
                         "Entry format must be valid"))
               (if (= :eof java-name)
                 result
                 (recur (update result
                                java-name
                                (fnil conj [])
                                clj-name-return-args)))))
           (map (fn [[jfn exp]]
                  (generate-functions java-name [jfn exp])))))))

(defonce special-case-functions 
  "(defonce +object-id-root+ ObjectId/ROOT)

(defonce +object-type-map+ ObjectType/MAP)
(defonce +object-type-list+ ObjectType/LIST)
(defonce +object-type-text+ ObjectType/TEXT)

(defonce +expand-mark-before+ ExpandMark/BEFORE)
(defonce +expand-mark-after+ ExpandMark/AFTER)
(defonce +expand-mark-both+ ExpandMark/BOTH)
(defonce +expand-mark-none+ ExpandMark/NONE)


(defn- array-instance? [c o]
  (and (-> o class .isArray)
       (= (-> o class .getComponentType)
          c)))")

(defn- interface-header [classes-to-import]
  (println ";;;\n;;; Generated file, do not edit\n;;;\n")
  (pp/cl-format true "(ns clojure.automerge-clj.automerge-interface
        (:import [java.util Optional List HashMap Date Iterator]
                 [org.automerge ObjectId ObjectType ExpandMark~%~{~A~^ ~}]))~2&~A~2&"
                classes-to-import
                special-case-functions))

(defn java->clojure-interface-file [java-files clj-file]
  (let [file (io/file clj-file)]
    (when (.exists file)
      (io/delete-file file)))
  (with-open [out (io/writer clj-file :append true)]
    (binding [*out* out]
      (interface-header (map (fn [f]
                               (-> (str/split f #"\/")
                                   last
                                   (str/split #"\.")
                                   first))
                             java-files))
      (doseq [java-file java-files
              :let [[name _] (-> (str/split java-file #"\/")
                                 last
                                 (str/split #"\."))
                    jv-clj-name (str name ".clj")]]
        (sh/sh "bash" "-c" (str "cd src/python && source python-env/bin/activate && python3 parse-java.py " java-file))
        (pp/cl-format true "~&;;; Class ~A~2&" name)
        (doseq [l (generate-clojure-interface (str "/tmp/" jv-clj-name))]
          (println l))))))

(comment
  (defn array-instance? [c o]
    (and (-> o class .isArray)
         (= (-> o class .getComponentType)
            c)))

  (let [arr (make-array ChangeHash 5)]
    (array-instance? ChangeHash arr))

  (java->clojure-interface-file ["~/Work/automerge-java/lib/src/main/java/org/automerge/Document.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Transaction.java"
                                 ;;"~/Work/automerge-java/lib/src/main/java/org/automerge/Counter.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/ChangeHash.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Cursor.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/PatchLog.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/SyncState.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/NewValue.java"
                                 ;; "~/Work/automerge-java/lib/src/main/java/org/automerge/CommitResult.java"
                                 ;; "~/Work/automerge-java/lib/src/main/java/org/automerge/ObjectId.java"

                                 ;; "~/Work/automerge-java/lib/src/main/java/org/automerge/ObjectType.java"

                                 ]
                                "src/clojure/automerge_clj/automerge_interface.clj")
  )

