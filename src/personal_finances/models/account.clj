(ns personal-finances.models.account
  (:require [next.jdbc.sql :as sql]))

(def categories #{"asset" "liability" "income" "expense"})

(defn insert-account!
  [acc db-conn]
  (sql/insert! db-conn :account acc))

(defn delete-account!
  [acc-id db-conn]
  (sql/delete! db-conn :account {:id acc-id}))

(defn read-accounts
  [db-conn]
  (sql/query db-conn ["SELECT id, category, name FROM account ORDER BY name ASC"]))

(defn read-account-one
  [{:keys [category name]} db-conn]
  (-> db-conn
      (sql/query ["SELECT id, category, name FROM account WHERE category = ? AND name = ?" category name])
      first))

(comment
(require '[personal-finances.main :refer [system]])
(def example-account {:category "liability"
                      :name     "teste2"})
(insert-account! example-account ((:database system)))
(insert-account! (dissoc example-account :name) ((:database system)))
(read-account-one {:category "liability" :name "teste"} ((:database system)))
(delete-account! 4 ((:database system)))
(read-accounts ((:database system)))
)

