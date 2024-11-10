(defproject automerge-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.automerge/automerge "0.0.7"]
                 [camel-snake-kebab "0.4.3"]
                 [org.clojure/data.codec "0.1.1"]
                 [clojure.java-time "1.4.2"]]
  :repl-options {:init (do (import [org.automerge ObjectId Document Transaction])
                           (let [doc (Document.)]
                             ObjectId/ROOT
                             (.free doc)))})
