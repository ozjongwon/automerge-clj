(ns clojure.automerge-clj.generate-interface
  (:require [clojure.data.priority-map :refer [priority-map]]
            [clojure.set :as set])
  (:import [org.automerge AutomergeSys Document ObjectId ChangeHash ObjectType
            PatchLog]
           [java.util Optional]))

(defn init-lib []
  (Document.) ;; Native lib init happens
  ObjectId/ROOT ;; Access lazy field
  )

(defn- simple-case? [defs]
  (let [num-arg-list (map #(count (second (rest %))) defs)]
    (or (empty? num-arg-list)
        (apply = num-arg-list))))

(defn- generate-a-function [def]
  `(~'defn ~@def))

;; (clojure.core/defn
;;   document-generate-sync-message
;;   Optional
;;   ([SyncState sync-state]))
(defn- generate-simple-multiple-arity-functions [defs]
  defs)

(defn- generate-complex-multiple-arity-functions [defs]
  :defs)

(defn- generate-multy-arity-functions [defs]
  `(~'defn ~(first (first defs))
    ~(if (simple-case? defs)
       (generate-simple-multiple-arity-functions defs)
       (generate-complex-multiple-arity-functions defs))))

(defn generate-functions [[java-fn clj-defs]]
  (if (= (count clj-defs) 1)
    (generate-a-function (first clj-defs))
    (generate-multy-arity-functions clj-defs)))

(defn geenrate-clojre-interface [filename]
  "Reads the file, processes each line, and returns a map of function names to their Clojure definitions."
  (with-open [reader (clojure.java.io/reader filename)]  ; Open the file for reading
    (let [reader (java.io.PushbackReader. reader)]
      (->> (loop [k nil result {}]
             (let [exp (read reader false :eof)]
               (cond (= :eof exp) result
                     (= '=> exp) (recur k result)
                     (symbol? exp) (recur exp (update result exp (fnil identity [])))
                     (list? exp) (recur k (update result k conj exp)))))
           (map generate-functions)))))




