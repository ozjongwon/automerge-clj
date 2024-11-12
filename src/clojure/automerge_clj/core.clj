(ns automerge-clj.core
  (:use [clojure.automerge-clj.automerge-interface])
  (:require [clojure.java.io :as io]
            [clojure.data.codec.base64 :as b64])
  (:import [org.automerge ObjectId ObjectType
            AmValue$UInt AmValue$Int AmValue$Bool AmValue$Bytes AmValue$Str
            AmValue$F64 AmValue$Counter AmValue$Timestamp AmValue$Null AmValue$Unknown
            AmValue$Map AmValue$List AmValue$Text]))

(defmacro with-document-tx [[doc tx & {:keys [free? commit? actor]}] & body]
  `(let [~doc ~(if actor
                 `(make-document ~actor)
                 `(make-document))
         ~tx (document-start-transaction ~doc)]
     (try (do ~@body)
          (finally
            ~@(when commit?
                `((transaction-commit ~tx)))
            ~@(when free?
                `((transaction-rollback ~tx))
                `((document-free ~doc)))))))

(defmacro with-text [[text tx] & body]
  `(let [~text  (transaction-set ~tx ObjectId/ROOT "text" +object-type-text+)]
     ~@body))

(defmacro with-document-content [[tx content-oid type-key key-index] & body]
  ;; type-key = #{:map :text :list}
  `(let [~content-oid (transaction-set ~tx
                                       ObjectId/ROOT
                                       ~key-index
                                       ~(symbol (str "+object-type-" (name type-key) "+")))]
     ~@body))

(defn splice
  ([tx text content]
   (splice tx text content 0 0))
  ([tx text content start]
   (splice tx text content start 0))
  ([tx text content start delete-count]
   (transaction-splice-text tx text start delete-count content)))

(defmacro with-loading-document [[doc doc-bytes & {:keys [free? actor]}] & body]
  ;; REMOVE??
  `(let [~doc (document-load ~doc-bytes)]
     (try (do ~@body)
          (finally
            ~@(when free?
                `((document-free ~doc)))))))

(defmacro with-loading-document-tx [[[doc doc-bytes] tx  & {:keys [free? commit?]}] & body]
  `(let [~doc (document-load ~doc-bytes)
         ~tx (document-start-transaction ~doc)]
     (try (do ~@body)
          (finally
            ~@(when commit?
                `((transaction-commit ~tx)))
            ~@(when free?
                `((transaction-rollback ~tx))
                `((document-free ~doc)))))))

(defn print-documents-text [doc1 doc2 text]
  (println "***\nDoc 1 text:"  (-> (document-text doc1 text) (.get))
           "\nDoc 2 text:" (-> (document-text doc2 text) (.get))))

