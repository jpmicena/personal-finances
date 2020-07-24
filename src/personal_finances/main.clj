(ns personal-finances.main
  (:require [com.stuartsierra.component :as component]
            [personal-finances.components.database :as c-db]))

(defrecord Application [database ;; Dependency
                        state]
  component/Lifecycle
  (start [this]
    (do
      (println "Starting the application")
      (assoc this :state "Running")))
  (stop [this]
    (do
      (println "Stopping the application")
      (assoc this :state "Stopped"))))

;; Builds an application with no state
(def application
  (component/using (map->Application {}) [:database]))

(def system
  (component/system-map
    :database    c-db/setup
    :application application))

(comment
  (alter-var-root #'system component/start)
  (alter-var-root #'system component/stop)

  )
