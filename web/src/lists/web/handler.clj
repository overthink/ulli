(ns lists.web.handler
  "Main Ring handler for the app."
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.java.io :as io]
    [ring.util.response :refer [response redirect]]
    [ring.middleware.session :refer [wrap-session]]
    [ring.middleware.params :refer [wrap-params]]
    [lists.web.db :as db]
    [lists.web.util :as util]
    [compojure.route :as route]
    [clojure.string :as string]
    [compojure.core :refer [defroutes GET POST]])
  (:import
    org.joda.time.DateTime))

(defroutes routes
  (route/resources "/")
  (GET "*" [] (io/resource "public/index.html")) ;; could/should eventually liberate the ng app from this project
  (route/not-found "Computer said no."))

(defn wrap-system
  "Adds the system to the request under the :system key."
  [handler system]
  (fn [request]
    (handler (assoc request :system system))))

(defn app
  "Returns a new instance of the application: (i.e. a Ring handler)."
  [system]
  (-> (var routes)
      (wrap-system system)
      (wrap-session {:cookie-name "sid"
                     :cookie-attrs {:path "/"
                                    :http-only true
                                    :max-age 3600}})
      wrap-params))

