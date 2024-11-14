;;;
;;; Generated file, do not edit
;;;

(ns clojure.automerge-clj.automerge-interface
      (:import [java.util Optional List HashMap Date Iterator ArrayList]
               [org.automerge ObjectType ExpandMark
                Document Transaction Counter ChangeHash Cursor PatchLog SyncState NewValue AmValue ObjectId Patch PatchAction Mark Prop NewValue$UInt NewValue$Null NewValue$Bytes NewValue$F64 NewValue$Str NewValue$Int NewValue$Timestamp NewValue$Bool NewValue$Counter AmValue$Str AmValue$Counter AmValue$Int AmValue$Unknown AmValue$Bytes AmValue$List AmValue$Text AmValue$F64 AmValue$UInt AmValue$Null AmValue$Map AmValue$Timestamp AmValue$Bool PatchAction$DeleteList PatchAction$SpliceText PatchAction$PutMap PatchAction$DeleteMap PatchAction$FlagConflict PatchAction$Mark PatchAction$Insert PatchAction$PutList PatchAction$Increment Prop$Index Prop$Key]))

(defonce +object-type-map+ ObjectType/MAP)
(defonce +object-type-list+ ObjectType/LIST)
(defonce +object-type-text+ ObjectType/TEXT)

(defonce +expand-mark-before+ ExpandMark/BEFORE)
(defonce +expand-mark-after+ ExpandMark/AFTER)
(defonce +expand-mark-both+ ExpandMark/BOTH)
(defonce +expand-mark-none+ ExpandMark/NONE)

(defn short? [n]
  (and (int? n)
       (<= Short/MIN_VALUE n Short/MAX_VALUE)))

(defn long? [n]
  (and (int? n)
       (or (< Integer/MAX_VALUE n)
           (> Integer/MIN_VALUE n))))

(defn array-instance? [c o]
  (and (-> o class .isArray)
       (= (-> o class .getComponentType)
          c)))

(defn prop-equals
   ([prop arg0]
   (and (instance? Prop$Key prop) (instance? Object arg0)) 
   ^boolean (.equals ^Prop$Key prop ^Object arg0) 
   (and (instance? Prop$Index prop) (instance? Object arg0)) 
   ^boolean (.equals ^Prop$Index prop ^Object arg0)))

(defn mark-get-start
   ([mark]
   ^long (.getStart ^Mark mark)))

(defn document-fork
   ([document]
   ^Document (.fork ^Document document)))

(defn transaction-mark
   ([transaction arg0 arg1 arg2 arg3 arg4 arg5]
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2) (instance? String arg3) (instance? NewValue arg4) (instance? ExpandMark arg5)) 
   ^void (.mark ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2) ^String arg3 ^NewValue arg4 ^ExpandMark arg5) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2) (instance? String arg3) (instance? String arg4) (instance? ExpandMark arg5)) 
   ^void (.mark ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2) ^String arg3 ^String arg4 ^ExpandMark arg5) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2) (instance? String arg3) (long? arg4) (instance? ExpandMark arg5)) 
   ^void (.mark ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2) ^String arg3 (long arg4) ^ExpandMark arg5) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2) (instance? String arg3) (double? arg4) (instance? ExpandMark arg5)) 
   ^void (.mark ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2) ^String arg3 (double arg4) ^ExpandMark arg5) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2) (instance? String arg3) (instance? bytes arg4) (instance? ExpandMark arg5)) 
   ^void (.mark ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2) ^String arg3 ^bytes arg4 ^ExpandMark arg5) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2) (instance? String arg3) (instance? Counter arg4) (instance? ExpandMark arg5)) 
   ^void (.mark ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2) ^String arg3 ^Counter arg4 ^ExpandMark arg5) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2) (instance? String arg3) (instance? Date arg4) (instance? ExpandMark arg5)) 
   ^void (.mark ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2) ^String arg3 ^Date arg4 ^ExpandMark arg5) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2) (instance? String arg3) (boolean? arg4) (instance? ExpandMark arg5)) 
   ^void (.mark ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2) ^String arg3 (boolean arg4) ^ExpandMark arg5)))

(defn counter-get-value
   ([counter]
   ^long (.getValue ^Counter counter)))

(defn new-value-f64
   ([value]
   ^NewValue (NewValue/f64 (double value))))

(defn document-free
   ([document]
   ^void (.free ^Document document)))

(defn patch-get-action
   ([patch]
   ^PatchAction (.getAction ^Patch patch)))

(defn patch-get-path
   ([patch]
   ^ArrayList (.getPath ^Patch patch)))

