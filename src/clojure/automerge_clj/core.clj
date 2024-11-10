(ns automerge-clj.core
  (:use [clojure.automerge-clj.automerge-interface])
  (:require [clojure.java.io :as io]
            [clojure.data.codec.base64 :as b64])
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

(defn example1 []
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

(defn example-2 []
  (with-document-tx [doc tx]
    (let [todo-list-oid (transaction-set tx +object-id-root+ "todos" +object-type-list+)]
      (transaction-insert tx todo-list-oid 0 "Go journey")
      (println "****" (document-get-heads doc))
      (transaction-insert tx todo-list-oid 0 "Go cycling")
      (println "****" (document-get-heads doc)))))

(def saved (let [alice-doc (make-document)
                 alice-tx (document-start-transaction alice-doc)
                 todo-list (transaction-set alice-tx +object-id-root+ "todos" +object-type-list+)
                 bob-doc (make-document)
                 bob-tx (document-start-transaction bob-doc)]
             (try (do
                    (transaction-insert alice-tx todo-list 0 "Buy groceries")
                    (transaction-insert alice-tx todo-list 0 "Call dentist")
                    (transaction-commit alice-tx)
                    (document-save alice-doc)))))

(def saved-str (b64-encode-str saved))

(def bob-saved (let [bob-doc (document-load (b64-str->decoded-bytes saved-str))
                     bob-tx (document-start-transaction bob-doc)]
                 (document-get-heads bob-doc)))

(defn b64-encode-str [byte-array]
  (-> (b64/encode saved)
      (String.)))

(defn b64-str->decoded-bytes [str]
  (-> (.getBytes str)
      (b64/decode)))

;; saved == byte array
(def encoded (b64/encode saved))

(def b64-encoded (b64/encode saved))
(def b64-str (String. b64-encoded))
(.getBytes b64-str) ;; back to b64-encoded
(b64/decode b64-encoded)


(def original-string "Hello, Clojure!")

;; Step 1: Convert the string to bytes (UTF-8)
(def byte-array (.getBytes original-string "UTF-8"))

;; Step 2: Base64 encode the byte array
(def encoded (b64/encode byte-array))

(def encoded-str (String. encoded))

(.getBytes encoded-str "UTF-8")
(def decoded-bytes (b64/decode encoded))

(def decoded-string (String. decoded-bytes "UTF-8"))
