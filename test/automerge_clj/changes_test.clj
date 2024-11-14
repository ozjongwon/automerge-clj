(ns automerge-clj.changes-test
  (:require [clojure.test :refer :all]
            [automerge-clj.core :refer :all]
            [clojure.automerge-clj.automerge-interface :refer :all]))

;; (deftest a-test
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

(deftest changes-test
  (let [doc (make-document)
        tx (document-start-transaction doc)]
    (try (transaction-set tx org.automerge.ObjectId/ROOT "key" "value")
         (finally (transaction-commit tx)))
    (let [heads (document-get-heads doc)
          doc2 (document-fork doc)
          tx (document-start-transaction doc2)]
      (try (transaction-set tx org.automerge.ObjectId/ROOT "key" "value2")
           (finally (transaction-commit tx)))
      (let [changes (document-encode-changes-since doc2 heads)]
        (document-apply-encoded-changes doc changes)
        (testing "Merge doc2 changes into doc, values are same"
          (is (= "value2"
                 (-> (document-get doc org.automerge.ObjectId/ROOT "key")
                     (optional->nilable)
                     (am-value-get-value)))))))))

;;(run-tests)
