(ns promdash-annotation-server.launcher
  (:gen-class)
  (:require [promdash-annotation-server.handler :as annotations]
            [ring.server.standalone :as server]))

(defn -main [& args]
  (annotations/ensure-annotations-table-exists)
  (server/serve annotations/app {:port 8080
                                 :open-browser? false
                                 :stacktraces? false
                                 :auto-reload? false}))
