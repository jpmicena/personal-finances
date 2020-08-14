(ns personal-finances.models.entry
  (:require [next.jdbc.sql :as sql]
            [clojure.java.io :as io]))

(def ^:private read-entries-query
  (-> "resources/queries/read-entries.sql"
      io/file
      slurp))

(def ^:private min-date "2000-01-01")
(def ^:private max-date "2099-12-31")

(defn insert-entry!
  [entry db-conn]
  (sql/insert! db-conn :entry entry))

(defn delete-entry!
  [entry-id db-conn]
  (sql/delete! db-conn :entry {:id entry-id}))

(defn read-entries
  ([db-conn]
   (read-entries min-date max-date db-conn))
  ([start-date end-date db-conn]
  (sql/query
    db-conn [read-entries-query start-date end-date])))

(comment
(require '[personal-finances.main :refer [system]])
(def example-entry {:increasing_account_id 1
                    :decreasing_account_id 2
                    :description "teste teste"
                    :value 10.23
                    :post_date "2020-08-03"
                    :due_date "2020-08-04"
                    })
(insert-entry! example-entry ((:database system)))
(delete-entry! 3 ((:database system)))
(read-entries "2020-08-03" "2020-08-04" ((:database system)))
(read-entries ((:database system)))
)


