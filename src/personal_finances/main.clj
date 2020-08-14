(ns personal-finances.main
  (:require [com.stuartsierra.component               :as component]
            [personal-finances.components.database    :as comp-db]
            [personal-finances.components.application :as comp-app]
            [personal-finances.cmd                    :as cmd]))

(def system
  (component/system-map
   :database    comp-db/setup
   :application comp-app/setup))

(defn -main []
  (let [system (component/system-map
                 :database    comp-db/setup
                 :application comp-app/setup)
        started-system (component/start system)]
  (while true
    (let [line (read-line)]
      (cmd/execute-cmd line started-system)
      (flush)))))

(comment
(alter-var-root #'system component/start)
(alter-var-root #'system component/stop)
)
