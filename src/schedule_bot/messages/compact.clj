(ns schedule-bot.messages.compact
  (:require [java-time.api :as jt]
            [schedule-bot.messages.markup :as markup]))

(defn- get-day-of-week [date]
  (condp #(%1 %2) date
    jt/monday? "Понедельник"
    jt/tuesday? "Вторник"
    jt/wednesday? "Среда"
    jt/thursday? "Четверг"
    jt/friday? "Пятница"
    jt/saturday? "Суббота"
    jt/sunday? "Воскресенье"))

(defn- get-day-line [date today]
  (let [tomorrow (jt/plus today (jt/days 1))
        label (condp = date
                today " (Сегодня)"
                tomorrow " (Завтра)"
                "")]
    (-> (markup/start)
        (markup/text "🗓 ")
        (markup/bold (get-day-of-week date))
        (markup/italic label)
        (markup/text "\n"))))

(defn- get-class-line [{name :name place :place group :group remote? :remote? [start _] :time}]
  (-> (markup/start)
      (markup/mono (if remote? "дист." (jt/format "HH:mm" start)))
      (markup/text " | ")
      (markup/text (if (some? group) (str "[" group "] ") ""))
      (markup/text name)
      (#(if (some? place)
          (-> %
              (markup/text ", ")
              (markup/italic place))
          %))
      (markup/text "\n")))

(defn- try-get-class-line [class]
  (try
    (get-class-line class)
    (catch Exception _
      (-> (markup/start)
          (markup/italic "⚠️ fail to parse class\n")))))

(defn- get-day [{classes :classes day :day} today]
  (let [class-lines (->> classes
                         (filter #(not (:empty %)))
                         (sort-by #(or (:group %) 0) <)
                         (sort-by #(-> % :time first) jt/<)
                         (map try-get-class-line))]
    (markup/combine
     (get-day-line day today)
     (apply markup/combine class-lines)
     (markup/start "\n"))))

(defn- get-link-line [link]
  (-> (markup/start)
      (markup/text "🌐 ")
      (markup/link "herzen.spb.ru" link)))

(defn create [days today link]
  (markup/combine
   (apply markup/combine (map #(get-day % today) (sort-by :day jt/< days)))
   (get-link-line link)))

