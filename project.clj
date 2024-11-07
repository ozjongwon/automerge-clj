(defproject automerge-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.automerge/automerge "0.0.7"]]
  :repl-options {:init-ns clojure.automerge-clj.generate-interface
                 :init (init-lib)}
  :jvm-opts ["-Djava.library.path=/home/jc/Work/automerge-clj/native/x86_64-unknown-linux-gnu/"]

  :java-source-paths ["src/java"]
  :profiles {:precomp {:source-paths ["src/java"]
                       :aot [org.automerge] }}
  ;; :aliases ["generate-map" ["run" "bash" "-c" "source src/python/python-env/bin/activate && python3 src/python/parse-java.py"]]
  )