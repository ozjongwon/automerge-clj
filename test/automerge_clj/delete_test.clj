(ns automerge-clj.delete-test
  (:require [clojure.test :refer :all]
            [automerge-clj.core :refer :all]
            [clojure.automerge-clj.automerge-interface :refer :all]))

(def root-doc (atom nil))
(def root-tx (atom nil))

(defn setup-fixture [f]
  (reset! root-doc (make-document))
  (reset! root-tx (document-start-transaction @root-doc))
  (f)
  ;;  (document-free @root-doc)
  (reset! root-doc nil)
  (reset! root-tx nil))

(use-fixtures :each setup-fixture)

(deftest delete-in-map-test
  (transaction-set @root-tx org.automerge.ObjectId/ROOT "key" (new-value-counter 10))
  (transaction-delete @root-tx org.automerge.ObjectId/ROOT "key")
  (is (nil? (-> (document-get @root-doc org.automerge.ObjectId/ROOT "key")
                (optional->nilable)))))

(deftest delete-in-list-test
  (let [list (transaction-set @root-tx org.automerge.ObjectId/ROOT "list" org.automerge.ObjectType/LIST)]
    (transaction-insert @root-tx list 0 123)
    (transaction-delete @root-tx list 0)
    (is (nil? (-> (document-get @root-doc list 0)
                  (optional->nilable))))))



;;(run-tests)
