(ns queens-army.deno
  (:require [queens-army.core :as core]))

(def args (aget js/Deno "args"))
(def difficulty (or (get args 0) "normal"))
(def title (core/title difficulty))

(defonce actions
  (atom (core/init difficulty)))

(println title)
(println "[Ctrl+C] exit\n")
(println core/menu)

(loop [key (js/prompt "command?")]
  (if (not core/over)
    (do (core/act println "⇢ " actions key)
      (recur (js/prompt "command?")))))
