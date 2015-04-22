(ns promdash-annotation-server.handler
  (:require [compojure.core :refer :all] [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [cheshire.core :refer :all]
            [taoensso.faraday :as far]))

(def client-opts
  {:access-key (System/getenv "AWS_ACCESS_KEY") :secret-key (System/getenv "AWS_SECRET_KEY")
   :endpoint (get (System/getenv) "AWS_DYNAMODB_ENDPOINT" "http://localhost:8000")
   :table-name "promdash-annotations"})

(defn ensure-annotations-table-exists []
  (far/ensure-table client-opts
                    (:table-name client-opts)
                    [:tag :s]
                    {:range-keydef [:timestamp :n]
                     :throughput {:read (get (System/getenv) "DYNAMODB_READ_CAPACITY_UNITS" 1)
                                  :write (get (System/getenv) "DYNAMODB_WRITE_CAPACITY_UNITS" 1)}
                     :block? true}))

(defn delete-annotation [tag timestamp]
  (far/delete-item client-opts (keyword (:table-name client-opts)) {:tag tag :timestamp (num timestamp)}))

(defn put-annotation [tag timestamp message]
  (far/put-item client-opts (keyword (:table-name client-opts)) {:tag tag :timestamp (num timestamp) :message message}))

(defn list-annotations [& {:keys [tag mrange until]}]
  (let [annotations (far/query client-opts
                               (keyword (:table-name client-opts))
                               {:tag [:eq tag]
                                :timestamp [:between [(- until mrange) until]]})]
    (map (fn [annotation] {:created_at (:timestamp annotation) :message (:message annotation)}) annotations)))

(comment
  (delete-annotation "foo" 123)
  (ensure-annotations-table-exists)
  (put-annotation "foo" 123 "hello!")
  (put-annotation "bar" 123 "hello!")
  (put-annotation "foo" 1 "hello!")
  (put-annotation "foo" 23 "hello!")
  (put-annotation "foo" 2345 "huhu")
  (list-annotations :tag "foo" :mrange 100 :until 123))

(defn annotation-list-response [tags mrange until]
  {:posts (into [] (flatten (map #(list-annotations :tag % :mrange mrange :until until) tags)))})

(defn current-timestamp []
  (int (/ (System/currentTimeMillis) 1000)))

(defroutes app-routes
           (GET "/annotations" request
                (let [tags (get-in request [:params "tags[]"] [])
                      mrange (read-string (get-in request [:params :range] "60"))
                      until (read-string (get-in request [:params :until] (str (current-timestamp))))]
                     (generate-string (annotation-list-response tags mrange until))))
           (POST "/annotations/tags/:tag" request
                (let [tag (get-in request [:params :tag])
                      body (parse-string (slurp (get request :body)) true)]
                  (put-annotation tag (:created_at body) (:message body)))
                 "ok")
           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))