(defn transaction-unmark
   ([transaction obj mark-name start end expand]
   ^void (.unmark ^Transaction transaction ^ObjectId obj ^String mark-name (long start) (long end) ^ExpandMark expand)))

(defn sync-state-decode
   ([encoded]
   ^SyncState (SyncState/decode ^bytes encoded)))

(defn mark-get-name
   ([mark]
   ^String (.getName ^Mark mark)))

(defn document-text
   ([document obj]
   ^Optional (.text ^Document document ^ObjectId obj)))

(defn cursor-from-bytes
   ([encoded]
   ^Cursor (Cursor/fromBytes ^bytes encoded)))

(defn document-get-marks-at-index
   ([document obj index]
   ^HashMap (.getMarksAtIndex ^Document document ^ObjectId obj (int index))))

(defn object-id-is-root
   ([object-id]
   ^boolean (.isRoot ^ObjectId object-id)))

(defn patch-action-get-value
   ([patch-action]
   (and (instance? PatchAction$PutMap patch-action)) 
   ^AmValue (.getValue ^PatchAction$PutMap patch-action) 
   (and (instance? PatchAction$PutList patch-action)) 
   ^AmValue (.getValue ^PatchAction$PutList patch-action) 
   (and (instance? PatchAction$Increment patch-action)) 
   ^long (.getValue ^PatchAction$Increment patch-action)))

(defn transaction-splice-text
   ([transaction obj start delete-count text]
   ^void (.spliceText ^Transaction transaction ^ObjectId obj (long start) (long delete-count) ^String text)))

(defn transaction-mark-uint
   ([transaction obj start end mark-name value expand]
   ^void (.markUint ^Transaction transaction ^ObjectId obj (long start) (long end) ^String mark-name (long value) ^ExpandMark expand)))

(defn change-hash-get-bytes
   ([change-hash]
   ^[:array-of byte] (.getBytes ^ChangeHash change-hash)))

(defn document-start-transaction
   ([document]
   ^Transaction (.startTransaction ^Document document)))

(defn patch-log-free
   ([patch-log]
   ^void (.free ^PatchLog patch-log)))

(defn patch-action-get-index
   ([patch-action]
   (and (instance? PatchAction$PutList patch-action)) 
   ^long (.getIndex ^PatchAction$PutList patch-action) 
   (and (instance? PatchAction$Insert patch-action)) 
   ^long (.getIndex ^PatchAction$Insert patch-action) 
   (and (instance? PatchAction$SpliceText patch-action)) 
   ^long (.getIndex ^PatchAction$SpliceText patch-action) 
   (and (instance? PatchAction$DeleteList patch-action)) 
   ^long (.getIndex ^PatchAction$DeleteList patch-action)))

(defn document-merge
   ([document other]
   ^void (.merge ^Document document ^Document other)))

(defn change-hash-equals
   ([change-hash obj]
   ^boolean (.equals ^ChangeHash change-hash ^Object obj)))

(defn make-sync-state
   ([]
   ^SyncState (SyncState. )))

(defn am-value-get-id
   ([am-value]
   (and (instance? AmValue$Map am-value)) 
   ^ObjectId (.getId ^AmValue$Map am-value) 
   (and (instance? AmValue$List am-value)) 
   ^ObjectId (.getId ^AmValue$List am-value) 
   (and (instance? AmValue$Text am-value)) 
   ^ObjectId (.getId ^AmValue$Text am-value)))

(defn document-start-transaction-at
   ([document patch-log heads]
   ^Transaction (.startTransactionAt ^Document document ^PatchLog patch-log ^"[Lorg.automerge.ChangeHash;" heads)))

(defn document-get-actor-id
   ([document]
   ^[:array-of byte] (.getActorId ^Document document)))

(defn cursor-from-string
   ([encoded]
   ^Cursor (Cursor/fromString ^String encoded)))

(defn transaction-splice
   ([transaction obj start delete-count items]
   ^void (.splice ^Transaction transaction ^ObjectId obj (long start) (long delete-count) ^Iterator items)))

(defn document-save
   ([document]
   ^[:array-of byte] (.save ^Document document)))

(defn transaction-rollback
   ([transaction]
   ^void (.rollback ^Transaction transaction)))

