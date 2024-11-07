(ns clojure.automerge-clj.generate-interface
  (:require
   [clojure.java.io :as io]
   [clojure.pprint :as pp]
   [clojure.string :as str])
  (:import [org.automerge AutomergeSys Document ObjectId ChangeHash ObjectType
            PatchLog SyncState Transaction Cursor]
           [java.util Optional List HashMap]))

(defn array-instance? [c o]
  (and (-> o class .isArray)
       (= (-> o class .getComponentType)
          c)))

;; Usage example:
(comment
  (let [arr (make-array ChangeHash 5)]
    (array-instance? ChangeHash arr))  ; => true
  
  (array-instance? ChangeHash [1 2 3]) ; => false
  )

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
  [(canonical-type java-class) (str/lower-case java-class)])

(defn- generate-a-function [java-fn java-class def]
  (let [[fn-name return-type type+args] def
        [types args] (split-type+args type+args)]
    (pp/cl-format nil "(defn ~{~A~} ~A [~{~A~^ ~}]~%~2T(. ~{~A~^ ~}))~2&"
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
  (assert (and (apply = (map second defs))) "Return values must be same")
  (let [[fn-name return-type type+args] (first defs)]
    (with-out-str
      (pp/cl-format true "(defn ~{~A~} ~A~&~{~A~^~})"
                    (when-let [result (canonical-type return-type)]
                      result)
                    fn-name
                    (for [[_ _ type+args] defs
                          :let [[types args] (split-type+args type+args)]]
                      (pp/cl-format nil "~2T([~{~A~^ ~}]~%~2T (. ~{~A~^~%~}))"
                                    `(~@(make-main-instance-arg java-class)
                                      ~@(mapcat (fn [type arg]
                                                  (list (canonical-type type) arg))
                                                types args))
                                    `(~(str/lower-case java-class)
                                      ~java-fn
                                      ~@args)))))))

;;;
;;; Multiple arity case
;;;
(defn- group-by-arity [methods]
  "Group methods by number of arguments"
  (group-by #(count (nth % 2)) methods))

(defn- type-check-expr [arg-num type-spec]
  (cond (symbol? type-spec)
        `(~'instance? ~(symbol type-spec) ~(symbol (str "arg" arg-num)))

        (and (vector? type-spec) (= :array-of (first type-spec)))
        `(~'array-instance? ~(second type-spec) ~(symbol (str "arg" arg-num)))

        :else (throw (ex-info "Uknown typespec" {:type-spec type-spec}))))

(defn- make-cond-clause [java-fn obj method]
  (let [method-name (first method)
        return-type (second method)
        args (nth method 2)
        conditions (map-indexed
                    (fn [idx [type _]]
                      (type-check-expr (inc idx) type))
                    args)
        arg-names (map-indexed
                   (fn [idx [type name]]
                     [type (symbol (str "arg" (inc idx)))])
                   args)]
    `(~'and ~@conditions)
    `[(~'and ~@conditions)
      (. ~obj ~java-fn ~@(mapcat (fn [[type arg]] [(str type) arg]) arg-names))]))

(defn- make-arity-method [java-fn obj methods arity]
  (let [arg-symbols (map #(symbol (str "arg" (inc %))) (range arity))
        cond-clauses (mapcat #(make-cond-clause java-fn obj %) methods)]
    `([~@arg-symbols]
      (~'cond ~@cond-clauses
       :else (throw (ex-info "Type error"
                             ~(zipmap (map #(symbol (str "arg" (inc %)))
                                           (range arity))
                                      arg-symbols)))))))

(defn- generate-complex-multiple-arity-functions [java-fn java-class defs]
  (let [method-name (ffirst methods)
        grouped (group-by-arity methods)
        arity-methods (map (fn [[arity methods]]
                             (make-arity-method java-fn obj methods arity))
                           grouped)]
    `(~'defn ~method-name
      ~@arity-methods)))

(defn- generate-multy-arity-functions [java-fn java-class defs]
  (if (simple-case? defs)
    (generate-simple-multiple-arity-functions java-fn java-class defs)
    (generate-complex-multiple-arity-functions java-fn java-class  defs)))

(defn generate-functions [java-class [java-fn clj-defs]]
  (if (= (count clj-defs) 1)
    (generate-a-function java-fn java-class (first clj-defs))
    (generate-multy-arity-functions java-fn java-class clj-defs)))

(defn- generate-simple-multiple-arity-functions [java-fn java-class defs]
  (assert (and (apply = (map second defs))) "Return values must be same")
  (let [[fn-name return-type type+args] (first defs)]
    (pp/cl-format nil "(defn ~{~A~} ~A~&~{~A~^~%~})"
                  (when-let [result (canonical-type return-type)]
                    result)
                  fn-name
                  (for [[_ _ type+args] defs
                        :let [[types args] (split-type+args type+args)]]
                    (pp/cl-format nil "~2T([~{~A~^ ~}]~%~2T (. ~{~A~^ ~}))~%"
                                  `(~@(make-main-instance-arg java-class)
                                    ~@(mapcat (fn [type arg]
                                                (list (canonical-type type) arg))
                                              types args))
                                  `(~(str/lower-case java-class)
                                    ~java-fn
                                    ~@args))))))

(comment
  (with-open [out (io/writer "/tmp/ex.clj")]
    (binding [*out* out]
      (doseq [l *1]
        (println l)))))
****  get Document


[(document-get Optional ([ObjectId obj] [String key]))
 (document-get Optional ([ObjectId obj] [int key]))
 (document-get Optional ([ObjectId obj] [String key] [[:array-of ChangeHash] heads]))
 (document-get Optional ([ObjectId obj] [int idx] [[:array-of ChangeHash] heads]))
 ]
