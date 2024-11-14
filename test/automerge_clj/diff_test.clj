(ns automerge-clj.diff-test
  (:require [clojure.test :refer :all]
            [automerge-clj.core :refer :all]
            [clojure.automerge-clj.automerge-interface :refer :all]))

(def first-heads (atom nil))
(def second-heads (atom nil))

(deftest transaction-at-test
  (let [doc (make-document)
        tx (document-start-transaction doc)]
    (transaction-set tx org.automerge.ObjectId/ROOT "key" 1.23)
    (reset! first-heads (-> tx
                            transaction-commit
                            optional->nilable))

    (let [tx (document-start-transaction doc)]
      (transaction-set tx org.automerge.ObjectId/ROOT "key" 4.56)
      (reset! second-heads (-> tx
                               transaction-commit
                               optional->nilable))
      ;; A single element array in Java - new ChangeHash[]{firstHeads}
      (let [patches (->> (document-diff doc
                                        (into-array org.automerge.ChangeHash [@first-heads])
                                        (into-array org.automerge.ChangeHash [@second-heads]))
                         (into []))]
        (is (= (count patches) 1))
        ;; FIXME: add generic 'patch-action-get-value' and 'patch-action-get-key'
        (let [action (->> (first patches)
                          (patch-get-action))]
          (is (= (patch-action-get-key action) "key"))
          (is (= (->> (patch-action-get-value action)
                      (am-value-get-value)) 4.56)))))))

;;(run-tests)
