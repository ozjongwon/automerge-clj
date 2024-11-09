(ns automerge-clj.core
  (:use [clojure.automerge-clj.automerge-interface])
  (:import [org.automerge ObjectId ObjectType ExpandMark
            Document Transaction ChangeHash Cursor PatchLog SyncState NewValue]))

(defmacro with-document-tx [[doc tx & {:keys [free? actor]}] & body]
  `(let [~doc (if ~actor
                (make-document ~actor)
                (make-document))
         ~tx (document-start-transaction ~doc)]
     ~@body
     (transaction-commit ~tx)
     (when ~free?
       (document-free ~doc))))

(defmacro with-text [[text tx] & body]
  `(let [~text  (transaction-set ~tx +object-id-root+ "text" +object-type-text+)]
     ~@body))

(defn splice
  ([tx text content]
   (splice tx text content 0 0))
  ([tx text content start]
   (splice tx text content start 0))
  ([tx text content start delete-count]
   (transaction-splice-text tx text start delete-count content)))

(defmacro with-loading-document [[doc doc-bytes & {:keys [free? actor]}] & body]
  `(let [~doc (document-load ~doc-bytes)]
     ~@body
     (when ~free?
       (document-free ~doc))))

(defn print-documents-text [doc1 doc2 text]
  (println "***\nDoc 1 text:"  (-> (document-text doc1 text) (.get))
           "\nDoc 2 text:" (-> (document-text doc2 text) (.get))))

(defmacro with-tx [[tx doc] & body]
  `(let [~tx (document-start-transaction ~doc)]
     ~@body
     (transaction-commit ~tx)))


(defn example []
  (let [doc1 (atom nil)
        doc2 (atom nil)
        shared-text (atom nil)]
    (with-document-tx [doc tx]
      (with-text [text tx]
        (splice tx text "Hello World")
        (reset! doc1 doc)
        (reset! shared-text text)))
    (with-loading-document [doc (document-save @doc1)]
      (print-documents-text @doc1 doc @shared-text)
      (with-tx [tx doc]
        (splice tx @shared-text " beautiful" 5))
      (reset! doc2 doc))
    (print-documents-text @doc1 @doc2 @shared-text)
    (with-tx [tx @doc1]
      (splice tx @shared-text " there" 5))
    (print-documents-text @doc1 @doc2 @shared-text)
    (document-merge @doc1 @doc2)
    (print-documents-text @doc1 @doc2 @shared-text)
    (document-merge @doc2 @doc1)
    (print-documents-text @doc1 @doc2 @shared-text)
    (println "Actor -> " (document-get-actor-id @doc1))
    (document-free @doc1)
    (document-free @doc2)))
