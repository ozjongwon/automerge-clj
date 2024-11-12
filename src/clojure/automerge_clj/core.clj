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
  (%crdt-get [this args])
  (%crdt-set [this tx pos-info value]))

(defn- unwrap-crdt-optional [optional]
  (when-let [val (optional->nilable optional)]
    val))

(defn- hydrate-crdt-value [am-value]
  ((condp = (type am-value)
     String identity
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

(defrecord CrdtMap [doc id]
  CRDT
  (%crdt-get [this index]
    (document-get doc id index))
  (%crdt-set [this tx index value]))

(defrecord CrdtList [doc id]
  CRDT
  (%crdt-get [this index]
    (document-get doc id index))
  (%crdt-set [this tx index value]))

(defrecord CrdtText [doc id]
  CRDT
  (%crdt-get [this ignore]
    (document-text doc id))
  (%crdt-set [this tx [start delete-count] value]
    (if (string? value)
      (transaction-splice-text tx id start delete-count value)
      :fixme)))

(defn- crdt-get
  ([^CrdtText crdt]
   (crdt-get crdt nil))
  ([crdt index]
   (-> (%crdt-get crdt index)
       unwrap-crdt-optional
       hydrate-crdt-value)))

(defn- crdt-set [crdt tx pos val]
  (%crdt-set crdt tx pos val))

(defn- crdt-free [crdt]
  (document-free (:doc crdt))
  nil)


(defn- make-crdt-instance
  ([doc am-value]
   (condp =  (type am-value)
     AmValue$Map (->CrdtMap doc (map-get-id am-value))
     AmValue$List (->CrdtList doc (list-get-id am-value))
     AmValue$Text (->CrdtText doc (text-get-id am-value))))
  ([doc k obj-id]
   (case k
     :map (->CrdtMap doc obj-id)
     :list (->CrdtList doc obj-id)
     :text (->CrdtText doc obj-id))))

(defn hydrate-crdt-object [doc-b64-str name]
  (let [doc (-> (b64-str->decoded-bytes doc-b64-str)
                (document-load))
        content (-> doc
                    (document-get ObjectId/ROOT name)
                    (optional->nilable))]
    (when content
      (make-crdt-instance doc content))))

(defn- key->object-type [k]
  (var-get (resolve (symbol (str "+object-type-" (name k) "+")))))

(defn add-crdt-data [doc-or-actor crdt-type crdt-key pos-info val]
  (letfn [(%make-crdt-instance [obj]
            (when obj
              (make-crdt-instance doc obj)))]
    (let [doc (cond (bytes? doc-or-actor) (make-document doc-or-actor) ;; actor
                    (string? doc-or-actor) (-> (b64-str->decoded-bytes doc-or-actor)
                                               (document-load)) ;; restor saved doc
                    :else (make-document))
          tx (document-start-transaction doc)]
      (try (let [crdt-obj (if (string? doc-or-actor) ;; from saved doc
                            (-> doc
                                (document-get ObjectId/ROOT crdt-key)
                                (optional->nilable)
                                (%make-crdt-instance))
                            (->> (key->object-type crdt-type)
                                 (transaction-set tx
                                                  ObjectId/ROOT
                                                  crdt-key)
                                 (make-crdt-instance doc crdt-type)))]
             ;;             (transaction-set tx org.automerge.ObjectId/ROOT "list1" +object-type-list+)
             (crdt-set crdt-obj tx pos-info val)
             (transaction-commit tx)
             (->> (document-save doc)
                  (b64-encode-str)
                  (assoc crdt-obj :doc)))
           (finally (document-free doc))))))

(defn get-crdt-data
  ([doc-or-actor crdt-key] ;; text
   (get-crdt doc-or-actor crdt-key nil))
  ([doc-or-actor crdt-key pos-info]
   (let [doc (cond (bytes? doc-or-actor) (make-document doc-or-actor) ;; actor
                   (string? doc-or-actor) (-> (b64-str->decoded-bytes doc-or-actor)
                                              (document-load)) ;; restor saved doc
                   )
         content (-> doc
                     (document-get ObjectId/ROOT crdt-key)
                     (optional->nilable))]
     (when content
       (-> (make-crdt-instance doc content)
           (crdt-get pos-info))))))

(def test (add-crdt :create :text "hello" [0 0] "world"))

;; (-> (hydrate-crdt-object "hW9Kg6lCVgcApQEBEJkw82CPLkUasRhFQ/UmbK0BgEQFsLdg2T2UX0543xE+0nv1LroTZVP9O5y/M4PQaGgGAQIDAhMCIwJAAlYCCwEEAgQTBBUJIQIjAjQCQgNWBFccgAECfwB/AX8CfwB/AH8HAAF/AAABfwEAAX8AfwVsaXN0MQABAgACAQEBfgIBfgDGA0hvdyB0byBkZWFsIHdpdGggQ29uZmxpY3RzPz8CAAA="
;;                          "list1")
;;     (crdt-get 0))

(comment
  (let [doc (make-document)
        tx (document-start-transaction doc)]
    (try (let [list (transaction-set tx org.automerge.ObjectId/ROOT "list1" +object-type-list+)]
           (transaction-insert tx list 0 "How to deal with Conflicts??")
           (transaction-commit tx)
           (-> (document-save doc)
               (b64-encode-str)))
         (finally (document-free doc))))

  (defn example-2 []
    (with-document-tx [doc tx]
      (let [todo-list-oid (transaction-set tx ObjectId/ROOT "todos" +object-type-list+)]
        (transaction-insert tx todo-list-oid 0 "Go journey")
        (println "****" (document-get-heads doc))
        (transaction-insert tx todo-list-oid 0 "Go cycling")
        (println "****" (document-get-heads doc)))))


  (defmacro with-document-crdt [[[doc & str-or-actor] crdt-type crdt-key pos-info value] & body]
    `(let [~doc (cond (bytes? ~@str-or-actor) (make-document ~@str-or-actor) ;; actor
                      (string? ~@str-or-actor) (-> (b64-str->decoded-bytes ~@str-or-actor)
                                                   (document-load)) ;; restor saved doc
                      :else (make-document))
           tx# (document-start-transaction ~doc)
           crdt-obj# (if (string? ~@str-or-actor) ;; from saved doc
                       (-> ~doc
                           (document-get ObjectId/ROOT ~crdt-key)
                           (optional->nilable)
                           #(when %
                              (make-crdt-instance ~doc %)))
                       (transaction-set tx# org.automerge.ObjectId/ROOT crdt-key
                                        ~(symbol (str "+object-type-" (name crdt-type)))))
           ]
       ~@body))

  (let [x "abc"]
    (with-document-crdt [[doc x] :text "hello" [0 0] "world"]
      1))
  (with-document-tx [doc tx :commit? true :free? true]
    (with-document-content [tx text :text "hello"]


      (splice tx text "Hello World")
      (reset! doc1 (document-save doc))))
  )
