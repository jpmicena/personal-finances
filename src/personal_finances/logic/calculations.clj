(ns personal-finances.logic.calculations)

(defn balances
  "Gets a full list of entries, return balances from all accounts"
  [entries]
  (->> entries
       (reduce (fn
                 [b {:keys [increasing_account decreasing_account entry/value]}]
                 (-> b
                     (update increasing_account (fnil + 0) value)
                     (update decreasing_account (fnil - 0) value)))
               {})
       (reduce-kv (fn [l k v] (conj l {:account k :value v})) [])))
