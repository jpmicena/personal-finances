(ns personal-finances.components.application
  (:require [com.stuartsierra.component :as component]))

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
(def setup
  (component/using (map->Application {}) [:database]))
