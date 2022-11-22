(ns dialog.examples.sentry
  (:require
    [clojure.tools.logging :as log]
    [dialog.logger :as dialog]
    [sentry-clj.core :as sentry]))


(defn sentry-logger
  "dialog function to write log events using sentry"
  [output]
  (let [sentry-opts (:sentry-opts output)
        opts (assoc output
                    :level-cache (atom {} :validator map?))
        dsn (:dsn sentry-opts)]
    (sentry/init! dsn output)
    (fn write-event
      [event _message]
      (let [{:keys [level logger]} event]
        (when (dialog/enabled? opts logger level)
          (sentry/send-event event))))))


(comment
  
  (dialog/initialize!)

  (log/info (ex-info "Example info" {:extra-data "extra data"})
            "This is an info message")
  
  (log/error (ex-info "Example error" {:extra-data "extra data"})
            "This is an error message")

  (dialog/match-level {"dialog.example" :info} "dialog.example.test")

  )
