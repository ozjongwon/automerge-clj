;;;
;;; Generated file, do not edit
;;;

(ns clojure.automerge-clj.automerge-interface
        (:import [java.util Optional List HashMap Date Iterator]
                 [org.automerge Document Transaction ObjectId ChangeHash PatchLog Cursor SyncState PatchLog ExpandMark ObjectType NewValue]))

(defn- array-instance? [c o]
  (and (-> o class .isArray)
       (= (-> o class .getComponentType)
          c)))

(defn ^NewValue new-value-uint [^Long value]
  (NewValue/uint value))

(defn ^NewValue new-value-integer [^Long value]
  (NewValue/integer value))

(defn ^NewValue new-value-f64 [^Double value]
  (NewValue/f64 value))

(defn ^NewValue new-value-bool [^Boolean value]
  (NewValue/bool value))

(defn ^NewValue new-value-str [^String value]
  (NewValue/str value))

(defn ^NewValue new-value-bytes [^bytes value]
  (NewValue/bytes value))

(defn ^NewValue new-value-counter [^Long value]
  (NewValue/bytes value))

(defn ^NewValue new-value-timestamp [^Date value]
  (NewValue/timestamp value))

;;; Class Document

(defn ^Optional document-get-all
  ([^Document document arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (. document getAll ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (integer? arg2))
        (. document getAll ^ObjectId arg1 ^Integer arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
  ([^Document document arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
        (. document getAll ^ObjectId arg1 ^String arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (array-instance? ChangeHash arg3))
        (. document getAll ^ObjectId arg1 ^Integer arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn ^Cursor document-make-cursor
  ([^Document document ^ObjectId obj ^Long index]
   (. document makeCursor obj index))
  ([^Document document ^ObjectId obj ^Long index ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document makeCursor obj index heads)))

(defn ^Document document-fork
  ([^Document document]
   (. document fork))
  ([^Document document arg1]
  (cond (bytes? arg1)
        (. document fork ^bytes arg1)
        (array-instance? ChangeHash arg1)
        (. document fork ^"[[[Lorg.automerge.ChangeHash;" arg1)
        :else (throw (ex-info "Type error" {:arg1 arg1}))))
  ([^Document document arg1 arg2]
  (cond (and (array-instance? ChangeHash arg1) (bytes? arg2))
        (. document fork ^"[[[Lorg.automerge.ChangeHash;" arg1 ^bytes arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))

(defn ^List document-make-patches [^Document document ^PatchLog patch-log]
  (. document makePatches patch-log))

(defn ^Transaction document-start-transaction-at [^Document document ^PatchLog patch-log ^"[[[Lorg.automerge.ChangeHash;" heads]
  (. document startTransactionAt patch-log heads))

(defn  document-receive-sync-message
  ([^Document document ^SyncState sync-state ^bytes message]
   (. document receiveSyncMessage sync-state message))
  ([^Document document ^SyncState sync-state ^PatchLog patch-log ^bytes message]
   (. document receiveSyncMessage sync-state patch-log message)))

(defn  document-apply-encoded-changes
  ([^Document document ^bytes changes]
   (. document applyEncodedChanges changes))
  ([^Document document ^bytes changes ^PatchLog patch-log]
   (. document applyEncodedChanges changes patch-log)))

(defn ^"[[[Lorg.automerge.ChangeHash;" document-get-heads [^Document document]
  (. document getHeads))

(defn ^Optional document-map-entries
  ([^Document document ^ObjectId obj]
   (. document mapEntries obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document mapEntries obj heads)))

(defn ^bytes document-get-actor-id [^Document document]
  (. document getActorId))

(defn ^bytes document-save [^Document document]
  (. document save))

(defn ^Optional document-get-object-type [^Document document ^ObjectId obj]
  (. document getObjectType obj))

(defn ^Optional document-get
  ([^Document document arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (. document get ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (integer? arg2))
        (. document get ^ObjectId arg1 ^Integer arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
  ([^Document document arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
        (. document get ^ObjectId arg1 ^String arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (array-instance? ChangeHash arg3))
        (. document get ^ObjectId arg1 ^Integer arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn ^Long document-lookup-cursor-index
  ([^Document document ^ObjectId obj ^Cursor cursor]
   (. document lookupCursorIndex obj cursor))
  ([^Document document ^ObjectId obj ^Cursor cursor ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document lookupCursorIndex obj cursor heads)))

(defn ^List document-diff [^Document document ^"[[[Lorg.automerge.ChangeHash;" before ^"[[[Lorg.automerge.ChangeHash;" after]
  (. document diff before after))

(defn ^Document make-document
  ([^Document document]
   (. document Document))
  ([^Document document ^bytes actor-id]
   (. document Document actor-id)))

(defn ^Optional document-list-items
  ([^Document document ^ObjectId obj]
   (. document listItems obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document listItems obj heads)))

(defn  document-free [^Document document]
  (. document free))

(defn ^Long document-length
  ([^Document document ^ObjectId obj]
   (. document length obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document length obj heads)))

(defn ^HashMap document-get-marks-at-index
  ([^Document document ^ObjectId obj ^Integer index]
   (. document getMarksAtIndex obj index))
  ([^Document document ^ObjectId obj ^Integer index ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document getMarksAtIndex obj index heads)))

(defn ^List document-marks
  ([^Document document ^ObjectId obj]
   (. document marks obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document marks obj heads)))

(defn ^Document document-load [^Document document ^bytes bytes]
  (. document load bytes))

(defn ^Optional document-generate-sync-message [^Document document ^SyncState sync-state]
  (. document generateSyncMessage sync-state))

(defn ^Optional document-keys
  ([^Document document ^ObjectId obj]
   (. document keys obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document keys obj heads)))

(defn  document-merge
  ([^Document document ^Document other]
   (. document merge other))
  ([^Document document ^Document other ^PatchLog patch-log]
   (. document merge other patch-log)))

(defn ^bytes document-encode-changes-since [^Document document ^"[[[Lorg.automerge.ChangeHash;" heads]
  (. document encodeChangesSince heads))

(defn ^Optional document-text
  ([^Document document ^ObjectId obj]
   (. document text obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (. document text obj heads)))

(defn ^Transaction document-start-transaction
  ([^Document document]
   (. document startTransaction))
  ([^Document document ^PatchLog patch-log]
   (. document startTransaction patch-log)))

;;; Class Transaction

(defn  transaction-delete
  ([^Transaction transaction arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (. transaction delete ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (instance? long arg2))
        (. transaction delete ^ObjectId arg1 ^Long arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))

(defn  transaction-mark-null [^Transaction transaction ^ObjectId obj ^Long start ^Long end ^String mark-name ^ExpandMark expand]
  (. transaction markNull obj start end mark-name expand))

(defn  transaction-increment
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? long arg3))
        (. transaction increment ^ObjectId arg1 ^String arg2 ^Long arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3))
        (. transaction increment ^ObjectId arg1 ^Long arg2 ^Long arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-splice-text [^Transaction transaction ^ObjectId obj ^Long start ^Long delete-count ^String text]
  (. transaction spliceText obj start delete-count text))

(defn  transaction-insert
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? long arg2) (instance? double arg3))
        (. transaction insert ^ObjectId arg1 ^Long arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? String arg3))
        (. transaction insert ^ObjectId arg1 ^Long arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (integer? arg3))
        (. transaction insert ^ObjectId arg1 ^Long arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (bytes? arg3))
        (. transaction insert ^ObjectId arg1 ^Long arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? Counter arg3))
        (. transaction insert ^ObjectId arg1 ^Long arg2 ^Counter arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? Date arg3))
        (. transaction insert ^ObjectId arg1 ^Long arg2 ^Date arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? boolean arg3))
        (. transaction insert ^ObjectId arg1 ^Long arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? NewValue arg3))
        (. transaction insert ^ObjectId arg1 ^Long arg2 ^NewValue arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-mark-uint [^Transaction transaction ^ObjectId obj ^Long start ^Long end ^String mark-name ^Long value ^ExpandMark expand]
  (. transaction markUint obj start end mark-name value expand))

(defn ^Optional transaction-commit [^Transaction transaction]
  (. transaction commit))

(defn  transaction-close [^Transaction transaction]
  (. transaction close))

(defn ^ObjectId transaction-set
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? ObjectType arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^ObjectType arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? ObjectType arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^ObjectType arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-rollback [^Transaction transaction]
  (. transaction rollback))

(defn  transaction-insert-null [^Transaction transaction ^ObjectId obj ^Long index]
  (. transaction insertNull obj index))

(defn  transaction-set
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? String arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? String arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? double arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? double arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (integer? arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (integer? arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? NewValue arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^NewValue arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? NewValue arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^NewValue arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (bytes? arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (bytes? arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? boolean arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? boolean arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Counter arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^Counter arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? Counter arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^Counter arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Date arg3))
        (. transaction set ^ObjectId arg1 ^String arg2 ^Date arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? Date arg3))
        (. transaction set ^ObjectId arg1 ^Long arg2 ^Date arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-unmark [^Transaction transaction ^ObjectId obj ^String mark-name ^Long start ^Long end ^ExpandMark expand]
  (. transaction unmark obj mark-name start end expand))

(defn  transaction-set-uint
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? long arg3))
        (. transaction setUint ^ObjectId arg1 ^String arg2 ^Long arg3)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3))
        (. transaction setUint ^ObjectId arg1 ^Long arg2 ^Long arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-set-null
  ([^Transaction transaction arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (. transaction setNull ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (instance? long arg2))
        (. transaction setNull ^ObjectId arg1 ^Long arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))

(defn  transaction-mark
  ([^Transaction transaction arg1 arg2 arg3 arg4 arg5 arg6]
  (cond (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? NewValue arg5) (instance? ExpandMark arg6))
        (. transaction mark ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^NewValue arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? String arg5) (instance? ExpandMark arg6))
        (. transaction mark ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^String arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? long arg5) (instance? ExpandMark arg6))
        (. transaction mark ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Long arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? double arg5) (instance? ExpandMark arg6))
        (. transaction mark ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^double arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (bytes? arg5) (instance? ExpandMark arg6))
        (. transaction mark ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^bytes arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? Counter arg5) (instance? ExpandMark arg6))
        (. transaction mark ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Counter arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? Date arg5) (instance? ExpandMark arg6))
        (. transaction mark ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Date arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? long arg2) (instance? long arg3) (instance? String arg4) (instance? boolean arg5) (instance? ExpandMark arg6))
        (. transaction mark ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Boolean arg5 ^ExpandMark arg6)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3, :arg4 arg4, :arg5 arg5, :arg6 arg6})))))

