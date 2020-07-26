(ns personal-finances.main
  (:require [com.stuartsierra.component :as component]
            [personal-finances.components.database :as c-db]
            [personal-finances.components.application     :as c-app]))

(def system
  (component/system-map
   :database    c-db/setup
   :application c-app/setup))

(comment
  (alter-var-root #'system component/start)
  (alter-var-root #'system component/stop))
