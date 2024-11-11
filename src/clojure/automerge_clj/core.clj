(ns automerge-clj.core
  (:use [clojure.automerge-clj.automerge-interface])
  (:require [clojure.java.io :as io]
            [clojure.data.codec.base64 :as b64])
  (:import [org.automerge ObjectId ObjectType ExpandMark Document Transaction
            ChangeHash Cursor PatchLog SyncState NewValue AmValue$List]))

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
  (-> (b64/encode saved)
      (String.)))

(defn b64-str->decoded-bytes [str]
  (-> (.getBytes str)
      (b64/decode)))


(defn optional->nilable [x]
  (when (.isPresent x)
    (.get x)))

(-> (let [doc (make-document)
          tx (document-start-transaction doc)]
      (try (let [text (transaction-set tx org.automerge.ObjectId/ROOT "text" +object-type-text+)]
             (splice tx text "Hello World")
             (transaction-commit tx)
             (document-save doc))
           (finally (document-free doc))))

    (document-load)
    (document-get ObjectId/ROOT "text")
    (optional->nilable))

(let [doc (make-document)
      tx (document-start-transaction doc)
      lobj (-> (try (let [list (transaction-set tx org.automerge.ObjectId/ROOT "list1" +object-type-list+)]
                      (transaction-insert tx list 0 "How to deal with Conflicts??")
                      (transaction-commit tx)
                      (document-save doc))
                    (finally (document-free doc)))
               (document-load)
               (document-get ObjectId/ROOT "list1")
               (optional->nilable)
               (.getId))]
  (document-get doc lobj 0))




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

(example-1)

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

(defn b64-encode-str [byte-array]
  (-> (b64/encode saved)
      (String.)))

(defn b64-str->decoded-bytes [str]
  (-> (.getBytes str)
      (b64/decode)))

(def saved-str (b64-encode-str saved))
(println (b64-str->decoded-bytes saved-str) saved)

(def bob-saved (let [bob-doc (document-load (b64-str->decoded-bytes saved-str))
                     bob-tx (document-start-transaction bob-doc)]
                 (document-get-heads bob-doc)))

(= (seq (b64-str->decoded-bytes saved-str)) (seq saved))
