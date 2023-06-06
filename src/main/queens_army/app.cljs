(ns queens-army.app
  (:require [queens-army.core :as core]))

(def params (js/URLSearchParams. (aget js/location "search")))
(def difficulty (or (.get params "difficulty") "normal"))
(def title (core/title difficulty))

(def cli (.getElementById js/document "command-line"))
(def body (aget js/document "body"))

(defonce actions
  (atom (core/init difficulty)))

(aset js/document "title" title)

(defn writeln [& args]
  (let [text (aget cli "innerHTML")]
    (aset cli "innerHTML" (.trimStart (str (apply str (cons text (interpose " " args))) "\n")))))

(defn init []
  (writeln title)
  (writeln core/menu))

(.addEventListener body "keydown"
  (fn [event]
    (let [key (aget event "key")]
      (if (not core/over)
        (core/act writeln actions key))
      (.scrollTo js/window 0 (aget body "scrollHeight")))))
