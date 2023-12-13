(ns schedule-bot.parse.parser
  (:require [net.cgrand.enlive-html :as html]
            [schedule-bot.parse.dayname :refer [dayname-node? get-day]]
            [schedule-bot.parse.class :refer [classes-node? get-classes]]))

(defn- parse-rows [[curr & rest :as acc] node]
  (cond
    (dayname-node? node) (conj acc {:day (get-day node) :classes '()})
    (classes-node? node) (conj rest (update curr :classes  into (get-classes node)))))

(defn- get-table-rows [html]
  (html/select html [:table.schedule :tbody :tr]))

(defn parse-schedule [html]
  (->> html
       get-table-rows
       (reduce parse-rows [])))

(defn load-and-parse-schedule [url]
  (-> url
      java.net.URL.
      html/html-resource
      parse-schedule))