(defn am-value-to-string
   ([am-value]
   (and (instance? AmValue$UInt am-value)) 
   ^String (.toString ^AmValue$UInt am-value) 
   (and (instance? AmValue$Int am-value)) 
   ^String (.toString ^AmValue$Int am-value) 
   (and (instance? AmValue$Bool am-value)) 
   ^String (.toString ^AmValue$Bool am-value) 
   (and (instance? AmValue$Bytes am-value)) 
   ^String (.toString ^AmValue$Bytes am-value) 
   (and (instance? AmValue$Str am-value)) 
   ^String (.toString ^AmValue$Str am-value) 
   (and (instance? AmValue$F64 am-value)) 
   ^String (.toString ^AmValue$F64 am-value) 
   (and (instance? AmValue$Counter am-value)) 
   ^String (.toString ^AmValue$Counter am-value) 
   (and (instance? AmValue$Timestamp am-value)) 
   ^String (.toString ^AmValue$Timestamp am-value) 
   (and (instance? AmValue$Null am-value)) 
   ^String (.toString ^AmValue$Null am-value) 
   (and (instance? AmValue$Unknown am-value)) 
   ^String (.toString ^AmValue$Unknown am-value) 
   (and (instance? AmValue$Map am-value)) 
   ^String (.toString ^AmValue$Map am-value) 
   (and (instance? AmValue$List am-value)) 
   ^String (.toString ^AmValue$List am-value) 
   (and (instance? AmValue$Text am-value)) 
   ^String (.toString ^AmValue$Text am-value)))

(defn transaction-insert-null
   ([transaction obj index]
   ^void (.insertNull ^Transaction transaction ^ObjectId obj (long index))))

