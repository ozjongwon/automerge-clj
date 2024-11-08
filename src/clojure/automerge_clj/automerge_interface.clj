;;;
;;; Generated file, do not edit
;;;

(ns clojure.automerge-clj.automerge-interface
        (:import [java.util Optional List HashMap Date Iterator]
                 [org.automerge ObjectId ObjectType ExpandMark
Document Transaction Counter ChangeHash Cursor PatchLog SyncState NewValue]))

(defonce +object-id-root+ ObjectId/ROOT)

(defonce +object-type-map+ ObjectType/MAP)
(defonce +object-type-list+ ObjectType/LIST)
(defonce +object-type-text+ ObjectType/TEXT)

(defonce +expand-mark-before+ ExpandMark/BEFORE)
(defonce +expand-mark-after+ ExpandMark/AFTER)
(defonce +expand-mark-both+ ExpandMark/BOTH)
(defonce +expand-mark-none+ ExpandMark/NONE)


(defn- array-instance? [c o]
  (and (-> o class .isArray)
       (= (-> o class .getComponentType)
          c)))

;;; Class Document

(defn ^Optional document-get-all
  ([^Document document arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.getAll document ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (integer? arg2))
        (.getAll document ^ObjectId arg1 ^Integer arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
  ([^Document document arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
        (.getAll document ^ObjectId arg1 ^String arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (array-instance? ChangeHash arg3))
        (.getAll document ^ObjectId arg1 ^Integer arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn ^Cursor document-make-cursor
  ([^Document document ^ObjectId obj ^Long index]
   (.makeCursor document obj index))
  ([^Document document ^ObjectId obj ^Long index ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.makeCursor document obj index heads)))

(defn ^Document document-fork
  ([^Document document]
   ((.fork document)))
  ([^Document document arg1]
  (cond (bytes? arg1)
        (.fork document ^bytes arg1)
        (array-instance? ChangeHash arg1)
        (.fork document ^"[[[Lorg.automerge.ChangeHash;" arg1)
        :else (throw (ex-info "Type error" {:arg1 arg1}))))
  ([^Document document arg1 arg2]
  (cond (and (array-instance? ChangeHash arg1) (bytes? arg2))
        (.fork document ^"[[[Lorg.automerge.ChangeHash;" arg1 ^bytes arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))

(defn ^List document-make-patches [^Document document ^PatchLog patch-log]
  (.makePatches document patch-log))

(defn ^Transaction document-start-transaction-at [^Document document ^PatchLog patch-log ^"[[[Lorg.automerge.ChangeHash;" heads]
  (.startTransactionAt document patch-log heads))

(defn  document-receive-sync-message
  ([^Document document ^SyncState sync-state ^bytes message]
   (.receiveSyncMessage document sync-state message))
  ([^Document document ^SyncState sync-state ^PatchLog patch-log ^bytes message]
   (.receiveSyncMessage document sync-state patch-log message)))

(defn  document-apply-encoded-changes
  ([^Document document ^bytes changes]
   (.applyEncodedChanges document changes))
  ([^Document document ^bytes changes ^PatchLog patch-log]
   (.applyEncodedChanges document changes patch-log)))

(defn ^"[[[Lorg.automerge.ChangeHash;" document-get-heads [^Document document]
  (.getHeads document))

(defn ^Optional document-map-entries
  ([^Document document ^ObjectId obj]
   (.mapEntries document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.mapEntries document obj heads)))

(defn ^bytes document-get-actor-id [^Document document]
  (.getActorId document))

(defn ^bytes document-save [^Document document]
  (.save document))

(defn ^Optional document-get-object-type [^Document document ^ObjectId obj]
  (.getObjectType document obj))

(defn ^Optional document-get
  ([^Document document arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.get document ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (integer? arg2))
        (.get document ^ObjectId arg1 ^Integer arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
  ([^Document document arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
        (.get document ^ObjectId arg1 ^String arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (array-instance? ChangeHash arg3))
        (.get document ^ObjectId arg1 ^Integer arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn ^Long document-lookup-cursor-index
  ([^Document document ^ObjectId obj ^Cursor cursor]
   (.lookupCursorIndex document obj cursor))
  ([^Document document ^ObjectId obj ^Cursor cursor ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.lookupCursorIndex document obj cursor heads)))

(defn ^List document-diff [^Document document ^"[[[Lorg.automerge.ChangeHash;" before ^"[[[Lorg.automerge.ChangeHash;" after]
  (.diff document before after))

(defn ^Document make-document
  ([]
   (Document.))
  ([^bytes actor-id]
   (Document. actor-id)))

(defn ^Optional document-list-items
  ([^Document document ^ObjectId obj]
   (.listItems document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.listItems document obj heads)))

(defn  document-free [^Document document]
  (.free document))

(defn ^Long document-length
  ([^Document document ^ObjectId obj]
   (.length document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.length document obj heads)))

(defn ^HashMap document-get-marks-at-index
  ([^Document document ^ObjectId obj ^Integer index]
   (.getMarksAtIndex document obj index))
  ([^Document document ^ObjectId obj ^Integer index ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.getMarksAtIndex document obj index heads)))

(defn ^List document-marks
  ([^Document document ^ObjectId obj]
   (.marks document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.marks document obj heads)))

(defn ^Document document-load [^bytes bytes]
  (Document/load bytes))

(defn ^Optional document-generate-sync-message [^Document document ^SyncState sync-state]
  (.generateSyncMessage document sync-state))

(defn ^Optional document-keys
  ([^Document document ^ObjectId obj]
   (.keys document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.keys document obj heads)))

(defn  document-merge
  ([^Document document ^Document other]
   (.merge document other))
  ([^Document document ^Document other ^PatchLog patch-log]
   (.merge document other patch-log)))

(defn ^bytes document-encode-changes-since [^Document document ^"[[[Lorg.automerge.ChangeHash;" heads]
  (.encodeChangesSince document heads))

(defn ^Optional document-text
  ([^Document document ^ObjectId obj]
   (.text document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.text document obj heads)))

(defn ^Transaction document-start-transaction
  ([^Document document]
   (.startTransaction document))
  ([^Document document ^PatchLog patch-log]
   (.startTransaction document patch-log)))

;;; Class Transaction

(defn  transaction-delete
  ([^Transaction transaction arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.delete transaction ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (instance? long arg2))
        (.delete transaction ^ObjectId arg1 ^Long arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))

(defn  transaction-mark-null [^Transaction transaction ^ObjectId obj ^Long start ^Long end ^String mark-name ^ExpandMark expand]
  (.markNull transaction obj start end mark-name expand))

(defn  transaction-increment
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? long arg3))
        (.increment transaction ^ObjectId arg1 ^String arg2 ^Long arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3))
        (.increment transaction ^ObjectId arg1 ^Long arg2 ^Long arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-splice-text [^Transaction transaction ^ObjectId obj ^Long start ^Long delete-count ^String text]
  (.spliceText transaction obj start delete-count text))

(defn  transaction-insert
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? long arg2) (instance? double arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? String arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (integer? arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (bytes? arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? Counter arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^Counter arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? Date arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^Date arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? boolean arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? NewValue arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^NewValue arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-mark-uint [^Transaction transaction ^ObjectId obj ^Long start ^Long end ^String mark-name ^Long value ^ExpandMark expand]
  (.markUint transaction obj start end mark-name value expand))

(defn ^Optional transaction-commit [^Transaction transaction]
  (.commit transaction))

(defn  transaction-close [^Transaction transaction]
  (.close transaction))

(defn ^ObjectId transaction-set
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? ObjectType arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^ObjectType arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? ObjectType arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^ObjectType arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-rollback [^Transaction transaction]
  (.rollback transaction))

(defn  transaction-insert-null [^Transaction transaction ^ObjectId obj ^Long index]
  (.insertNull transaction obj index))

(defn  transaction-set
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? String arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? String arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? double arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? double arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (integer? arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (integer? arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? NewValue arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^NewValue arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? NewValue arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^NewValue arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (bytes? arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (bytes? arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? boolean arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? boolean arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Counter arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Counter arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? Counter arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^Counter arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Date arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Date arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? Date arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^Date arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-unmark [^Transaction transaction ^ObjectId obj ^String mark-name ^Long start ^Long end ^ExpandMark expand]
  (.unmark transaction obj mark-name start end expand))

(defn  transaction-set-uint
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? long arg3))
        (.setUint transaction ^ObjectId arg1 ^String arg2 ^Long arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3))
        (.setUint transaction ^ObjectId arg1 ^Long arg2 ^Long arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-set-null
  ([^Transaction transaction arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.setNull transaction ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (instance? long arg2))
        (.setNull transaction ^ObjectId arg1 ^Long arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))

(defn  transaction-mark
  ([^Transaction transaction arg1 arg2 arg3 arg4 arg5 arg6]
  (cond (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? NewValue arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^NewValue arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? String arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^String arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? long arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Long arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? double arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^double arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (bytes? arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^bytes arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? Counter arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Counter arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? Date arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Date arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? boolean arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Boolean arg5 ^ExpandMark arg6)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3, :arg4 arg4, :arg5 arg5, :arg6 arg6})))))

(defn ^ObjectId transaction-insert [^Transaction transaction ^ObjectId parent ^Long index ^ObjectType obj-type]
  (.insert transaction parent index obj-type))

(defn  transaction-insert-uint [^Transaction transaction ^ObjectId obj ^Long index ^Long value]
  (.insertUint transaction obj index value))

(defn  transaction-splice [^Transaction transaction ^ObjectId obj ^Long start ^Long delete-count ^Iterator items]
  (.splice transaction obj start delete-count items))

;;; Class Counter

(defn ^Counter make-counter [^Long value]
  (Counter. value))

(defn ^Long counter-get-value [^Counter counter]
  (.getValue counter))

(defn ^Integer counter-hash-code [^Counter counter]
  (.hashCode counter))

(defn ^Boolean counter-equals [^Counter counter ^Object obj]
  (.equals counter obj))

;;; Class ChangeHash

(defn ^bytes change-hash-get-bytes [^ChangeHash changehash]
  (.getBytes changehash))

(defn ^Integer change-hash-hash-code [^ChangeHash changehash]
  (.hashCode changehash))

(defn ^Boolean change-hash-equals [^ChangeHash changehash ^Object obj]
  (.equals changehash obj))

;;; Class Cursor

(defn ^Cursor cursor-from-bytes [^bytes encoded]
  (Cursor/fromBytes encoded))

(defn ^Cursor cursor-from-string [^String encoded]
  (Cursor/fromString encoded))

(defn ^String cursor-to-string [^Cursor cursor]
  (.toString cursor))

(defn ^bytes cursor-to-bytes [^Cursor cursor]
  (.toBytes cursor))

;;; Class PatchLog

(defn ^PatchLog make-patch-log []
  (PatchLog.))

(defn  patch-log-free [^PatchLog patchlog]
  (.free patchlog))

;;; Class SyncState

(defn ^SyncState make-sync-state []
  (SyncState.))

(defn ^SyncState sync-state-decode [^bytes encoded]
  (SyncState/decode encoded))

(defn ^bytes sync-state-encode [^SyncState syncstate]
  (.encode syncstate))

(defn  sync-state-free [^SyncState syncstate]
  (.free syncstate))

(defn ^Boolean sync-state-is-in-sync [^SyncState syncstate ^Document doc]
  (.isInSync syncstate doc))

;;; Class NewValue

(defn ^NewValue new-value-uint [^Long value]
  (NewValue/uint value))

(defn ^NewValue new-value-integer [^Long value]
  (NewValue/integer value))

(defn ^NewValue new-value-f64 [^double value]
  (NewValue/f64 value))

(defn ^NewValue new-value-bool [^Boolean value]
  (NewValue/bool value))

(defn ^NewValue new-value-str [^String value]
  (NewValue/str value))

(defn ^NewValue new-value-bytes [^bytes value]
  (NewValue/bytes value))

(defn ^NewValue new-value-counter [^Long value]
  (NewValue/counter value))

(defn ^NewValue new-value-timestamp [^Date value]
  (NewValue/timestamp value))

