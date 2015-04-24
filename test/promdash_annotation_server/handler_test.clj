(ns promdash-annotation-server.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [promdash-annotation-server.handler :refer :all]
            [cheshire.core :refer :all]))

(def some-deployment-annotation-post
  {:created_at 1423487712 :message "some deployment"})

(use-fixtures :once (fn [f] (ensure-annotations-table-exists) (f)))
(use-fixtures :each (fn [f] (delete-annotation "deployment" 1423487712) (f)))

(deftest test-app
  (testing "returns a empty list of annotation posts"
    (let [response (app (mock/request :get "/annotations?range=3600&tags[]=deployment&tags[]=prometheus&until=1423491311.424"))]
      (is (= (:status response) 200))
      (is (= (:body response) (generate-string {:posts []})))))

  (testing "returns a list of annotation posts"
    (let [response (app (mock/body (mock/request :post "/annotations/tags/deployment") (generate-string some-deployment-annotation-post)))]
      (is (= (:status response) 200))
      (is (= (:body response) "ok")))
    (let [response (app (mock/body (mock/request :post "/annotations/tags/foo") (generate-string some-deployment-annotation-post)))]
      (is (= (:status response) 200))
      (is (= (:body response) "ok")))
    (let [response (app (mock/request :get "/annotations?range=3600&tags[]=deployment&until=1423491311.424"))]
      (is (= (:status response) 200))
      (is (= (:body response) (generate-string {:posts [some-deployment-annotation-post]})))))

  (testing "returns a list of annotation posts"
    (let [response (app (mock/body (mock/request :post "/annotations/tags/deployment") (generate-string some-deployment-annotation-post)))]
      (is (= (:status response) 200))
      (is (= (:body response) "ok")))
    (let [response (app (mock/request :get "/annotations?range=3600&tags%5B%5D=deployment&tags%5B%5D=prometheus&until=1423491311.424"))]
      (is (= (:status response) 200))
      (is (= (:body response) (generate-string {:posts [some-deployment-annotation-post]})))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
