(ns automerge-clj.cursor-test
  (:require [clojure.test :refer :all]
            [automerge-clj.core :refer :all]
            [clojure.automerge-clj.automerge-interface :refer :all]))

(def root-doc (atom nil))
(def root-text (atom nil))

(defn setup-fixture [f]
  (let [doc (make-document)
        tx (document-start-transaction doc)
        text (try (let [text (transaction-set tx org.automerge.ObjectId/ROOT "text" org.automerge.ObjectType/TEXT)]
                    (transaction-splice-text tx text 0 0 "hellow world")
                    text)
                  (finally (transaction-commit tx)))]

    (reset! root-doc doc)
    (reset! root-text text)
    (f)
    (document-free @root-doc)
    (reset! root-doc nil)
    (reset! root-text nil)))

(use-fixtures :each setup-fixture)

(deftest cursor-in-doc-test
  (let [cursor (document-make-cursor @root-doc @root-text 3)]
    (is (= 3 (document-lookup-cursor-index @root-doc @root-text cursor)))
    (let [heads (document-get-heads @root-doc)
          tx (document-start-transaction @root-doc)]
      (try (transaction-splice-text tx @root-text 3 0 "!")
           (finally (transaction-commit tx)))
      (is (= 4 (document-lookup-cursor-index @root-doc @root-text cursor)))
      (is (= 3 (document-lookup-cursor-index @root-doc @root-text cursor heads)))
      (let [old-cursor (document-make-cursor @root-doc @root-text 3 heads)]
        (is (= 4 (document-lookup-cursor-index @root-doc @root-text old-cursor)))
        (is (= 3 (document-lookup-cursor-index @root-doc @root-text old-cursor heads)))))))

(deftest to-from-string-test
  (let [cursor (document-make-cursor @root-doc @root-text 3)
        encoded (str cursor)
        decoded (cursor-from-string encoded)]
    (is (= (document-lookup-cursor-index @root-doc @root-text decoded) 3))
    (is (thrown? IllegalArgumentException (cursor-from-string "invalid")))))

(deftest to-from-bytes-test
  (let [cursor (document-make-cursor @root-doc @root-text 3)
        encoded (cursor-to-bytes cursor)
        decoded (cursor-from-bytes encoded)]
    (is (= (document-lookup-cursor-index @root-doc @root-text decoded) 3))
    (is (thrown? IllegalArgumentException (->> [1 2 3]
                                               byte-array
                                               bytes
                                               cursor-from-bytes)))))


;;(run-tests)
