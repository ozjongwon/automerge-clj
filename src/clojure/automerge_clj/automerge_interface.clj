;;;
;;; Generated file, do not edit
;;;

(ns clojure.automerge-clj.automerge-interface
        (:import [java.util Optional List HashMap Date Iterator ArrayList]
                 [org.automerge ObjectId ObjectType ExpandMark
Document Transaction ChangeHash Cursor PatchLog SyncState NewValue AmValue ObjectId Patch PatchAction Mark Prop]))

(defonce +object-type-map+ ObjectType/MAP)
(defonce +object-type-list+ ObjectType/LIST)
(defonce +object-type-text+ ObjectType/TEXT)

(defonce +expand-mark-before+ ExpandMark/BEFORE)
(defonce +expand-mark-after+ ExpandMark/AFTER)
(defonce +expand-mark-both+ ExpandMark/BOTH)
(defonce +expand-mark-none+ ExpandMark/NONE)

(defn array-instance? [c o]
  (and (-> o class .isArray)
       (= (-> o class .getComponentType)
          c)))

;;; Class Document


(defn ^Cursor document-make-cursor
  ([^Document document obj index]
   (.makeCursor document ^ObjectId obj ^Long (long index)))
  ([^Document document obj index heads]
   (.makeCursor document ^ObjectId obj ^Long (long index) ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn ^Document document-fork
  ([^Document document]
   (.fork document))
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
nil

(defn ^Document make-document
  ([]
   (Document.))
  ([actor-id]
   (Document. ^bytes actor-id)))
nil

(defn ^Optional document-generate-sync-message [^Document document sync-state]
  (.generateSyncMessage document ^SyncState sync-state))
nil

(defn ^Optional document-text
  ([^Document document obj]
   (.text document ^ObjectId obj))
  ([^Document document obj heads]
   (.text document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn ^List document-diff [^Document document before after]
  (.diff document ^"[[[Lorg.automerge.ChangeHash;" before ^"[[[Lorg.automerge.ChangeHash;" after))
nil

(defn ^Long document-lookup-cursor-index
  ([^Document document obj cursor]
   (.lookupCursorIndex document ^ObjectId obj ^Cursor cursor))
  ([^Document document obj cursor heads]
   (.lookupCursorIndex document ^ObjectId obj ^Cursor cursor ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn ^Document document-load [bytes]
  (Document/load ^bytes bytes))
nil

(defn ^Optional document-get-object-type [^Document document obj]
  (.getObjectType document ^ObjectId obj))
nil

(defn  document-apply-encoded-changes
  ([^Document document changes]
   (.applyEncodedChanges document ^bytes changes))
  ([^Document document changes patch-log]
   (.applyEncodedChanges document ^bytes changes ^PatchLog patch-log)))
nil

(defn ^Optional document-get-all
  ([^Document document arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.getAll document ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (integer? arg2))
        (.getAll document ^ObjectId arg1 ^Integer (int arg2))
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
  ([^Document document arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
        (.getAll document ^ObjectId arg1 ^String arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (array-instance? ChangeHash arg3))
        (.getAll document ^ObjectId arg1 ^Integer (int arg2) ^"[[[Lorg.automerge.ChangeHash;" arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))
nil

(defn ^Optional document-get
  ([^Document document arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.get document ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (integer? arg2))
        (.get document ^ObjectId arg1 ^Integer (int arg2))
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2}))))
  ([^Document document arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (array-instance? ChangeHash arg3))
        (.get document ^ObjectId arg1 ^String arg2 ^"[[[Lorg.automerge.ChangeHash;" arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (array-instance? ChangeHash arg3))
        (.get document ^ObjectId arg1 ^Integer (int arg2) ^"[[[Lorg.automerge.ChangeHash;" arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))
nil

(defn ^Transaction document-start-transaction
  ([^Document document]
   (.startTransaction document))
  ([^Document document patch-log]
   (.startTransaction document ^PatchLog patch-log)))
nil

(defn ^bytes document-encode-changes-since [^Document document heads]
  (.encodeChangesSince document ^"[[[Lorg.automerge.ChangeHash;" heads))
nil

(defn ^"[[[Lorg.automerge.ChangeHash;" document-get-heads [^Document document]
  (.getHeads document))
nil

(defn  document-free [^Document document]
  (.free document))
nil

(defn ^Transaction document-start-transaction-at [^Document document patch-log heads]
  (.startTransactionAt document ^PatchLog patch-log ^"[[[Lorg.automerge.ChangeHash;" heads))
nil

(defn ^bytes document-save [^Document document]
  (.save document))
nil

(defn  document-merge
  ([^Document document other]
   (.merge document ^Document other))
  ([^Document document other patch-log]
   (.merge document ^Document other ^PatchLog patch-log)))
nil

(defn ^Optional document-map-entries
  ([^Document document obj]
   (.mapEntries document ^ObjectId obj))
  ([^Document document obj heads]
   (.mapEntries document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn ^Optional document-keys
  ([^Document document obj]
   (.keys document ^ObjectId obj))
  ([^Document document obj heads]
   (.keys document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn ^Optional document-list-items
  ([^Document document obj]
   (.listItems document ^ObjectId obj))
  ([^Document document obj heads]
   (.listItems document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn ^List document-make-patches [^Document document patch-log]
  (.makePatches document ^PatchLog patch-log))
nil

(defn ^bytes document-get-actor-id [^Document document]
  (.getActorId document))
nil

(defn ^Long document-length
  ([^Document document obj]
   (.length document ^ObjectId obj))
  ([^Document document obj heads]
   (.length document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn ^List document-marks
  ([^Document document obj]
   (.marks document ^ObjectId obj))
  ([^Document document obj heads]
   (.marks document ^ObjectId obj ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn ^HashMap document-get-marks-at-index
  ([^Document document obj index]
   (.getMarksAtIndex document ^ObjectId obj ^Integer (int index)))
  ([^Document document obj index heads]
   (.getMarksAtIndex document ^ObjectId obj ^Integer (int index) ^"[[[Lorg.automerge.ChangeHash;" heads)))
nil

(defn  document-receive-sync-message
  ([^Document document sync-state message]
   (.receiveSyncMessage document ^SyncState sync-state ^bytes message))
  ([^Document document sync-state patch-log message]
   (.receiveSyncMessage document ^SyncState sync-state ^PatchLog patch-log ^bytes message)))
nil
;;; Class Transaction


(defn ^ObjectId transaction-set
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (instance? String arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^String arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (instance? String arg3))
        (.set transaction ^ObjectId arg1 ^Long (long arg2) ^String arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (double? arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Double (double arg3))
        (and (instance? ObjectId arg1) (integer? arg2) (double? arg3))
        (.set transaction ^ObjectId arg1 ^Long (long arg2) ^Double (double arg3))
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3))
        (.set transaction ^ObjectId arg1 ^Long (long arg2) ^Integer (int arg3))
        (and (instance? ObjectId arg1) (instance? String arg2) (integer? arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Integer (int arg3))
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? NewValue arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^NewValue arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (instance? NewValue arg3))
        (.set transaction ^ObjectId arg1 ^Long (long arg2) ^NewValue arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (bytes? arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^bytes arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (bytes? arg3))
        (.set transaction ^ObjectId arg1 ^Long (long arg2) ^bytes arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (boolean? arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Boolean (boolean arg3))
        (and (instance? ObjectId arg1) (integer? arg2) (boolean? arg3))
        (.set transaction ^ObjectId arg1 ^Long (long arg2) ^Boolean (boolean arg3))
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? Date arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^Date arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (instance? Date arg3))
        (.set transaction ^ObjectId arg1 ^Long (long arg2) ^Date arg3)
        (and (instance? ObjectId arg1) (instance? String arg2) (instance? ObjectType arg3))
        (.set transaction ^ObjectId arg1 ^String arg2 ^ObjectType arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (instance? ObjectType arg3))
        (.set transaction ^ObjectId arg1 ^Long (long arg2) ^ObjectType arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))
nil

(defn  transaction-rollback [^Transaction transaction]
  (.rollback transaction))
nil

(defn  transaction-mark-uint [^Transaction transaction obj start end mark-name value expand]
  (.markUint transaction ^ObjectId obj ^Long (long start) ^Long (long end) ^String mark-name ^Long (long value) ^ExpandMark expand))
nil

(defn  transaction-mark-null [^Transaction transaction obj start end mark-name expand]
  (.markNull transaction ^ObjectId obj ^Long (long start) ^Long (long end) ^String mark-name ^ExpandMark expand))
nil

(defn ^ObjectId transaction-insert
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (integer? arg2) (double? arg3))
        (.insert transaction ^ObjectId arg1 ^Long (long arg2) ^Double (double arg3))
        (and (instance? ObjectId arg1) (integer? arg2) (instance? String arg3))
        (.insert transaction ^ObjectId arg1 ^Long (long arg2) ^String arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3))
        (.insert transaction ^ObjectId arg1 ^Long (long arg2) ^Integer (int arg3))
        (and (instance? ObjectId arg1) (integer? arg2) (bytes? arg3))
        (.insert transaction ^ObjectId arg1 ^Long (long arg2) ^bytes arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (instance? Date arg3))
        (.insert transaction ^ObjectId arg1 ^Long (long arg2) ^Date arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (boolean? arg3))
        (.insert transaction ^ObjectId arg1 ^Long (long arg2) ^Boolean (boolean arg3))
        (and (instance? ObjectId arg1) (integer? arg2) (instance? NewValue arg3))
        (.insert transaction ^ObjectId arg1 ^Long (long arg2) ^NewValue arg3)
        (and (instance? ObjectId arg1) (integer? arg2) (instance? ObjectType arg3))
        (.insert transaction ^ObjectId arg1 ^Long (long arg2) ^ObjectType arg3)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))
nil

(defn  transaction-increment
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (integer? arg3))
        (.increment transaction ^ObjectId arg1 ^String arg2 ^Long (long arg3))
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3))
        (.increment transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3))
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))
nil

(defn  transaction-splice [^Transaction transaction obj start delete-count items]
  (.splice transaction ^ObjectId obj ^Long (long start) ^Long (long delete-count) ^Iterator items))
nil

(defn  transaction-splice-text [^Transaction transaction obj start delete-count text]
  (.spliceText transaction ^ObjectId obj ^Long (long start) ^Long (long delete-count) ^String text))
nil

(defn  transaction-mark
  ([^Transaction transaction arg1 arg2 arg3 arg4 arg5 arg6]
  (cond (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3) (instance? String arg4) (instance? NewValue arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3) ^String arg4 ^NewValue arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3) (instance? String arg4) (instance? String arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3) ^String arg4 ^String arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3) (instance? String arg4) (integer? arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3) ^String arg4 ^Long (long arg5) ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3) (instance? String arg4) (double? arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3) ^String arg4 ^Double (double arg5) ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3) (instance? String arg4) (bytes? arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3) ^String arg4 ^bytes arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3) (instance? String arg4) (instance? Date arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3) ^String arg4 ^Date arg5 ^ExpandMark arg6)
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3) (instance? String arg4) (boolean? arg5) (instance? ExpandMark arg6))
        (.mark transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3) ^String arg4 ^Boolean (boolean arg5) ^ExpandMark arg6)
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3, :arg4 arg4, :arg5 arg5, :arg6 arg6})))))
nil

