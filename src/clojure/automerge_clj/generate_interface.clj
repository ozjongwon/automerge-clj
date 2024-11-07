(ns clojure.automerge-clj.generate-interface
  (:require [clojure.data.priority-map :refer [priority-map]]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.java.io :as io])
  (:import [org.automerge AutomergeSys Document ObjectId ChangeHash ObjectType
            PatchLog SyncState Transaction]
           [java.util Optional List]))

(defn init-lib []
  (Document.) ;; Native lib init happens
  ObjectId/ROOT ;; Access lazy field
  )

(defn- simple-case? [defs]
  (let [num-arg-list (map #(count (nth %  2)) defs)]
    (or (empty? num-arg-list)
        (apply = num-arg-list))))

(defn- split-type+args [type+args]
  (loop [[[type arg] & more-defs] type+args types [] args []]
    (cond (nil? type) [types args]
          :else
          (recur more-defs (conj types type) (conj args arg)))))

(defn- canonical-type [type]
  (cond (nil? type) nil

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
    (with-out-str
      (pp/cl-format true "(defn ~{~A~} ~A [~{~A~^ ~}]~%~2T(. ~{~A~^ ~}))~2&"
                    (when-let [result (canonical-type return-type)]
                      result)
                    fn-name
                    `(~@(make-main-instance-arg java-class)
                      ~@(mapcat (fn [type arg]
                                  (list (canonical-type type) arg))
                                types args))
                    `(~(str/lower-case java-class)
                      ~java-fn
                      ~@args)))))

;; (clojure.core/defn
;;   document-generate-sync-message
;;   Optional
;;   ([SyncState sync-state]))
(defn- generate-simple-multiple-arity-functions [java-fn java-class defs]
  defs)

(defn- generate-complex-multiple-arity-functions [java-fn java-class defs]
  :defs)

(defn- generate-multy-arity-functions [java-fn java-class defs]
  `(~'defn ~(first (first defs))
    ~(if (simple-case? defs)
       (generate-simple-multiple-arity-functions java-fn java-class defs)
       (generate-complex-multiple-arity-functions java-fn java-class  defs))))

(defn generate-functions [java-class [java-fn clj-defs]]
  (if (= (count clj-defs) 1)
    (generate-a-function java-fn java-class (first clj-defs))
    (generate-multy-arity-functions java-fn java-class clj-defs)))

(defn generate-clojre-interface [filename]
  "Reads the file, processes each line, and returns a map of function names to their Clojure definitions."
  (with-open [reader (clojure.java.io/reader filename)]  ; Open the file for reading
    (let [reader (java.io.PushbackReader. reader)
          [class-var def-map]
          (loop [k nil result {} class-var nil]
            (let [exp (read reader false :eof)]
              (cond (= :eof exp) [class-var result]
                    (= '=> exp) (recur k result class-var)
                    (symbol? exp) (recur exp 
                                         (update result exp (fnil identity []))
                                         class-var)
                    (list? exp) 
                    (if class-var
                      (recur k (update result k conj exp) class-var)
                      (let [[fname return-type _] exp
                            [make class] (str/split (name fname)  #"\-")]
                        (if (and (= make "make")
                                 (= class (str/lower-case return-type)))
                          (recur k (update result k conj exp) return-type)
                          (recur k (update result k conj exp) class-var)))))))]
      (map #(generate-functions class-var %) def-map))))




