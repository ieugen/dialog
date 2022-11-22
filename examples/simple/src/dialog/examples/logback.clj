(ns dialog.examples.logback
  (:require
    [dialog.format.pretty :as pretty]
    [dialog.format.simple :as simple]
    [dialog.logger :as log])
  (:import
    (ch.qos.logback.core
      ContextBase
      FileAppender)
    (ch.qos.logback.core.encoder
      Encoder)
    (java.nio.charset
      StandardCharsets)))


(defn encoder
  "In Logback, an encoder converts a log event to byte array."
  [output]
  (let [formatter (simple/formatter output)]
    (reify Encoder
      ;; no header 
      (headerBytes [_this] nil)

      (encode
        [_this event]
        (let [event-str (formatter event)
              event-str (str event-str "\n")]
          (.getBytes event-str StandardCharsets/UTF_8)))

      (footerBytes [_this] nil))))


(defn file-appender
  "Create a ^FileAppender from dialog output map."
  [opts]
  (let [{:keys [appender-name file-name context encoder
                immediate-flush]
         :or {appender-name "file-appender"
              file-name "dialog-file-appender.log"
              immediate-flush true}} opts
        appender (doto (FileAppender.)
                   (.setEncoder encoder)
                   (.setName appender-name)
                   (.setFile file-name)
                   (.setContext context)
                   (.setImmediateFlush immediate-flush)
                   (.start))]
    appender))


(defn file-appender-logger
  "dialog function to write log events using ^FileAppender."
  [output]
  (let [context (ContextBase.)
        encoder (encoder output)
        output (assoc output
                      :context context
                      :encoder encoder)
        appender (file-appender output)]
    (fn write-event
      [event _message]
      (.doAppend appender event))))


(comment

  (log/initialize!)
  
  
  (log/info "Hello")
  )

