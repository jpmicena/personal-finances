(ns personal-finances.model.account
  (:require [next.jdbc.sql :as sql]))

(def categories ["asset" "liability" "income" "expense"])

(defn add-account!
  [acc db]
  (sql/insert! (db) :account acc))

(defn delete-account!
  [acc-id db]
  (sql/delete! (db) :account {:id acc-id}))

(comment
(def my-system personal-finances.main/system)

(def example-account {:category "asset"
                      :name     "teste"})

(add-account! example-account (:database my-system))

(delete-account! 2 (:database my-system))
)