(defn ^ObjectId transaction-insert [^Transaction transaction ^ObjectId parent ^Long index ^ObjectType obj-type]
  (. transaction insert parent index obj-type))

(defn  transaction-insert-uint [^Transaction transaction ^ObjectId obj ^Long index ^Long value]
  (. transaction insertUint obj index value))

(defn  transaction-splice [^Transaction transaction ^ObjectId obj ^Long start ^Long delete-count ^Iterator items]
  (. transaction splice obj start delete-count items))

;;; Class ObjectId

(defn ^Boolean object-id-is-root [^ObjectId objectid]
  (. objectid isRoot))

(defn ^String object-id-to-string [^ObjectId objectid]
  (. objectid toString))

(defn ^Integer object-id-hash-code [^ObjectId objectid]
  (. objectid hashCode))

(defn ^Boolean object-id-equals [^ObjectId objectid ^Object obj]
  (. objectid equals obj))

;;; Class ChangeHash

(defn ^bytes change-hash-get-bytes [^ChangeHash changehash]
  (. changehash getBytes))

(defn ^Integer change-hash-hash-code [^ChangeHash changehash]
  (. changehash hashCode))

(defn ^Boolean change-hash-equals [^ChangeHash changehash ^Object obj]
  (. changehash equals obj))

;;; Class PatchLog

