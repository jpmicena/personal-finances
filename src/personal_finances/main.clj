(ns personal-finances.main
  (:require [com.stuartsierra.component               :as component]
            [personal-finances.components.database    :as c-db]
            [personal-finances.components.application :as c-app]
            [personal-finances.cmd                    :as cmd]))

(def system
  (component/system-map
   :database    c-db/setup
   :application c-app/setup))

(defn -main []
  (let [system (component/system-map
                 :database    c-db/setup
                 :application c-app/setup)
        started-system (component/start system)]
  (println (-> started-system :database :datasource)) ; this is probably wrong
  (while true
    (let [line (read-line)]
      (cmd/execute-cmd line started-system)))))

(comment
(alter-var-root #'system component/start)
(alter-var-root #'system component/stop)
)