(defn  transaction-insert-null [^Transaction transaction obj index]
  (.insertNull transaction ^ObjectId obj ^Long (long index)))
nil

(defn  transaction-delete
  ([^Transaction transaction arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.delete transaction ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (integer? arg2))
        (.delete transaction ^ObjectId arg1 ^Long (long arg2))
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))
nil

(defn  transaction-set-uint
  ([^Transaction transaction arg1 arg2 arg3]
  (cond (and (instance? ObjectId arg1) (instance? String arg2) (integer? arg3))
        (.setUint transaction ^ObjectId arg1 ^String arg2 ^Long (long arg3))
        (and (instance? ObjectId arg1) (integer? arg2) (integer? arg3))
        (.setUint transaction ^ObjectId arg1 ^Long (long arg2) ^Long (long arg3))
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2, :arg3 arg3})))))
nil

(defn  transaction-close [^Transaction transaction]
  (.close transaction))
nil

(defn  transaction-insert-uint [^Transaction transaction obj index value]
  (.insertUint transaction ^ObjectId obj ^Long (long index) ^Long (long value)))
nil

(defn  transaction-unmark [^Transaction transaction obj mark-name start end expand]
  (.unmark transaction ^ObjectId obj ^String mark-name ^Long (long start) ^Long (long end) ^ExpandMark expand))
