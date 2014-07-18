(ns lists.web.pgsession
  "PostgreSQL backed SessionStore implementation.  Sessions are JSONified by
  Cheshire, so don't put anything in the session that can't be reliably read
  back after going to JSON. i.e. no functions or Java objects, etc."
  (:require
    [cheshire.core :as json]
    [ring.middleware.session.store :refer [SessionStore]])
  (:import
    java.sql.Connection
    javax.sql.DataSource
    java.util.UUID))

(defn session-get
  "Return the deserialized session matching session-key, or nil if there is no
  match. Commit not called."
  [^Connection conn session-key]
  (let [q "SELECT data FROM session WHERE key = ?"]
    (with-open [stmt (.prepareStatement conn q)]
      (.setString stmt 1 session-key)
      (let [raw (:data (doall (first (resultset-seq (.executeQuery stmt)))))]
        (json/parse-string raw true)))))

(defn session-upsert
  "Update or insert the session data with key session-key.  Data is a clojure
  map that will be JSON encoded and stored.  Also updates the last accessed
  time of the session in the db. Returns nil. Commit not called."
  [^Connection conn session-key data]
  (let [q "SELECT session_upsert(?, ?)"]
    (with-open [stmt (.prepareStatement conn q)]
      (.setString stmt 1 session-key)
      (.setString stmt 2 (json/generate-string data))
      (.executeQuery stmt)
      nil)))

(defn session-delete
  "Remove the session row with key matching session-key.  Returns nil."
  [^Connection conn session-key]
  (let [q "DELETE FROM session WHERE key = ?"]
    (with-open [stmt (.prepareStatement conn q)]
      (.setString stmt 1 session-key)
      (.executeUpdate stmt)
      nil)))

(deftype PgSessionStore [^DataSource ds]
  SessionStore
  (read-session [this session-key]
    (when session-key
      (with-open [conn (.getConnection ds)]
        (session-get conn session-key))))
  (write-session [this session-key data]
    (let [k (or session-key (UUID/randomUUID))]
      (with-open [conn (.getConnection ds)]
        (session-upsert conn k data))
      k))
  (delete-session [this session-key]
    (when session-key
      (with-open [conn (.getConnection ds)]
        (session-delete conn session-key)))))

(defn pg-session-store
  "Helper for getting an instance of the session store Clojure."
  [^DataSource ds]
  (PgSessionStore. ds))

