(ns schedule-bot.parse.class
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]
            [java-time.api :as jt]
            [schedule-bot.parse.utils :refer [content]]))

(defn- get-place [class-node]
  (-> class-node
      content
      last
      (str/replace #"^," "")
      str/trim))

(defn- get-name [class-node]
  (or
   (-> class-node
       content
       first
       content
       first
       content
       first)
   (-> class-node
       content
       first
       content
       first)))

(defn- empty-class? [class-node]
  (= '("—") (content class-node)))

(defn- get-class-data [class-node]
  (if (empty-class? class-node)
    {:empty true}
    {:name (get-name class-node)
     :place (get-place class-node)}))

(defn- get-time-slot [classes-node]
  (-> classes-node
      (html/select [:tr :th])
      first
      content
      first
      (str/split #" — ")
      ((fn [x] (map #(jt/local-time "H:mm" %) x)))))

(defn get-classes [classes-node]
  (let [time-slot (get-time-slot classes-node)
        class-nodes (html/select classes-node [:tr :td])
        split? (= 2 (count class-nodes))]
    (map-indexed
     (fn [i class-node]
       (-> class-node
           get-class-data
           (assoc :time time-slot)
           (#(if split? (assoc % :group (inc i)) %))))
     class-nodes)))

(defn classes-node? [node]
  (seq (html/select node [:tr.lowline])))
