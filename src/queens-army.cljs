(require '[clojure.string :as string])

(def difficulty (or (#{"easy" "normal" "hard"} (nth (concat js/process.argv [""]) 3)) "normal"))

(def difficulties {
  "easy" #(case (get % :action) (2 4) (dissoc % :pouch) %)
  "hard" #(case (get % :action) 4 (assoc % :pouch :saltpeter) %)
})

(def cards
  (group-by :action
    (shuffle
      (map (get difficulties difficulty identity)
       (flatten [
         (repeat 2 {:title "Guild" :action 1})
         {:title "Improve" :action 1}
         (repeat 2 {:title "Influence" :action 1})
         (repeat 4 {:title "Migrate" :action 1})
         {:title "Move" :action 1}
         {:title "Disperse" :action 2 :pouch :sulfer}
         (repeat 3 {:title "Guild" :action 2})
         (repeat 2 {:title "Improve" :action 2})
         {:title "Influence" :action 2 :pouch :sulfer}
         {:title "Migrate" :action 2 :pouch :sulfer}
         {:title "Move" :action 2}
         {:title "Move" :action 2 :pouch :sulfer}
         (repeat 5 {:title "Conquer" :action 3})
         (repeat 2 {:title "Improve" :action 3})
         (repeat 3 {:title "Influence" :action 3})
         {:title "Disperse" :action 4 :pouch :saltpeter}
         {:title "Explore" :action 4 :pouch :saltpeter}
         {:title "Harvest" :action 4 :pouch :saltpeter}
         (repeat 3 {:title "Improve" :action 4})
         (repeat 2 {:title "Move" :action 4})
         {:title "Move" :action 4 :pouch :saltpeter}
         {:title "Tax" :action 4 :pouch :saltpeter}
         {:title "Conquer" :action 5}
         {:title "Disperse" :action 5}
         {:title "Explore" :action 5}
         {:title "Guild" :action 5}
         {:title "Harvest" :action 5}
         {:title "Improve" :action 5}
         (repeat 2 {:title "Influence" :action 5})
         {:title "Migrate" :action 5}
         {:title "Tax" :action 5}
       ])))))

(def directions [:n :e :s :w])
(def interactions [:push :pull :trade])
(def regions [:islands :sea :desert :forest :badlands :mountains])
(def guilds [:farmer :merchant :alchemist :knight :noble :monk])
(def cubes [:saltpeter :sulfter :iron :wood :food])

(defn sided [n]
  (range 1 (inc (int n))))

(defn roll [& dice]
  (mapv
    (fn [sides]
      (nth (apply vector sides) (rand-int (count sides))))
    dice))

(defn word [x]
  (string/capitalize (if (keyword? x) (name x) (str x))))

(defn render-dice [faces]
  (string/join " " (map word faces)))

(defn render-card [{round :round action :action title :title cube :pouch}]
  (str "Round " round ", Action " action ": " title (when cube (str " +" (word cube)))))

(defn extra-action? [cards]
  (= (get (nth cards 3) :pouch) :saltpeter))

(defn extra-action [cards]
  (apply vector
    (if (extra-action? cards)
      cards
      (take 4 cards))))

(def actions
  (atom
    (apply concat
      (map extra-action
        (partition 5
          (for [round (range 1 11)
                action (range 1 6)]
            (assoc (get-in cards [action (dec round)]) :round round)))))))

(def sep "⇢")
(def menu "\nOptions:\n  [q] queen\n  [f] fate\n  [p] progress\n  [l] location\n  [r] region\n  [d] direction\n  [g] guild\n  [i] interaction\n  [c] cube\n[2-9] random #\n  [?] show options\n")

(println "Feudum: The Queen's Army" (str "(" difficulty ")"))
(println menu)

(js/process.stdin.resume)
(js/process.stdin.setEncoding "utf8")
(js/process.stdin.setRawMode true)
(js/process.stdin.on "data"
  #(case %
    "\u0003" (js/process.exit)
    "q" (do
          (if-let [card (first @actions)]
            (println "queen" sep (render-card card))
            (js/process.exit))
          (swap! actions rest))
    "f" (println "fate" sep (render-dice (roll interactions directions)))
    "p" (println "progress" sep (render-dice (roll regions)))
    "l" (println "location" sep (render-dice (roll directions regions)))
    "r" (println "region" sep (render-dice (roll regions)))
    "d" (println "direction" sep (render-dice (roll directions)))
    "g" (println "guild" sep (render-dice (roll guilds)))
    "i" (println "interaction" sep (render-dice (roll interactions)))
    "c" (println "cube" sep (render-dice (roll cubes)))
    "?" (println menu)
    ("2" "3" "4" "5" "6" "7" "8" "9")
      (println (str "# from 1 to " %) sep (render-dice (roll (sided %))))
    (identity)))
