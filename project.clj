(defproject alandipert/cljs-priority-map "1.0.0-SNAPSHOT"
  :description "ClojureScript priority map implementation based on clojure.data.priority-map"
  :url "https://github.com/alandipert/cljs-priority-map"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/cljs"]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :cljsbuild {:builds {:test
                       {:source-paths ["test"]
                        :compiler {:output-to "public/test.js"
                                   :optimizations :advanced
                                   ;; :optimizations :whitespace
                                   ;; :pretty-print true
                                   }
                        :jar false}}
              :test-commands {"unit" ["phantomjs" "test/runner.js"]}})
