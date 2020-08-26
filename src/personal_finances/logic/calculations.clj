(ns personal-finances.logic.calculations)

(defn entry-pairs
  [entry]
  (let [entry-skeleton   {:account/id        entry
                          :account/name      entry
                          :account/category  entry
                          :entry/description (:entry/description entry)
                          :entry/post-date   (:entry/post_date entry)
                          :entry/entry-date  (:entry/entry_date entry)
                          :entry/value       (:entry/value entry)}
        entry-increasing {:account/id       :entry/increasing_account_id
                          :account/name     :account/increasing_account_name
                          :account/category :account/increasing_account_category
                          :entry/value +}
        entry-decreasing {:account/id       :entry/decreasing_account_id
                          :account/name     :account/decreasing_account_name
                          :account/category :account/decreasing_account_category
                          :entry/value -}
        update-fn (fn [m [k f]] (update m k f))]
    [(reduce update-fn entry-skeleton entry-increasing)
     (reduce update-fn entry-skeleton entry-decreasing)]))

(defn- update-account-info
  [{:keys [account/name account/category entry/description entry/post-date entry/entry-date entry/value]} acc]
  (let [entry {:entry/description description
               :entry/post-date   post-date
               :entry/entry-date  entry-date
               :entry/value       value}]
  (if (nil? acc)
    {:account/category category
     :account/name name
     :account/entries [entry]}
    (update acc :account/entries #(conj % entry)))))

(defn accounts-summary
  [entries]
  (->> entries
       (mapcat entry-pairs)
       (reduce (fn [s e] (update s (:account/id e) (partial update-account-info e))) {})
       vals))

(defn- add-balance-one
  [summ]
  (assoc summ :account/balance (reduce (fn [v e] (+ v (:entry/value e))) 0 (:account/entries summ))))

(defn add-balances
  [summs]
  (map add-balance-one summs))

(comment
(def eg '({:entry/id 5,
   :entry/description "oile",
   :entry/decreasing_account_id 1,
   :entry/post_date "2020-01-02",
   :account/increasing_account_category "asset",
   :decreasing_account "liability:teste2",
   :increasing_account "asset:teste2",
   :account/decreasing_account_name "teste2",
   :entry/increasing_account_id 2,
   :account/increasing_account_name "teste2",
   :account/decreasing_account_category "liability",
   :entry/entry_date "2020-01-02",
   :entry/value 250}
  {:entry/id 4,
   :entry/description "oile",
   :entry/decreasing_account_id 1,
   :entry/post_date "2020-01-02",
   :account/increasing_account_category "asset",
   :decreasing_account "liability:teste2",
   :increasing_account "asset:teste2",
   :account/decreasing_account_name "teste2",
   :entry/increasing_account_id 2,
   :account/increasing_account_name "teste2",
   :account/decreasing_account_category "liability",
   :entry/entry_date "2020-01-02",
   :entry/value 210}
  {:entry/id 3,
   :entry/description "oile",
   :entry/decreasing_account_id 1,
   :entry/post_date "2020-01-01",
   :account/increasing_account_category "asset",
   :decreasing_account "liability:teste2",
   :increasing_account "asset:teste2",
   :account/decreasing_account_name "teste2",
   :entry/increasing_account_id 2,
   :account/increasing_account_name "teste2",
   :account/decreasing_account_category "liability",
   :entry/entry_date "2020-01-01",
   :entry/value 210}
  {:entry/id 2,
   :entry/description "oile",
   :entry/decreasing_account_id 1,
   :entry/post_date "2020-01-01",
   :account/increasing_account_category "asset",
   :decreasing_account "liability:teste2",
   :increasing_account "asset:teste2",
   :account/decreasing_account_name "teste2",
   :entry/increasing_account_id 2,
   :account/increasing_account_name "teste2",
   :account/decreasing_account_category "liability",
   :entry/entry_date "2020-01-01",
   :entry/value 210}
  {:entry/id 1,
   :entry/description "oile",
   :entry/decreasing_account_id 1,
   :entry/post_date "2020-01-01",
   :account/increasing_account_category "asset",
   :decreasing_account "liability:teste2",
   :increasing_account "asset:teste2",
   :account/decreasing_account_name "teste2",
   :entry/increasing_account_id 2,
   :account/increasing_account_name "teste2",
   :account/decreasing_account_category "liability",
   :entry/entry_date "2020-01-01",
   :entry/value 210}))
(-> eg accounts-summary add-balances)
)