(defn transaction-set
   ([transaction arg0 arg1 arg2]
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (instance? String arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 ^String arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? String arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 (long arg1) ^String arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (double? arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 (double arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (double? arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 (long arg1) (double arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (int? arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 (long arg1) (int arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (int? arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 (int arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (instance? NewValue arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 ^NewValue arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? NewValue arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 (long arg1) ^NewValue arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (instance? bytes arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 ^bytes arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? bytes arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 (long arg1) ^bytes arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (boolean? arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 (boolean arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (boolean? arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 (long arg1) (boolean arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (instance? Counter arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 ^Counter arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? Counter arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 (long arg1) ^Counter arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (instance? Date arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 ^Date arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? Date arg2)) 
   ^void (.set ^Transaction transaction ^ObjectId arg0 (long arg1) ^Date arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (instance? ObjectType arg2)) 
   ^ObjectId (.set ^Transaction transaction ^ObjectId arg0 ^String arg1 ^ObjectType arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? ObjectType arg2)) 
   ^ObjectId (.set ^Transaction transaction ^ObjectId arg0 (long arg1) ^ObjectType arg2)))

(defn document-lookup-cursor-index
   ([document obj cursor]
   ^long (.lookupCursorIndex ^Document document ^ObjectId obj ^Cursor cursor)))

(defn transaction-mark-null
   ([transaction obj start end mark-name expand]
   ^void (.markNull ^Transaction transaction ^ObjectId obj (long start) (long end) ^String mark-name ^ExpandMark expand)))

(defn sync-state-encode
   ([sync-state]
   ^[:array-of byte] (.encode ^SyncState sync-state)))

(defn document-generate-sync-message
   ([document sync-state]
   ^Optional (.generateSyncMessage ^Document document ^SyncState sync-state)))

(defn patch-get-obj
   ([patch]
   ^ObjectId (.getObj ^Patch patch)))

(defn new-value-integer
   ([value]
   ^NewValue (NewValue/integer (long value))))

(defn am-value-get-value
   ([am-value]
   (and (instance? AmValue$UInt am-value)) 
   ^long (.getValue ^AmValue$UInt am-value) 
   (and (instance? AmValue$Int am-value)) 
   ^long (.getValue ^AmValue$Int am-value) 
   (and (instance? AmValue$Bool am-value)) 
   ^boolean (.getValue ^AmValue$Bool am-value) 
   (and (instance? AmValue$Bytes am-value)) 
   ^[:array-of byte] (.getValue ^AmValue$Bytes am-value) 
   (and (instance? AmValue$Str am-value)) 
   ^String (.getValue ^AmValue$Str am-value) 
   (and (instance? AmValue$F64 am-value)) 
   ^double (.getValue ^AmValue$F64 am-value) 
   (and (instance? AmValue$Counter am-value)) 
   ^long (.getValue ^AmValue$Counter am-value) 
   (and (instance? AmValue$Timestamp am-value)) 
   ^Date (.getValue ^AmValue$Timestamp am-value) 
   (and (instance? AmValue$Unknown am-value)) 
   ^[:array-of byte] (.getValue ^AmValue$Unknown am-value)))

(defn patch-action-get-property
   ([patch-action]
   (and (instance? PatchAction$Increment patch-action)) 
   ^Prop (.getProperty ^PatchAction$Increment patch-action) 
   (and (instance? PatchAction$FlagConflict patch-action)) 
   ^Prop (.getProperty ^PatchAction$FlagConflict patch-action)))

(defn patch-action-get-marks
   ([patch-action]
   ^[:array-of Mark] (.getMarks ^PatchAction$Mark patch-action)))

(defn document-map-entries
   ([document obj]
   ^Optional (.mapEntries ^Document document ^ObjectId obj)))

(defn document-load
   ([bytes]
   ^Document (Document/load ^bytes bytes)))

(defn sync-state-is-in-sync
   ([sync-state doc]
   ^boolean (.isInSync ^SyncState sync-state ^Document doc)))

(defn make-document
   ([]
   ^Document (Document. )))

(defn transaction-close
   ([transaction]
   ^void (.close ^Transaction transaction)))

(defn document-get-all
   ([document arg0 arg1]
   (and (instance? Document document) (instance? ObjectId arg0) (instance? String arg1)) 
   ^Optional (.getAll ^Document document ^ObjectId arg0 ^String arg1) 
   (and (instance? Document document) (instance? ObjectId arg0) (int? arg1)) 
   ^Optional (.getAll ^Document document ^ObjectId arg0 (int arg1))))

(defn document-receive-sync-message
   ([document sync-state message]
   ^void (.receiveSyncMessage ^Document document ^SyncState sync-state ^bytes message)))

(defn new-value-uint
   ([value]
   ^NewValue (NewValue/uint (long value))))

(defn document-marks
   ([document obj]
   ^List (.marks ^Document document ^ObjectId obj)))

(defn cursor-to-bytes
   ([cursor]
   ^[:array-of byte] (.toBytes ^Cursor cursor)))

(defn patch-action-get-text
   ([patch-action]
   ^String (.getText ^PatchAction$SpliceText patch-action)))

(defn new-value-timestamp
   ([value]
   ^NewValue (NewValue/timestamp ^Date value)))

(defn patch-action-get-length
   ([patch-action]
   ^long (.getLength ^PatchAction$DeleteList patch-action)))

(defn mark-get-end
   ([mark]
   ^long (.getEnd ^Mark mark)))

(defn object-id-to-string
   ([object-id]
   ^String (.toString ^ObjectId object-id)))

(defn am-value-get-type-code
   ([am-value]
   ^int (.getTypeCode ^AmValue$Unknown am-value)))

(defn object-id-hash-code
   ([object-id]
   ^int (.hashCode ^ObjectId object-id)))

(defn transaction-insert
   ([transaction arg0 arg1 arg2]
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (double? arg2)) 
   ^void (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) (double arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? String arg2)) 
   ^void (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) ^String arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (int? arg2)) 
   ^void (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) (int arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? bytes arg2)) 
   ^void (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) ^bytes arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? Counter arg2)) 
   ^void (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) ^Counter arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? Date arg2)) 
   ^void (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) ^Date arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (boolean? arg2)) 
   ^void (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) (boolean arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? NewValue arg2)) 
   ^void (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) ^NewValue arg2) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (instance? ObjectType arg2)) 
   ^ObjectId (.insert ^Transaction transaction ^ObjectId arg0 (long arg1) ^ObjectType arg2)))

(defn mark-to-string
   ([mark]
   ^String (.toString ^Mark mark)))

(defn object-id-equals
   ([object-id obj]
   ^boolean (.equals ^ObjectId object-id ^Object obj)))

(defn new-value-bytes
   ([value]
   ^NewValue (NewValue/bytes ^bytes value)))

(defn transaction-set-uint
   ([transaction arg0 arg1 arg2]
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (long? arg2)) 
   ^void (.setUint ^Transaction transaction ^ObjectId arg0 ^String arg1 (long arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2)) 
   ^void (.setUint ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2))))

(defn new-value-str
   ([value]
   ^NewValue (NewValue/str ^String value)))

(defn prop-hash-code
   ([prop]
   (and (instance? Prop$Key prop)) 
   ^int (.hashCode ^Prop$Key prop) 
   (and (instance? Prop$Index prop)) 
   ^int (.hashCode ^Prop$Index prop)))

(defn make-patch-log
   ([]
   ^PatchLog (PatchLog. )))

(defn make-counter
   ([value]
   ^Counter (Counter. (long value))))

(defn sync-state-free
   ([sync-state]
   ^void (.free ^SyncState sync-state)))

(defn document-make-patches
   ([document patch-log]
   ^List (.makePatches ^Document document ^PatchLog patch-log)))

(defn new-value-bool
   ([value]
   ^NewValue (NewValue/bool (boolean value))))

