(ns personal-finances.main
  (:require [com.stuartsierra.component               :as component]
            [personal-finances.components.database    :as c-db]
            [personal-finances.components.application :as c-app]))

(def system
  (component/system-map
   :database    c-db/setup
   :application c-app/setup))

(defn -main []
  (while true
    (println "Starting...")
    (let [x (read-line)]
      (println "Typed" (+ 1 (Integer/parseInt x))))))

(comment
(alter-var-root #'system component/start)
(alter-var-root #'system component/stop)
)