(defn ^PatchLog make-patch-log [^PatchLog patchlog]
  (. patchlog PatchLog))

(defn  patch-log-free [^PatchLog patchlog]
  (. patchlog free))

;;; Class Cursor

(defn ^Cursor cursor-from-bytes [^Cursor cursor ^bytes encoded]
  (. cursor fromBytes encoded))

(defn ^Cursor cursor-from-string [^Cursor cursor ^String encoded]
  (. cursor fromString encoded))

(defn ^String cursor-to-string [^Cursor cursor]
  (. cursor toString))

(defn ^bytes cursor-to-bytes [^Cursor cursor]
  (. cursor toBytes))

;;; Class SyncState

(defn ^SyncState make-sync-state [^SyncState syncstate]
  (. syncstate SyncState))

(defn ^SyncState sync-state-decode [^SyncState syncstate ^bytes encoded]
  (. syncstate decode encoded))

(defn ^bytes sync-state-encode [^SyncState syncstate]
  (. syncstate encode))

(defn  sync-state-free [^SyncState syncstate]
  (. syncstate free))

(defn ^Boolean sync-state-is-in-sync [^SyncState syncstate ^Document doc]
  (. syncstate isInSync doc))

;;; Class PatchLog

(defn ^PatchLog make-patch-log [^PatchLog patchlog]
  (. patchlog PatchLog))

(defn  patch-log-free [^PatchLog patchlog]
  (. patchlog free))