nil

(defn  transaction-set-null
  ([^Transaction transaction arg1 arg2]
  (cond (and (instance? ObjectId arg1) (instance? String arg2))
        (.setNull transaction ^ObjectId arg1 ^String arg2)
        (and (instance? ObjectId arg1) (integer? arg2))
        (.setNull transaction ^ObjectId arg1 ^Long (long arg2))
        :else (throw (ex-info "Type error" {:arg1 arg1, :arg2 arg2})))))
nil

(defn ^Optional transaction-commit [^Transaction transaction]
  (.commit transaction))
nil
;;; Class ChangeHash


(defn ^bytes change-hash-get-bytes [^ChangeHash changehash]
  (.getBytes changehash))
nil

(defn ^Integer change-hash-hash-code [^ChangeHash changehash]
  (.hashCode changehash))
nil

(defn ^Boolean change-hash-equals [^ChangeHash changehash obj]
  (.equals changehash ^Object obj))
nil
;;; Class Cursor


(defn ^Cursor cursor-from-bytes [encoded]
  (Cursor/fromBytes ^bytes encoded))
nil

(defn ^Cursor cursor-from-string [encoded]
  (Cursor/fromString ^String encoded))
nil

(defn ^String cursor-to-string [^Cursor cursor]
  (.toString cursor))