(defn document-get
   ([document arg0 arg1]
   (and (instance? Document document) (instance? ObjectId arg0) (instance? String arg1)) 
   ^Optional (.get ^Document document ^ObjectId arg0 ^String arg1) 
   (and (instance? Document document) (instance? ObjectId arg0) (int? arg1)) 
   ^Optional (.get ^Document document ^ObjectId arg0 (int arg1))))

(defn cursor-to-string
   ([cursor]
   ^String (.toString ^Cursor cursor)))

(defn transaction-increment
   ([transaction arg0 arg1 arg2]
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1) (long? arg2)) 
   ^void (.increment ^Transaction transaction ^ObjectId arg0 ^String arg1 (long arg2)) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1) (long? arg2)) 
   ^void (.increment ^Transaction transaction ^ObjectId arg0 (long arg1) (long arg2))))

(defn transaction-insert-uint
   ([transaction obj index value]
   ^void (.insertUint ^Transaction transaction ^ObjectId obj (long index) (long value))))

(defn document-make-cursor
   ([document obj index]
   ^Cursor (.makeCursor ^Document document ^ObjectId obj (long index))))

(defn new-value-counter
   ([value]
   ^NewValue (NewValue/counter (long value))))

(defn make-prop
   ([key]
   (and (instance? String key)) 
   ^Prop (Prop$Key. ^String key) 
   (and (long? key)) 
   ^Prop (Prop$Index. (long key))))

(defn document-get-heads
   ([document]
   ^[:array-of ChangeHash] (.getHeads ^Document document)))

(defn patch-action-is-conflict
   ([patch-action]
   (and (instance? PatchAction$PutMap patch-action)) 
   ^boolean (.isConflict ^PatchAction$PutMap patch-action) 
   (and (instance? PatchAction$PutList patch-action)) 
   ^boolean (.isConflict ^PatchAction$PutList patch-action)))

(defn document-length
   ([document obj]
   ^long (.length ^Document document ^ObjectId obj)))

(defn document-keys
   ([document obj]
   ^Optional (.keys ^Document document ^ObjectId obj)))

(defn counter-hash-code
   ([counter]
   ^int (.hashCode ^Counter counter)))

(defn patch-action-get-key
   ([patch-action]
   (and (instance? PatchAction$PutMap patch-action)) 
   ^String (.getKey ^PatchAction$PutMap patch-action) 
   (and (instance? PatchAction$DeleteMap patch-action)) 
   ^String (.getKey ^PatchAction$DeleteMap patch-action)))

(defn document-get-object-type
   ([document obj]
   ^Optional (.getObjectType ^Document document ^ObjectId obj)))

(defn document-diff
   ([document before after]
   ^List (.diff ^Document document ^"[Lorg.automerge.ChangeHash;" before ^"[Lorg.automerge.ChangeHash;" after)))

(defn change-hash-hash-code
   ([change-hash]
   ^int (.hashCode ^ChangeHash change-hash)))

(defn prop-get-value
   ([prop]
   (and (instance? Prop$Key prop)) 
   ^String (.getValue ^Prop$Key prop) 
   (and (instance? Prop$Index prop)) 
   ^long (.getValue ^Prop$Index prop)))

(defn transaction-commit
   ([transaction]
   ^Optional (.commit ^Transaction transaction)))

(defn patch-action-get-values
   ([patch-action]
   ^ArrayList (.getValues ^PatchAction$Insert patch-action)))

(defn document-apply-encoded-changes
   ([document changes]
   ^void (.applyEncodedChanges ^Document document ^bytes changes)))

(defn counter-equals
   ([counter obj]
   ^boolean (.equals ^Counter counter ^Object obj)))

(defn mark-get-value
   ([mark]
   ^AmValue (.getValue ^Mark mark)))

(defn document-list-items
   ([document obj]
   ^Optional (.listItems ^Document document ^ObjectId obj)))

(defn document-encode-changes-since
   ([document heads]
   ^[:array-of byte] (.encodeChangesSince ^Document document ^"[Lorg.automerge.ChangeHash;" heads)))

(defn transaction-set-null
   ([transaction arg0 arg1]
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1)) 
   ^void (.setNull ^Transaction transaction ^ObjectId arg0 ^String arg1) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1)) 
   ^void (.setNull ^Transaction transaction ^ObjectId arg0 (long arg1))))

(defn transaction-delete
   ([transaction arg0 arg1]
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (instance? String arg1)) 
   ^void (.delete ^Transaction transaction ^ObjectId arg0 ^String arg1) 
   (and (instance? Transaction transaction) (instance? ObjectId arg0) (long? arg1)) 
   ^void (.delete ^Transaction transaction ^ObjectId arg0 (long arg1))))
