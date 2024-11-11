(ns clojure.automerge-clj.automerge-interface
        (:import [java.util Optional List HashMap Date Iterator]
                 [org.automerge ObjectId ObjectType ExpandMark
Document Transaction ChangeHash Cursor PatchLog SyncState NewValue AmValue]))

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

(defn ^Document make-document
  ([]
   (Document.))
  ([^bytes actor-id]
   (Document. actor-id)))

(defn ^Optional document-generate-sync-message [^Document document ^SyncState sync-state]
  (.generateSyncMessage document sync-state))

(defn ^Optional document-text
  ([^Document document ^ObjectId obj]
   (.text document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.text document obj heads)))

(defn ^List document-diff [^Document document ^"[[[Lorg.automerge.ChangeHash;" before ^"[[[Lorg.automerge.ChangeHash;" after]
  (.diff document before after))

(defn ^Long document-lookup-cursor-index
  ([^Document document ^ObjectId obj ^Cursor cursor]
   (.lookupCursorIndex document obj cursor))
  ([^Document document ^ObjectId obj ^Cursor cursor ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.lookupCursorIndex document obj cursor heads)))

(defn ^Document document-load [^bytes bytes]
  (Document/load bytes))

(defn ^Optional document-get-object-type [^Document document ^ObjectId obj]
  (.getObjectType document obj))

(defn  document-apply-encoded-changes
  ([^Document document ^bytes changes]
   (.applyEncodedChanges document changes))
  ([^Document document ^bytes changes ^PatchLog patch-log]
   (.applyEncodedChanges document changes patch-log)))

