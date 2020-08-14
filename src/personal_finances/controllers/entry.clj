(ns personal-finances.controllers.entry
  (:require [clojure.spec.alpha                    :as s]
            [personal-finances.models.entry        :as m-ent]
            [personal-finances.controllers.account :as c-acc]
            [personal-finances.logic.coercion      :as l-coe]))

;; TODO: better error message when it doesn't find the account

(defn- add-entry
  [increasing-account decreasing-account description value post-date planned-date db]
  (let [db-conn (db)
        i-acc   (-> increasing-account l-coe/account-like (c-acc/get-account db) :success :account/id)
        d-acc   (-> decreasing-account l-coe/account-like (c-acc/get-account db) :success :account/id)
        entry   {:increasing_account_id i-acc
                 :decreasing_account_id d-acc
                 :description description
                 :value value
                 :post_date post-date
                 :planned_date planned-date}]
  (try {:success
        (str
          "Entry added. id: " (-> entry
                                  (m-ent/insert-entry! db-conn)
                                  (get (keyword "last_insert_rowid()"))))}
       (catch Exception e {:failure (ex-message e)}))))

;; Command logic (function + handler)

(defn- entry-add-cmd
  [{:keys [increasing-account decreasing-account post-date description value system]}]
  (let [coerced-value (l-coe/double-like value)
        result (add-entry increasing-account decreasing-account description coerced-value post-date post-date (:database system))]
    (-> result first val println)))

(def entry-add-handler
  #:personal-finances.cmd{:name ["entry" "add"]
                          :fn entry-add-cmd
                          :args-spec (s/cat :increasing-account l-coe/account-like
                                            :decreasing-account l-coe/account-like
                                            :post-date          l-coe/date-like
                                            :description        string?
                                            :value              l-coe/double-like
                                            :system             (s/keys :req-un [::database]))})

(comment
(require '[personal-finances.main :refer [system]])
(add-entry "liability:teste" "liability:teste" "oi" 123.24 "2020-01-01" "2020-01-01" (:database system))
(entry-add-cmd {:increasing-account "liability:teste"
                :decreasing-account "liability:teste"
                :post-date "2020-01-01"
                :description "oile"
                :value "210"
                :system system})
)

