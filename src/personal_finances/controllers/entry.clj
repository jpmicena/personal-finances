(ns personal-finances.controllers.entry
  (:require [clojure.spec.alpha                    :as s]
            [personal-finances.models.entry        :as m-ent]
            [personal-finances.controllers.account :as c-acc]
            [personal-finances.logic.coercion      :as l-coe]
            [personal-finances.logic.utils         :as l-utl]
            [personal-finances.format              :as fmt]))

;; TODO: better error message when it doesn't find the account

(defn- add-entry
  [increasing-account decreasing-account description value post-date due-date db]
  (let [db-conn (db)
        i-acc   (-> increasing-account l-coe/account-like (c-acc/get-account db) :success :account/id)
        d-acc   (-> decreasing-account l-coe/account-like (c-acc/get-account db) :success :account/id)
        entry   {:increasing_account_id i-acc
                 :decreasing_account_id d-acc
                 :description description
                 :value value
                 :post_date post-date
                 :due_date due-date}]
  (try {:success
        (-> entry
            (m-ent/insert-entry! db-conn)
            (get (keyword "last_insert_rowid()")))}
       (catch Exception e {:failure (ex-message e)}))))

(defn- list-entries
  [limit db]
  (let [db-conn (db)]
    (try
      {:success (take limit (m-ent/read-entries db-conn))}
      (catch Exception e {:failure (ex-message e)}))))

;; Command logic (function + handler)

(defn- entry-add-cmd
  [{:keys [increasing-account decreasing-account post-date description value system]}]
  (let [coerced-value (l-coe/double-like value)
        result (add-entry increasing-account decreasing-account description coerced-value post-date post-date (:database system))]
    (l-utl/print-result #(str "Entry added [id: " % "]") result)))

(defn- entry-list-cmd
  [{:keys [system]}]
  (let [result (list-entries 30 (:database system))]
    (l-utl/print-result fmt/table result)))

(def entry-add-handler
  #:personal-finances.cmd{:name ["entry" "add"]
                          :fn entry-add-cmd
                          :args-spec (s/cat :increasing-account l-coe/account-like
                                            :decreasing-account l-coe/account-like
                                            :post-date          l-coe/date-like
                                            :description        string?
                                            :value              l-coe/double-like
                                            :system             (s/keys :req-un [::database]))})

(def entry-list-handler
  #:personal-finances.cmd{:name ["entry" "list"]
                          :fn entry-list-cmd
                          :args-spec (s/cat :system (s/keys :req-un [::database]))})

(comment
(require '[personal-finances.main :refer [system]])
(add-entry "liability:teste" "liability:teste" "oi" 123.24 "2020-01-01" "2020-01-01" (:database system))
(entry-add-cmd {:increasing-account "liability:teste"
                :decreasing-account "liability:teste"
                :post-date "2020-01-01"
                :description "oile"
                :value "210"
                :system system})

(entry-list-cmd {:system system})
)


