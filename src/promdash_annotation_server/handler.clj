(ns promdash-annotation-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [cheshire.core :refer :all]
            [taoensso.faraday :as far]))

(def client-opts
  {:access-key (System/getenv "AWS_ACCESS_KEY")
   :secret-key (System/getenv "AWS_SECRET_KEY")
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

(defn delete-tag [tag timestamp]
  (far/delete-item client-opts (keyword (:table-name client-opts)) {:tag tag :timestamp (str timestamp)}))

(defn put-tag [tag timestamp message]
  (far/put-item client-opts (keyword (:table-name client-opts)) {:tag tag :timestamp (str timestamp) :message message}))

(defn list-annotations [& {:keys [tags mrange until]}]
  [])

(comment
  (delete-tag "foo" 123)
  (put-tag "foo" 123 "hello!")
  (list-annotations :tags ["foo" "bar"] :range 3600 :until 123))

(defroutes app-routes
           (GET "/annotations" [request]
                (let [tags (get-in request [:query-params :tags] [])
                      mrange (get-in request [:query-params :range] 60)
                      until (get-in request [:query-params :until] (/ (System/currentTimeMillis) 1000))]
                     (generate-string {:posts (list-annotations :tags tags :range mrange :until until)})))
           (PUT "/annotations/tags/:tag" request
                (let [tag (get-in request [:params :tag])
                      body (parse-string (slurp (get request :body)) true)]
                  (put-tag tag (:created_at body) (:message body)))
                 "ok")
           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))
