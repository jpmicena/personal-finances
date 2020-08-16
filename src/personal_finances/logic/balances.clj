(ns persona-finances.logic.balances)

(defn balances
  [entries]
  (->> entries
       (reduce (fn
                 [b {:keys [increasing_account decreasing_account entry/value]}]
                 (-> b
                     (update increasing_account (fnil + 0) value)
                     (update decreasing_account (fnil - 0) value)))
               {})
       (reduce-kv (fn [l k v] (conj l {:account k :value v})) [])))
