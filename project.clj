(defproject tailrecursion/cljs-priority-map "1.1.0"
  :description "ClojureScript priority map implementation based on clojure.data.priority-map"
  :url "https://github.com/tailrecursion/cljs-priority-map"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/cljs"]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {:builds {:test
                       {:source-paths ["test"]
                        :dependencies [[org.clojure/clojurescript "0.0-2156"]]
                        :compiler {:output-to "public/test.js"
                                   :optimizations :advanced}
                        :jar false}}})
