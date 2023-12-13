(ns schedule-bot.core
  (:require [clojure.core.async :refer [<!!]]
            [schedule-bot.parse.parser :as parser]
            [schedule-bot.prettier :as prettier]
            [schedule-bot.messages.compact :as compact-message]
            [schedule-bot.messages.markup :as markup]
            [java-time.api :as jt]
            [morse.polling :as p]
            [morse.handlers :as h]
            [clojure.edn :as edn]
            [morse.api :as t])
  (:gen-class))

(defn- get-url [group-id start-date end-date]
  (str
   "https://guide.herzen.spb.ru/static/schedule_dates.php?"
   "id_group=" group-id
   "&date1=" start-date
   "&date2=" end-date))

(defn- try-get-schedule [url today parse-mode]
  (try
    (-> url
        parser/load-and-parse-schedule
        prettier/prettify
        (compact-message/create today url)
        (markup/to-str parse-mode))
    (catch Exception _ "⚠️ я упаль ⚠️")))

(defn- get-week [parse-mode]
  (let [today (jt/local-date (jt/zoned-date-time (jt/zone-id "Europe/Moscow")))
        week-after-today (jt/plus today (jt/days 6))
        url (get-url 19040 today week-after-today)]
    (try-get-schedule url today parse-mode)))

(defn- safe-slurp [file-path]
  (try
    (slurp file-path)
    (catch java.io.FileNotFoundException _ nil)))

(def token
  (let [dev-token (some-> ".ed" safe-slurp edn/read-string :bot-token)
        prod-token (System/getenv "BOT_TOKEN")]
    (or dev-token prod-token)))

(h/defhandler handler
  (h/command-fn "start"
                (fn [{{id :id} :chat}]
                  (t/send-text token id "Welcome to mybot start method!")))
  (h/command-fn "help"
                (fn [{{id :id} :chat}]
                  (t/send-text token id "Help is on the way")))
  (h/command-fn "week"
                (fn [{{id :id} :chat}]
                  (t/send-text token id {:parse_mode "html"} (get-week markup/html)))))

(defn -main [& args]
  (when (nil? token)
    (println "Bot token is needed!")
    (System/exit 1))

  (println "Starting schedule-bot")
  (<!! (p/start token handler)))
