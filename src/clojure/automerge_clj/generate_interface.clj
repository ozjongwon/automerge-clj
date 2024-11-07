(ns clojure.automerge-clj.generate-interface
  (:require [clojure.data.priority-map :refer [priority-map]]
            [clojure.set :as set])
  (:import [org.automerge AutomergeSys Document ObjectId ChangeHash ObjectType
            PatchLog]))

(defn init-lib []
  (Document.) ;; Native lib init happens
  ObjectId/ROOT ;; Access lazy field
  )
