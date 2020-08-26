(ns personal-finances.controllers.entry
  (:require [clojure.spec.alpha                    :as s]
            [personal-finances.models.entry        :as m-ent]
            [personal-finances.models.account      :as m-acc]
            [personal-finances.controllers.account :as c-acc]
            [personal-finances.logic.coercion      :as l-coe]
            [personal-finances.logic.utils         :as l-utl]
            [personal-finances.logic.calculations  :as l-cal]
            [personal-finances.format              :as fmt]))

;; TODO: better error message when it doesn't find the account

(defn- add-entry
  [increasing-account decreasing-account description value entry-date post-date db]
  (let [db-conn (db)
        i-acc   (-> increasing-account l-coe/account-like (c-acc/get-account db) :success :account/id)
        d-acc   (-> decreasing-account l-coe/account-like (c-acc/get-account db) :success :account/id)
        entry   {:increasing_account_id i-acc
                 :decreasing_account_id d-acc
                 :description description
                 :value value
                 :entry_date entry-date
                 :post_date post-date}]
  (try {:success
        (-> entry
            (m-ent/insert-entry! db-conn)
            (get (keyword "last_insert_rowid()")))}
       (catch Exception e {:failure (ex-message e)}))))

(defn- list-entries
  ([db]
   (let [db-conn (db)]
     (try
       {:success (->> (m-ent/read-entries db-conn)
                      (map (fn [{:keys [account/increasing_account_category
                                        account/increasing_account_name
                                        account/decreasing_account_category
                                        account/decreasing_account_name] :as entry}]
                             (assoc entry
                                    :increasing_account (m-acc/account increasing_account_category increasing_account_name)
                                    :decreasing_account (m-acc/account decreasing_account_category decreasing_account_name)))))}
       (catch Exception e {:failure (ex-message e)}))))
  ([limit db]
   (l-utl/update-if-exists (list-entries db) :success (partial take limit))))

(defn- list-balances
  [entries]
    (l-utl/update-if-exists entries
                            :success
                            #(->> % l-cal/accounts-summary l-cal/add-balances)))

;; Command logic (function + handler)

(defn- entry-add-cmd
  [{:keys [post-date increasing-account decreasing-account description value system]}]
  (let [coerced-value (l-coe/double-like value)
        result (add-entry increasing-account decreasing-account description coerced-value post-date post-date (:database system))]
    (l-utl/print-result #(str "Entry added [id: " % "]") result)))

(defn- entry-list-cmd
  [{:keys [system]}]
  (let [result (list-entries 30 (:database system))]
    (l-utl/print-result fmt/table result)))

(defn- balances-cmd
  [{:keys [system]}]
  (let [entries (list-entries (:database system))
        result  (list-balances entries)]
    (l-utl/print-result fmt/balances-table result)))

(def entry-add-handler
  #:personal-finances.cmd{:name ["entry" "add"]
                          :fn entry-add-cmd
                          :args-spec (s/cat :post-date          l-coe/date-like
                                            :increasing-account l-coe/account-like
                                            :decreasing-account l-coe/account-like
                                            :description        string?
                                            :value              l-coe/double-like
                                            :system             (s/keys :req-un [::database]))})

(def entry-list-handler
  #:personal-finances.cmd{:name ["entry" "list"]
                          :fn entry-list-cmd
                          :args-spec (s/cat :system (s/keys :req-un [::database]))})

(def balances-handler
  #:personal-finances.cmd{:name ["balances"]
                          :fn balances-cmd
                          :args-spec (s/cat :system (s/keys :req-un [::database]))})

(comment
(require '[personal-finances.main :refer [system]])
(add-entry "liability:teste2" "liability:teste" "oi" 123.24 "2020-01-01" "2020-01-01" (:database system))
(entry-add-cmd {:increasing-account "asset:teste2"
                :decreasing-account "liability:teste2"
                :post-date "2020-01-02"
                :description "oile"
                :value "250"
                :system system})
(entry-list-cmd {:system system})
(list-entries (:database system))
(balances-cmd {:system system})
)
