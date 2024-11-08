(ns clojure.automerge-clj.generate-interface
  (:require
   [clojure.java.io :as io]
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.java.shell :as sh]
   [camel-snake-kebab.core :as csk])
  (:import [org.automerge Document ObjectId ChangeHash ObjectType
            PatchLog SyncState Transaction Cursor]
           [java.util Optional List HashMap]))

(defn init-lib []
  (Document.) ;; Native lib init happens
  ObjectId/ROOT ;; Access lazy field
  )

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

        (symbol? type) (str "^" type)

        (= '[:array-of byte] type) "^bytes"

        (= (first type) :array-of)
        (format "^\"%s\"" (.getName (class (make-array (resolve (second type)) 0 0 0))))

        :else (throw (ex-info "Unknown type" {:type type}))))

(defn- make-main-instance-arg [java-class]
  [(canonical-type java-class) (symbol (str/lower-case java-class))])

(defn- generate-a-function [java-fn java-class def]
  (let [[fn-name return-type type+args] def
        [types args] (split-type+args type+args)]
    (pp/cl-format nil "(defn ~{~A~} ~A [~{~A~^ ~}]~%~2T(. ~{~A~^ ~}))~&"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  `(~@(make-main-instance-arg java-class)
                    ~@(mapcat (fn [type arg]
                                (list (canonical-type type) arg))
                              types args))
                  `(~(str/lower-case java-class)
                    ~java-fn
                    ~@args))))

;; (clojure.core/defn
;;   document-generate-sync-message
;;   Optional
;;   ([SyncState sync-state]))
(defn- generate-simple-multiple-arity-functions [java-fn java-class defs]
  (assert (apply = (map second defs)) "Return type must be same")
  (let [[fn-name return-type type+args] (first defs)]
    (pp/cl-format nil "(defn ~{~A~} ~A~%~{~A~^~%~})~&"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  (for [[_ _ type+args] defs
                        :let [[types args] (split-type+args type+args)]]
                    (pp/cl-format nil "~2T([~{~A~^ ~}]~%~2T (. ~{~A~^ ~}))"
                                  `(~@(make-main-instance-arg java-class)
                                    ~@(mapcat (fn [type arg]
                                                (list (canonical-type type) arg))
                                              types args))
                                  `(~(str/lower-case java-class)
                                    ~java-fn
                                    ~@args))))))
;;;
;;; Multiple arity case
;;;
(defn- group-by-arity [methods]
  "Group methods by number of arguments"
  (group-by #(count (nth % 2)) methods))

(defn- type-check-expr [arg-num type-spec]
  (let [arg (symbol (str "arg" arg-num))]
    (cond (symbol? type-spec)
          (cond (= type-spec 'int) `(~'integer? ~arg)
                :else
                `(~'instance? ~(symbol type-spec) ~arg))

          (and (vector? type-spec) (= :array-of (first type-spec)))
          (let [type (second type-spec)]
            (cond (= type 'byte) `(~'bytes? ~arg)
                  :else
                  `(~'array-instance? ~(second type-spec) ~arg)))

          :else (throw (ex-info "Uknown type-spec" {:type-spec type-spec})))))

(defn- make-cond-clause [java-fn obj method]
  (let [method-name (first method)
        return-type (second method)
        args (nth method 2)]
    (if (empty? args)
      `(.~obj ~java-fn)
      (let [conditions (map-indexed
                        (fn [idx [type _]]
                          (type-check-expr (inc idx) type))
                        args)
            arg-names (map-indexed
                       (fn [idx [type name]]
                         [type (symbol (str "arg" (inc idx)))])
                       args)]
        `[~@(if (> (count conditions) 1)
              `((~'and ~@conditions))
              conditions)
          (. ~obj ~java-fn ~@(mapcat (fn [[type arg]] [(canonical-type type) arg]) arg-names))]))))

(defn- make-arity-method [java-fn java-class methods arity]
  (let [arg-symbols (map #(symbol (str "arg" (inc %))) (range arity))
        [main-obj-type main-obj] (make-main-instance-arg java-class)
        cond-clauses (mapcat #(make-cond-clause java-fn main-obj %) methods)]
    (if (zero? arity)
      (pp/cl-format nil "[~{~A~^ ~}]~%~3T~A"
                    `(~main-obj-type ~main-obj ~@arg-symbols) cond-clauses)
      (pp/cl-format nil "[~{~A~^ ~}]~%~2T(cond ~{~A~^~%~8T~}~%~8T:else (throw (ex-info \"Type error\" ~S)))"
                    `(~main-obj-type ~main-obj ~@arg-symbols) cond-clauses (zipmap (map #(keyword (str "arg" (inc %)))
                                                                                        (range arity))
                                                                                   arg-symbols)))))

(defn- generate-complex-multiple-arity-functions [java-fn java-class defs]
  (assert  (apply = (map second defs)) (str "Return type must be same" java-fn defs))
  (let [[fn-name return-type type+args] (first defs)
        grouped (group-by-arity defs)
        arity-methods (map (fn [[arity methods]]
                             (make-arity-method java-fn java-class methods arity))
                           grouped)]
    (pp/cl-format nil "(defn ~{~A~} ~A~%~{~2T(~A)~^~%~})~&"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  arity-methods)))

(defn- generate-multy-arity-functions [java-fn java-class defs]
  (if (simple-case? defs)
    (generate-simple-multiple-arity-functions java-fn java-class defs)
    (generate-complex-multiple-arity-functions java-fn java-class  defs)))

(defn generate-functions [java-class [java-fn clj-defs]]
  (if (= (count clj-defs) 1)
    (generate-a-function java-fn java-class (first clj-defs))
    (generate-multy-arity-functions java-fn java-class clj-defs)))

(defn generate-clojre-interface [filename]
  "Reads the file, processes each line, and returns a map of function names to their Clojure definitions."
  (with-open [reader (clojure.java.io/reader filename)]  ; Open the file for reading
    (let [reader (java.io.PushbackReader. reader)
          [class-name def-map]
          (loop [k nil result {} class-name nil]
            (let [exp (read reader false :eof)]
              (cond (= :eof exp) [class-name result]
                    (= '=> exp) (recur k result class-name)
                    (symbol? exp) (recur exp 
                                         (update result exp (fnil identity []))
                                         class-name)
                    (list? exp) ;; def
                    (cond class-name (recur k (update result k conj exp) class-name)

                          (= (first exp) :class-name)
                          (recur k result (second exp))

                          :else
                          (recur k (update result k conj exp) class-name)))))]
      (map #(generate-functions class-name %) def-map))))

(defn- interface-header [classes-to-import]
  (println ";;;\n;;; Generated file, do not edit\n;;;\n")
  (pp/cl-format true "(ns clojure.automerge-clj.automerge-interface
        (:import [java.util Optional List HashMap]
                 [org.automerge ~{~A~^ ~}]))~2&~A~2&"
                classes-to-import
                "(defn- array-instance? [c o]
               (and (-> o class .isArray)
                    (= (-> o class .getComponentType)
                       c)))"))

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
        (doseq [l (generate-clojre-interface (str "/tmp/" jv-clj-name))]
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
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/ObjectId.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/ChangeHash.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/PatchLog.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/Cursor.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/SyncState.java"
                                 "~/Work/automerge-java/lib/src/main/java/org/automerge/PatchLog.java"
                                 ]
                                "src/clojure/automerge_clj/automerge_interface.clj")
  )