(defn ^Optional document-get-all
  ([^Document document arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.getAll document ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (instance? Integer arg2))
        (.getAll document ^ObjectId arg1 ^Integer arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
  ([^Document document arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
        (.getAll document ^ObjectId arg1 ^String arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        (and (instance? ObjectId arg1) (instance? Integer arg2) (array-instance? ChangeHash arg3))
        (.getAll document ^ObjectId arg1 ^Integer arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn ^Optional document-get
  ([^Document document arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.get document ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (instance? Integer arg2))
        (.get document ^ObjectId arg1 ^Integer arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
  ([^Document document arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
        (.get document ^ObjectId arg1 ^String arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        (and (instance? ObjectId arg1) (instance? Integer arg2) (array-instance? ChangeHash arg3))
        (.get document ^ObjectId arg1 ^Integer arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn ^Transaction document-start-transaction
  ([^Document document]
   (.startTransaction document))
  ([^Document document ^PatchLog patch-log]
   (.startTransaction document patch-log)))

(defn ^bytes document-encode-changes-since [^Document document ^"[[[Lorg.automerge.ChangeHash;" heads]
  (.encodeChangesSince document heads))

(defn ^"[[[Lorg.automerge.ChangeHash;" document-get-heads [^Document document]
  (.getHeads document))

(defn  document-free [^Document document]
  (.free document))

(defn ^Transaction document-start-transaction-at [^Document document ^PatchLog patch-log ^"[[[Lorg.automerge.ChangeHash;" heads]
  (.startTransactionAt document patch-log heads))

(defn ^bytes document-save [^Document document]
  (.save document))

(defn  document-merge
  ([^Document document ^Document other]
   (.merge document other))
  ([^Document document ^Document other ^PatchLog patch-log]
   (.merge document other patch-log)))

(defn ^Optional document-map-entries
  ([^Document document ^ObjectId obj]
   (.mapEntries document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.mapEntries document obj heads)))

(defn ^Optional document-keys
  ([^Document document ^ObjectId obj]
   (.keys document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.keys document obj heads)))

(defn ^Optional document-list-items
  ([^Document document ^ObjectId obj]
   (.listItems document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.listItems document obj heads)))

(defn ^List document-make-patches [^Document document ^PatchLog patch-log]
  (.makePatches document patch-log))

(defn ^bytes document-get-actor-id [^Document document]
  (.getActorId document))

(defn ^Long document-length
  ([^Document document ^ObjectId obj]
   (.length document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.length document obj heads)))

(defn ^List document-marks
  ([^Document document ^ObjectId obj]
   (.marks document obj))
  ([^Document document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.marks document obj heads)))

(defn ^HashMap document-get-marks-at-index
  ([^Document document ^ObjectId obj ^Integer index]
   (.getMarksAtIndex document obj index))
  ([^Document document ^ObjectId obj ^Integer index ^"[[[Lorg.automerge.ChangeHash;" heads]
   (.getMarksAtIndex document obj index heads)))

(defn  document-receive-sync-message
  ([^Document document ^SyncState sync-state ^bytes message]
   (.receiveSyncMessage document sync-state message))
  ([^Document document ^SyncState sync-state ^PatchLog patch-log ^bytes message]
   (.receiveSyncMessage document sync-state patch-log message)))
;;; Class Transaction


(defn ^ObjectId transaction-set
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? String arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? String arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Double arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Double arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Integer arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Integer arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? NewValue arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^NewValue arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? NewValue arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^NewValue arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (bytes? arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (bytes? arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Boolean arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Boolean arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Date arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Date arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Date arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^Date arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? ObjectType arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^ObjectType arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? ObjectType arg3))
        (.set transaction ^ObjectId arg1 ^Long arg2 ^ObjectType arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-rollback [^Transaction transaction]
  (.rollback transaction))

(defn  transaction-mark-uint [^Transaction transaction ^ObjectId obj ^Long start ^Long end ^String mark-name ^Long value ^ExpandMark expand]
  (.markUint transaction obj start end mark-name value expand))

(defn  transaction-mark-null [^Transaction transaction ^ObjectId obj ^Long start ^Long end ^String mark-name ^ExpandMark expand]
  (.markNull transaction obj start end mark-name expand))

(defn ^ObjectId transaction-insert
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Double arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^double arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? String arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^String arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Integer arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^Integer arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (bytes? arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Date arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^Date arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Boolean arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^Boolean arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? NewValue arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^NewValue arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? ObjectType arg3))
        (.insert transaction ^ObjectId arg1 ^Long arg2 ^ObjectType arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-increment
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? Long arg3))
        (.increment transaction ^ObjectId arg1 ^String arg2 ^Long arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3))
        (.increment transaction ^ObjectId arg1 ^Long arg2 ^Long arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-splice [^Transaction transaction ^ObjectId obj ^Long start ^Long delete-count ^Iterator items]
  (.splice transaction obj start delete-count items))

(defn  transaction-splice-text [^Transaction transaction ^ObjectId obj ^Long start ^Long delete-count ^String text]
  (.spliceText transaction obj start delete-count text))

(defn  transaction-mark
  ([^Transaction transaction arg1 arg2 arg3 arg4 arg5 arg6]
  (cond (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3) (instance? String arg4) (instance? NewValue arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^NewValue arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3) (instance? String arg4) (instance? String arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^String arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3) (instance? String arg4) (instance? Long arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Long arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3) (instance? String arg4) (instance? Double arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^double arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3) (instance? String arg4) (bytes? arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^bytes arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3) (instance? String arg4) (instance? Date arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Date arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3) (instance? String arg4) (instance? Boolean arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long arg2 ^Long arg3 ^String arg4 ^Boolean arg5 ^ExpandMark arg6)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3, :arg4 arg4, :arg5 arg5, :arg6 arg6})))))

(defn  transaction-insert-null [^Transaction transaction ^ObjectId obj ^Long index]
  (.insertNull transaction obj index))

(defn  transaction-delete
  ([^Transaction transaction arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.delete transaction ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (instance? Long arg2))
        (.delete transaction ^ObjectId arg1 ^Long arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))

(defn  transaction-set-uint
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? Long arg3))
        (.setUint transaction ^ObjectId arg1 ^String arg2 ^Long arg3)
        (and (instance? ObjectId arg1) (instance? Long arg2) (instance? Long arg3))
        (.setUint transaction ^ObjectId arg1 ^Long arg2 ^Long arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))

(defn  transaction-close [^Transaction transaction]
  (.close transaction))

(defn  transaction-insert-uint [^Transaction transaction ^ObjectId obj ^Long index ^Long value]
  (.insertUint transaction obj index value))

(defn  transaction-unmark [^Transaction transaction ^ObjectId obj ^String mark-name ^Long start ^Long end ^ExpandMark expand]
  (.unmark transaction obj mark-name start end expand))

(defn  transaction-set-null
  ([^Transaction transaction arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.setNull transaction ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (instance? Long arg2))
        (.setNull transaction ^ObjectId arg1 ^Long arg2)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))

(defn ^Optional transaction-commit [^Transaction transaction]
  (.commit transaction))
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
;;; Class AmValue


(defn ^Long u-int-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^Long int-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^Boolean bool-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^bytes bytes-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^String str-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^double f64-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^Long counter-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^Date timestamp-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^bytes unknown-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^String u-int-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String int-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String bool-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String bytes-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String str-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String f64-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String counter-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String timestamp-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String null-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String unknown-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String map-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String list-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^String text-to-string [^AmValue amvalue]
  (.toString amvalue))

(defn ^Integer unknown-get-type-code [^AmValue amvalue]
  (.getTypeCode amvalue))

(defn ^ObjectId map-get-id [^AmValue amvalue]
  (.getId amvalue))

(defn ^ObjectId list-get-id [^AmValue amvalue]
  (.getId amvalue))

(defn ^ObjectId text-get-id [^AmValue amvalue]
  (.getId amvalue))