(defmacro with-tx [[tx doc] & body]
  `(let [~tx (document-start-transaction ~doc)]
     ~@body
     (transaction-commit ~tx)))

(defn b64-encode-str [byte-array]
  (-> (b64/encode byte-array)
      (String.)))

(defn b64-str->decoded-bytes [str]
  (-> (.getBytes str)
      (b64/decode)))


(defn optional->nilable [x]
  (when (.isPresent x)
    (.get x)))


(defn example-1 []
  (let [doc1 (atom nil)
        doc2 (atom nil)]
    (with-document-tx [doc tx :commit? true :free? true]
      (with-document-content [tx text :text "text"]
        (splice tx text "Hello World")
        (reset! doc1 (document-save doc))))
    (with-loading-document [doc (document-load @doc1) :free? true]
      (with-tx [tx doc]
        (splice tx doc " beautiful" 5))
      (reset! doc2 (document-save doc)))
    (list @doc1 @doc2)
    ;; (with-tx [tx @doc1]
    ;;   (splice tx @shared-text " there" 5))
    ;; (print-documents-text @doc1 @doc2 @shared-text)
    ;; (document-merge @doc1 @doc2)
    ;; (print-documents-text @doc1 @doc2 @shared-text)
    ;; (document-merge @doc2 @doc1)
    ;; (print-documents-text @doc1 @doc2 @shared-text)
    ;; (println "Actor -> " (document-get-actor-id @doc1))
    ;; (document-free @doc1)
    ;; (document-free @doc2)
    ))

(defn b64-encode-str [byte-array]
  (-> (b64/encode byte-array)
      (String.)))

(defn b64-str->decoded-bytes [str]
  (-> (.getBytes str)
      (b64/decode)))

(defprotocol CRDT
  (%crdt-get [this args]))

(defn- unwrap-crdt-optional [optional]
  (when-let [val (optional->nilable optional)]
    val))

(defn- hydrate-crdt-value [am-value]
  ((condp = (type am-value)
     AmValue$UInt u-int-get-value
     AmValue$Int int-get-value
     AmValue$Bool bool-get-value
     AmValue$Bytes bytes-get-value
     AmValue$Str str-get-value
     AmValue$F64 f64-get-value
     AmValue$Counter counter-get-value
     AmValue$Timestamp timestamp-get-value
     AmValue$Unknown unknown-get-value)
   am-value))

(defn- crdt-get
  ([^CrdtText crdt]
   (crdt-get crdt nil))
  ([crdt index]
   (-> (%crdt-get crdt index)
       unwrap-crdt-optional
       hydrate-crdt-value)))

(defn- crdt-free [crdt]
  (document-free (:doc crdt))
  nil)

(defrecord CrdtMap [doc id]
  CRDT
  (%crdt-get [this index]
    (document-get doc id index)))

(defrecord CrdtList [doc id]
  CRDT
  (%crdt-get [this index]
    (document-get doc id index)))

(defrecord CrdtText [doc id]
  CRDT
  (%crdt-get [this ignore]
    (document-text doc id)))

(defn- make-crdt-instance [doc am-value]
  (condp =  (type am-value)
    AmValue$Map (->CrdtMap doc (map-get-id am-value))
    AmValue$List (->CrdtList doc (list-get-id am-value))
    AmValue$Text (->CrdtText doc (text-get-id am-value))))

(defn hydrate-crdt-object [doc-b64-str name]
  (let [doc (-> (b64-str->decoded-bytes doc-b64-str)
                (document-load))
        content (-> doc
                    (document-get ObjectId/ROOT name)
                    (optional->nilable))]
    (when content
      (make-crdt-instance doc content))))

(comment
  (let [doc (make-document)
        tx (document-start-transaction doc)]
    (try (let [list (transaction-set tx org.automerge.ObjectId/ROOT "list1" +object-type-list+)]
           (transaction-insert tx list 0 "How to deal with Conflicts??")
           (transaction-commit tx)
           (-> (document-save doc)
               (b64-encode-str)))
         (finally (document-free doc))))

  (let [doc (-> (b64-str->decoded-bytes "hW9Kg6lCVgcApQEBEJkw82CPLkUasRhFQ/UmbK0BgEQFsLdg2T2UX0543xE+0nv1LroTZVP9O5y/M4PQaGgGAQIDAhMCIwJAAlYCCwEEAgQTBBUJIQIjAjQCQgNWBFccgAECfwB/AX8CfwB/AH8HAAF/AAABfwEAAX8AfwVsaXN0MQABAgACAQEBfgIBfgDGA0hvdyB0byBkZWFsIHdpdGggQ29uZmxpY3RzPz8CAAA=")
                (document-load))
        list (-> (document-get doc ObjectId/ROOT "list1")
                 (optional->nilable)
                 (list-get-id))]
    (println "***>>>"
             (->> (document-get-object-type doc  list)
                  (optional->nilable))
             ObjectType/LIST)
    (-> (document-get doc list 0)
        (optional->nilable)
        (str-get-value)))

  (let [doc (make-document)
        tx (document-start-transaction doc)
        new-doc (-> (try (let [list (transaction-set tx org.automerge.ObjectId/ROOT "list1" +object-type-list+)]
                           (transaction-insert tx list 0 "How to deal with Conflicts??")
                           (transaction-commit tx)

                           (let [sd (document-save doc)
                                 loid (->> (document-get doc ObjectId/ROOT "list1")
                                           (optional->nilable)
                                           (list-get-id))]
                             (println "*** "  (-> (document-get doc loid 0)
                                                  str))
                             sd))
                         (finally (document-free doc)))
                    (document-load))
        amval (-> (document-get new-doc ObjectId/ROOT "list1")
                  (optional->nilable))]
    [doc amval])

  (let [doc (make-document)
        tx (document-start-transaction doc)
        new-doc (-> (try (let [list (transaction-set tx org.automerge.ObjectId/ROOT "list1" +object-type-list+)]
                           (transaction-insert tx list 0 "How to deal with Conflicts??")
                           (transaction-commit tx)
                           (println "*** " (-> (document-get doc ObjectId/ROOT "list1")
                                               (optional->nilable)))
                           (document-save doc))
                         (finally (document-free doc)))
                    (document-load))
        amval (-> (document-get new-doc ObjectId/ROOT "list1")
                  (optional->nilable))]
    [doc amval])

  (def doc (first *1))
  (def aml (second *2))
  (document-get doc (list-get-id aml) 0)


  (let [doc (make-document)
        tx (document-start-transaction doc)
        new-doc (-> (try (let [list (transaction-set tx org.automerge.ObjectId/ROOT "list1" +object-type-list+)]
                           (transaction-insert tx list 0 "How to deal with Conflicts??")
                           (transaction-commit tx)
                           (document-save doc))
                         (finally (document-free doc)))
                    (document-load))
        amval (-> (document-get new-doc ObjectId/ROOT "list1")
                  (optional->nilable))]
    [doc amval])

  (defn example-2 []
    (with-document-tx [doc tx]
      (let [todo-list-oid (transaction-set tx ObjectId/ROOT "todos" +object-type-list+)]
        (transaction-insert tx todo-list-oid 0 "Go journey")
        (println "****" (document-get-heads doc))
        (transaction-insert tx todo-list-oid 0 "Go cycling")
        (println "****" (document-get-heads doc)))))

  (def saved (let [alice-doc (make-document)
                   alice-tx (document-start-transaction alice-doc)
                   todo-list (transaction-set alice-tx ObjectId/ROOT "todos" +object-type-list+)
                   bob-doc (make-document)
                   bob-tx (document-start-transaction bob-doc)]
               (try (do
                      (transaction-insert alice-tx todo-list 0 "Buy groceries")
                      (transaction-insert alice-tx todo-list 0 "Call dentist")
                      (transaction-commit alice-tx)
                      (document-save alice-doc)))))

  (example-2)



  (defn b64-str->decoded-bytes [str]
    (-> (.getBytes str)
        (b64/decode)))

  (def saved-str (b64-encode-str saved))
  (println (b64-str->decoded-bytes saved-str) saved)

  (def bob-saved (let [bob-doc (document-load (b64-str->decoded-bytes saved-str))
                       bob-tx (document-start-transaction bob-doc)]
                   (document-get-heads bob-doc)))

  (= (seq (b64-str->decoded-bytes saved-str)) (seq saved))
  )
