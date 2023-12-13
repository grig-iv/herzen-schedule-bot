(ns schedule-bot.prettier
  (:require [clojure.string :as string]))

(defn- replace-in [map key old-sub new-sub]
  (update map key #(string/replace % old-sub new-sub)))

(defn- remove-in [map key sub]
  (replace-in map key sub ""))

(defn- prettify-foreigen-language [{name :name :as class}]
  (let [[_ language] (re-matches #"Иностранный язык \((.*)\)" name)]
    (->> (or language name)
         string/capitalize
         (assoc class :name))))

(defn- prettify-class-name [class]
  (-> class
      (update :name #(or % "⚠️"))
      (remove-in :name #" \(элективная дисциплина\)")
      (remove-in :name #"Факультатив: ")
      (replace-in :name #"Физическая культура и спорт" "Физкультура")
      prettify-foreigen-language))

(defn- remove-pe-place [{name :name place :place :as class}]
  (if (and
       (= name "Физкультура")
       (= place "ауд.1 - спортивный зал"))
    (assoc class :place nil)
    class))

(defn- update-remote [class]
  (if (re-find #"дистанционное обучение" (or (:place class) ""))
    (-> class
        (assoc :place nil)
        (assoc :remote? true))
    class))

(defn- prettify-class-place [class]
  (-> class
      (update :place #(or % "⚠️"))
      (remove-in :place #", корпус 20 \(Мойка 48\)")
      remove-pe-place
      update-remote))

(defn- prettify-class [class]
  (if (:empty class)
    class
    (-> class
        prettify-class-name
        prettify-class-place)))

(defn- prettify-day [day]
  (update day :classes #(map prettify-class %)))

(defn prettify [days]
  (map prettify-day days))
