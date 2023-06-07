(ns queens-army.deno
  (:require [queens-army.core :as core]
            ["https://deno.land/x/keypress@0.0.11/mod.ts" :as kp]))

(def args (aget js/Deno "args"))
(def difficulty (or (get args 0) "normal"))
(def title (core/title difficulty))

(defonce actions
  (atom (core/init difficulty)))

(println title)
(println "[Ctrl+C] exit")
(println core/menu)

(def keypress (kp/readKeypressSync))

(loop [pressed (.next keypress)]
  (let [event    (aget pressed "value")
        key      (aget event "key")
        ctrl-key (aget event "ctrlKey")]
    (if (not (and (= key "c") ctrl-key))
      (if (not core/over)
        (do (core/act println "⇢ " actions key)
          (recur (.next keypress)))))))
