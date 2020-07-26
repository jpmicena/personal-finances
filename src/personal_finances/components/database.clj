(ns personal-finances.components.database
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]
            [clojure.java.io :as io]))

(def ^:private schemas (->> "resources/schemas"
                            io/file
                            file-seq
                            (filter #(.isFile %))
                            (map (fn [f] {(.getName f) (slurp f)}))
                            (reduce merge {})))

(def ^:private db-config
  "Database connection configuration"
  {:dbtype "sqlite" :dbname "personal_finances_db" :schemas schemas})

(defn- populate!
  [db schemas]
  (doseq [[n s] schemas]
    (try
      (jdbc/execute! (db) [s])
      (println "Created table " n)
      (catch Exception e
        (println "Exception:" (ex-message e))
        (println "Table " n " was already created?")))))

(defrecord Database [db-spec        ;; Configuration
                     datasource]    ;; State
  component/Lifecycle
  (start [this]
    (if datasource
      this
      (let [database (assoc this :datasource (jdbc/get-datasource db-spec))]
        (populate! database (:schemas db-spec))
        database)))
  (stop [this]
    (assoc this datasource nil))

  clojure.lang.IFn            ;; This protocol makes possible to call
  (invoke [this] datasource)) ;; the component without arguments (exposing the datasource)

(def setup (map->Database {:db-spec db-config}))

