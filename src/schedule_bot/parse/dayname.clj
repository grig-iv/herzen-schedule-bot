(ns schedule-bot.parse.dayname
  (:require [net.cgrand.enlive-html :as html]
            [schedule-bot.parse.utils :refer [content]]
            [clojure.string :as string]
            [java-time.api :as jt]))

(defn get-day [dayname-node]
  (-> dayname-node
      (html/select [:th.dayname])
      first
      content
      first
      (string/split #", ")
      first
      (#(jt/local-date "d.MM.yyyy" %))))

(defn dayname-node? [node]
  (seq (html/select node [:th.dayname])))