nil

(defn ^bytes cursor-to-bytes [^Cursor cursor]
  (.toBytes cursor))
nil
;;; Class PatchLog


(defn ^PatchLog make-patch-log []
  (PatchLog.))
nil

(defn  patch-log-free [^PatchLog patchlog]
  (.free patchlog))
nil
;;; Class SyncState


(defn ^SyncState make-sync-state []
  (SyncState.))
nil

(defn ^SyncState sync-state-decode [encoded]
  (SyncState/decode ^bytes encoded))
nil

(defn ^bytes sync-state-encode [^SyncState syncstate]
  (.encode syncstate))
nil

(defn  sync-state-free [^SyncState syncstate]
  (.free syncstate))
nil

(defn ^Boolean sync-state-is-in-sync [^SyncState syncstate doc]
  (.isInSync syncstate ^Document doc))
nil
;;; Class NewValue


(defn ^NewValue new-value-uint [value]
  (NewValue/uint ^Long (long value)))
nil

(defn ^NewValue new-value-integer [value]
  (NewValue/integer ^Long (long value)))
nil

(defn ^NewValue new-value-f64 [value]
  (NewValue/f64 ^Double (double value)))
nil

(defn ^NewValue new-value-bool [value]
  (NewValue/bool ^Boolean (boolean value)))
nil

(defn ^NewValue new-value-str [value]
  (NewValue/str ^String value))
nil

(defn ^NewValue new-value-bytes [value]
  (NewValue/bytes ^bytes value))
nil

(defn ^NewValue new-value-counter [value]
  (NewValue/counter ^Long (long value)))
nil

(defn ^NewValue new-value-timestamp [value]
  (NewValue/timestamp ^Date value))
nil
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

(defn ^Double f64-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^Long counter-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^Date timestamp-get-value [^AmValue amvalue]
  (.getValue amvalue))

(defn ^bytes unknown-get-value [^AmValue amvalue]
  (.getValue amvalue))
nil

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
nil

(defn ^Integer unknown-get-type-code [^AmValue amvalue]
  (.getTypeCode amvalue))
nil

(defn ^ObjectId map-get-id [^AmValue amvalue]
  (.getId amvalue))

