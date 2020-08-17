(ns personal-finances.controllers.account
  (:require [clojure.string                      :as string]
            [clojure.spec.alpha                  :as s]
            [personal-finances.format            :as fmt]
            [personal-finances.logic.coercion    :as l-coe]
            [personal-finances.logic.utils       :as l-utl]
            [personal-finances.models.account    :as m-acc]))

(defn- add-account
 [category name valid-categories db]
 (let [lc-category (string/lower-case category)
       lc-name     (string/lower-case name)
       db-conn     (db)
       acc         {:category lc-category :name lc-name}]
   (if (some #{lc-category} valid-categories)
     (try {:success (-> acc
                        (m-acc/insert-account! db-conn)
                        (get (keyword "last_insert_rowid()")))}
          (catch Exception e {:failure (ex-message e)}))
     {:failure (str "Invalid category. Valid categories: " (string/join ", " valid-categories))})))

(defn- list-accounts
  [db]
  (let [db-conn (db)]
    (try
      {:success (m-acc/read-accounts db-conn)}
      (catch Exception e {:failure (ex-message e)}))))

(defn- remove-account
  [id db]
  (let [db-conn (db)]
    (try {:success (-> (m-acc/delete-account! id db-conn) :next.jdbc/update-count)}
         (catch Exception e {:failure (ex-message e)}))))

(defn get-account
  [acc db]
  (let [db-conn (db)]
    (try {:success (m-acc/read-account-one acc db-conn)}
         (catch Exception e {:failure (ex-message e)}))))

;; Command logic (function + handler)

(defn- account-add-cmd
  [valid-categories {:keys [category name system]}]
  (let [result (add-account category name valid-categories (:database system))]
    (l-utl/print-result #(str "Account added [id: " % "]") result)))

(defn- account-list-cmd
  [{:keys [system]}]
  (let [result (list-accounts (:database system))]
    (l-utl/print-result fmt/table result)))

(defn- account-remove-cmd
  [{:keys [id system]}]
  (let [coerced-id (l-coe/int-like id)
        result (remove-account coerced-id (:database system))]
    (l-utl/print-result #(str "Account deleted [count: " % "]") result)))

(def account-add-handler
  #:personal-finances.cmd{:name ["account" "add"]
                          :fn (partial account-add-cmd m-acc/categories)
                          :args-spec (s/cat :category string?
                                            :name     string?
                                            :system   (s/keys :req-un [::database]))})

(def account-list-handler
  #:personal-finances.cmd{:name ["account" "list"]
                          :fn account-list-cmd
                          :args-spec (s/cat :system (s/keys :req-un [::database]))})

(def account-remove-handler
  #:personal-finances.cmd{:name ["account" "remove"]
                          :fn account-remove-cmd
                          :args-spec (s/cat :id l-coe/int-like
                                            :system (s/keys :req-un [::database]))})

(comment
(require '[personal-finances.main :refer [system]])
(add-account "inflow" "teste" m-acc/categories (:database system))
)
