(ns personal-finances.format
  (:require [clojure.pprint :as cpp]
            [clojure.string :as string]))

(defn table
  [m]
  (let [printable-table (with-out-str (cpp/print-table m))
        size (-> printable-table (string/split #"\n") second count)]
    (str printable-table (apply str (repeat size "-")))))



