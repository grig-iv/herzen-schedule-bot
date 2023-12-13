(ns schedule-bot.messages.markup)

(defn start
  ([] (list))
  ([text] (list text)))

(defn text [markup text]
  (conj markup text))

(defn mono [markup text]
  (conj markup (list :mono text)))

(defn italic [markup text]
  (conj markup (list :italic text)))

(defn bold [markup text]
  (conj markup (list :bold text)))

(defn link [markup text url]
  (conj markup (list :link text url)))

(defn combine [& markups]
  (apply clojure.core/concat (reverse markups)))

(defn html [token]
  (cond
    (nil? token) ""
    (string? token) token
    (= :mono (first token)) (str "<code>" (second token) "</code>")
    (= :italic (first token)) (str "<i>" (second token) "</i>")
    (= :bold (first token)) (str "<b>" (second token) "</b>")
    (= :link (first token)) (str "<a href=\"" (second (next token)) "\">" (second token) "</a>")))

(defn plain-text [token]
  (cond
    (nil? token) ""
    (string? token) token
    (= :mono (first token))  (second token)
    (= :italic (first token)) (second token)
    (= :bold (first token)) (second token)
    (= :link (first token)) (second token)))

(defn to-str [markup renderer]
  (apply str (map renderer (reverse markup))))

