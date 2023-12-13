(ns schedule-bot.parse.parser-test
  (:require [net.cgrand.enlive-html :as html]
            [java-time.api :as jt]
            [schedule-bot.parse.parser :as parser]))

(def schedule-html
  (html/html-resource "schedule.html"))

(def day-schedule
  (parser/parse-schedule schedule-html))

(defn- get-day [day]
  (first (filter #(jt/= (:day %) day) day-schedule)))

(get-day (jt/local-date 2023 12 12))

;; TODO write tests
