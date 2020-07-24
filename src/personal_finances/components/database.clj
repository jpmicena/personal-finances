(ns personal-finances.components.database
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]))

(def ^:private db-config
  "Database connection configuration"
  {:dbtype "sqlite" :dbname "personal_finances_db"})

(defn- populate
  [db]
  (try
    (jdbc/execute-one! (db) [
                             (str "
                                  create table account (
                                  id      integer primary key autoincrement
                                  name    text
                                  )")])
    (println "Succesfully created db and table")
    (catch Exception e
      (println "Exception:" (ex-message e))
      (println "Looks like the database is already setup?"))))

(defrecord Database [db-spec        ;; Configuration
                     datasource]    ;; State
  component/Lifecycle
  (start [this]
    (if datasource
      this
      (let [database (assoc this :datasource (jdbc/get-datasource db-spec))]
        (populate database)
        database)))
  (stop [this]
    (assoc this datasource nil))

  clojure.lang.IFn            ;; This protocol makes possible to call
  (invoke [this] datasource)) ;; the component without arguments (exposing the datasource)

(def setup (map->Database {:db-spec db-config}))




