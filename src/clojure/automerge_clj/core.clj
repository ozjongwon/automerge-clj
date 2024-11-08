(ns automerge-clj.core
  (:use [clojure.automerge-clj.automerge-interface]))

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
   (print "***" (type tx) (type text) (type content )(type start) (type delete-count))
   (transaction-splice tx text start delete-count content)))

(let [doc1 (atom nil)
      doc2 (atom nil)
      shared-text (atom nil)]
  (with-document-tx [doc tx :free? true]
    (with-text [text tx]
      (splice tx text "Hello World")
      (reset! doc1 doc)
      (reset! shared-text text))))

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
    (println "Actor -> " (document-actor @doc1))
    (document-free @doc1)
    (document-free @doc2)))














(defn document-save [doc]
  (.save doc))

(defn document-load [^bytes doc]
  (Document/load doc))

(defn document-text [^Document doc text]
  (-> (.text doc text)
      .get))

(defn document-get
  ([^Document doc ^ObjectId object-id key]
   (cond (string? key) (.get doc object-id ^String key)
         (integer? key) (.get doc object-id ^int key)
         :else (throw (ex-info "Unsupported type" {:type (type key)}))))
  ([^Document doc ^ObjectId object-id key-or-index ^"[[[Lorg.automerge.ChangeHash;" heads]
   (cond (string? key) (.get doc object-id ^String key-or-index ^String key)
         (integer? key) (.get doc object-id ^int key-or-index ^int key)
         :else (throw (ex-info "Unsupported type" {:type (type key-or-index)})))))

(defn document-get-all []
  :fixme)


(defn document-merge [doc1 doc2]
  (if (instance? Document doc2)
    (.merge ^Document doc1 ^Document doc2)
    (.merge ^Document doc1 ^PatchLog doc2)))

(defn document-free [doc]
  (.free doc))

(defn document-actor [doc]
  (.getActorId doc))

(defn- bytes-array? [x]
  (instance? (Class/forName "[B") x))

(defn document-fork
  ([doc]
   (.fork doc))
  ([doc actor-or-change-hash]
   (if (bytes-array? actor-or-change-hash)
     (.fork ^bytes actor-or-change-hash)
     ;;  Type Hint (.getName (class (make-array ChangeHash 0 0 0)))
     (.fork ^"[[[Lorg.automerge.ChangeHash;" actor-or-change-hash))))

(defn encode-changes-since [^Document doc ^"[[[Lorg.automerge.ChangeHash;" heads]
  (.encodeChangesSince doc heads))

(defn apply-encoded-changes
  ([^Document doc ^bytes changes]
   (.applyEncodedChanges doc changes))
  ([^Document doc ^bytes changes ^PatchLog patch-log]
   (.applyEncodedChanges doc changes patch-log)))




(defmacro with-loading-document [[doc doc-bytes & {:keys [free? actor]}] & body]
  `(let [~doc (document-load ~doc-bytes)]
     ~@body
     (when ~free?
       (document-free ~doc))))

(defmacro with-tx [[tx doc] & body]
  `(let [~tx (.startTransaction ~doc)]
     ~@body
     (.commit ~tx)))

(defn init-lib []
  (with-document-tx [doc tx]
    (with-text [text tx]
      (splice tx text "Hello World"))))


(defn print-documents-text [doc1 doc2 text]
  (println "***\nDoc 1 text:" (document-text doc1 text)
           "\nDoc 2 text:" (document-text doc2 text)))

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
    (println "Actor -> " (document-actor @doc1))
    (document-free @doc1)
    (document-free @doc2)))

(example)

(comment

  ;; Data structures

  (defrecord Change [actor seq op key value start end])
  (defrecord Clock [actor seq])
  (defrecord Doc [changes clock values conflicts])

  ;; Actor ID generation
  (defn generate-actor-id []
    (str (java.util.UUID/randomUUID)))

  ;; Initialize new document
  (defn init []
    (->Doc #{} {} {} {}))

  ;; Vector clock operations
  (defn increment-clock [clock actor]
    (update clock actor (fnil inc 0)))

  (defn compare-clocks [c1 c2]
    (cond
      (= c1 c2) 0
      (every? (fn [[k v]] (<= (get c2 k 0) v)) c1) 1
      (every? (fn [[k v]] (<= (get c1 k 0) v)) c2) -1
      :else :concurrent))

  ;; Change handling
  (defn make-change [actor seq op key value & [start end]]
    (->Change actor seq op key value start end))

  (defn apply-change [doc change]
    (let [{:keys [actor seq op key value start end]} change
          current-seq (get-in doc [:clock actor] 0)]
      (if (> seq current-seq)
        (-> doc
            (update :changes conj change)
            (assoc-in [:clock actor] seq)
            (cond->
                (= op :set) (assoc-in [:values key] value)
                (= op :delete) (update :values dissoc key)
                (= op :insert) (update-in [:values key]
                                          #(if (vector? %)
                                             (into (into [] (take start %))
                                                   (concat [value] (drop start %)))
                                             [value]))
                (= op :list-delete) (update-in [:values key]
                                               #(into (into [] (take start %))
                                                      (drop (inc end) %))))))))

  ;; Conflict resolution
  (defn resolve-conflicts [changes]
    (reduce (fn [acc change]
              (let [{:keys [actor seq op key value]} change]
                (case op
                  :set (if-let [existing (get acc key)]
                         (if (pos? (compare [seq actor]
                                            [(get-in existing [:seq])
                                             (get-in existing [:actor])]))
                           (assoc acc key change)
                           acc)
                         (assoc acc key change))
                  acc)))
            {}
            (sort-by (juxt :seq :actor) changes)))

  ;; Public API
  (defn new-doc []
    (let [actor (generate-actor-id)]
      {:doc (init)
       :actor actor}))

  (defn set-value [{:keys [doc actor] :as state} key value]
    (let [seq (inc (get-in doc [:clock actor] 0))
          change (make-change actor seq :set key value)]
      (update state :doc apply-change change)))

  (defn get-value [{:keys [doc]} key]
    (get-in doc [:values key]))

  (defn insert [{:keys [doc actor] :as state} key value index]
    (let [seq (inc (get-in doc [:clock actor] 0))
          change (make-change actor seq :insert key value index)]
      (update state :doc apply-change change)))

  (defn delete [{:keys [doc actor] :as state} key]
    (let [seq (inc (get-in doc [:clock actor] 0))
          change (make-change actor seq :delete key nil)]
      (update state :doc apply-change change)))

  ;; Merging documents
  (defn merge-docs [doc1 doc2]
    (reduce apply-change
            doc1
            (sort-by (juxt :seq :actor)
                     (set/difference (:changes doc2) (:changes doc1)))))

  ;; History tracking
  (defn get-history [doc]
    (->> (:changes doc)
         (sort-by (juxt :seq :actor))
         (map #(select-keys % [:actor :seq :op :key :value]))))

  ;; Example usage:
  (comment
    ;; Create two replicas
    (def replica1 (new-doc))
    (def replica2 (new-doc))

    ;; Make concurrent changes
    (def replica1 (-> replica1
                      (set-value "name" "Alice")
                      (set-value "age" 30)))

    (def replica2 (-> replica2
                      (set-value "name" "Bob")
                      (set-value "location" "NYC")))

    ;; Merge changes
    (def merged (merge-docs (:doc replica1) (:doc replica2)))

    ;; Check values
    (get-value {:doc merged} "name")    ;; Returns last-write-wins value
    (get-value {:doc merged} "age")     ;; Returns 30
    (get-value {:doc merged} "location") ;; Returns "NYC"
    )
  )