(defn ^ObjectId list-get-id [^AmValue amvalue]
  (.getId amvalue))

(defn ^ObjectId text-get-id [^AmValue amvalue]
  (.getId amvalue))
nil
;;; Class ObjectId


(defn ^Boolean object-id-is-root [^ObjectId objectid]
  (.isRoot objectid))
nil

(defn ^String object-id-to-string [^ObjectId objectid]
  (.toString objectid))
nil

(defn ^Integer object-id-hash-code [^ObjectId objectid]
  (.hashCode objectid))
nil

(defn ^Boolean object-id-equals [^ObjectId objectid obj]
  (.equals objectid ^Object obj))
nil
;;; Class Patch


(defn ^ObjectId patch-get-obj [^Patch patch]
  (.getObj patch))
nil

(defn ^ArrayList patch-get-path [^Patch patch]
  (.getPath patch))
nil

(defn ^PatchAction patch-get-action [^Patch patch]
  (.getAction patch))
nil
;;; Class PatchAction


(defn ^Long delete-list-get-length [^PatchAction patchaction]
  (.getLength patchaction))
nil

(defn ^String splice-text-get-text [^PatchAction patchaction]
  (.getText patchaction))
nil

(defn ^String put-map-get-key [^PatchAction patchaction]
  (.getKey patchaction))

(defn ^String delete-map-get-key [^PatchAction patchaction]
  (.getKey patchaction))
nil

(defn ^AmValue put-map-get-value [^PatchAction patchaction]
  (.getValue patchaction))

(defn ^AmValue put-list-get-value [^PatchAction patchaction]
  (.getValue patchaction))

(defn ^Long increment-get-value [^PatchAction patchaction]
  (.getValue patchaction))
nil

(defn ^Boolean put-map-is-conflict [^PatchAction patchaction]
  (.isConflict patchaction))

(defn ^Boolean put-list-is-conflict [^PatchAction patchaction]
  (.isConflict patchaction))
nil

(defn ^Prop increment-get-property [^PatchAction patchaction]
  (.getProperty patchaction))

(defn ^Prop flag-conflict-get-property [^PatchAction patchaction]
  (.getProperty patchaction))
nil

(defn ^Long put-list-get-index [^PatchAction patchaction]
  (.getIndex patchaction))

(defn ^Long insert-get-index [^PatchAction patchaction]
  (.getIndex patchaction))

(defn ^Long splice-text-get-index [^PatchAction patchaction]
  (.getIndex patchaction))

(defn ^Long delete-list-get-index [^PatchAction patchaction]
  (.getIndex patchaction))
nil

(defn ^"[[[Lorg.automerge.Mark;" mark-get-marks [^PatchAction patchaction]
  (.getMarks patchaction))
nil

(defn ^ArrayList insert-get-values [^PatchAction patchaction]
  (.getValues patchaction))
nil
;;; Class Mark


(defn ^Long mark-get-start [^Mark mark]
  (.getStart mark))
nil

(defn ^Long mark-get-end [^Mark mark]
  (.getEnd mark))
nil

(defn ^String mark-get-name [^Mark mark]
  (.getName mark))
nil

(defn ^AmValue mark-get-value [^Mark mark]
  (.getValue mark))
nil

(defn ^String mark-to-string [^Mark mark]
  (.toString mark))
nil
;;; Class Prop


(defn ^Prop make-prop
  ([^Prop prop arg1]
  (cond (instance? String arg1)
        (.Prop prop ^String arg1)
        (integer? arg1)
        (.Prop prop ^Long (long arg1))
        :else (throw (ex-info "Type error" {:arg1 arg1})))))
nil

(defn ^String key-get-value [^Prop prop]
  (.getValue prop))

(defn ^Long index-get-value [^Prop prop]
  (.getValue prop))
nil

(defn ^Integer key-hash-code [^Prop prop]
  (.hashCode prop))

(defn ^Integer index-hash-code [^Prop prop]
  (.hashCode prop))
nil

(defn ^Boolean key-equals [^Prop prop obj]
  (.equals prop ^Object obj))

(defn ^Boolean index-equals [^Prop prop obj]
  (.equals prop ^Object obj))
nil
