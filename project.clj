(defproject promdash-annotation-server "0.1.0-SNAPSHOT"
  :description "An annotation backend for promdash."
  :url "https://github.com/schnipseljagd/promdash-annotation-server"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-json "0.4.0"]
                 [cheshire "5.5.0"]
                 [com.taoensso/faraday "1.7.1"]
                 [clj-time "0.11.0"]
                 [environ "1.0.1"]
                 [ring-server "0.4.0"]]
  :plugins [[lein-ring "0.8.13"]
            [lein-environ "1.0.0"]]
  :ring {:handler promdash-annotation-server.handler/app}
  :main promdash-annotation-server.launcher
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :uberjar {:aot :all}})
