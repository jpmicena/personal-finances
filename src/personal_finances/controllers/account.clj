(ns personal-finances.controllers.account
  (:require [clojure.string                   :as string]
            [clojure.spec.alpha               :as s]
            [personal-finances.models.account :as m-acc]))

(defn- add-account!
 [category name valid-categories db]
 (let [lc-category (string/lower-case category)
       lc-name     (string/lower-case name)
       db-conn     (db)
       acc         {:category lc-category :name lc-name}]
   (if (some #{lc-category} valid-categories)
     (-> acc
         (m-acc/insert-account! db-conn)
         (#(hash-map :success
                     (str "Account " lc-name " on category " lc-category " added. id: "
                          (get % (keyword "last_insert_rowid()"))))))
     {:failure (str "Invalid category. Valid categories: " (string/join ", " valid-categories))})))

;; Command logic (function + handler)

(defn- account-add-cmd
  [valid-categories {:keys [category name system]}]
  (let [result (add-account! category name valid-categories (:database system))
        success (:success result)
        failure (:failure result)]
    (if success
      (println success)
      (println failure))))


(def account-add-handler
  #:personal-finances.cmd{:name ["account" "add"]
                          :fn (partial account-add-cmd m-acc/categories)
                          :args-spec (s/cat :category         string?
                                            :name             string?
                                            :system           (s/keys :req-un [::database]))})
