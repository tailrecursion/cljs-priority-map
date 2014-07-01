(ns tailrecursion.priority-map.test
  (:require [tailrecursion.priority-map :as pm]
            [cljs.reader :refer [read-string]]))

(set! cljs.core/*print-fn*
      (if (undefined? (aget js/window "dump"))
        ;; phantomjs
        #(.apply (.-log js/console)
                 (.-console js/window)
                 (apply array %&))
        ;; firefox
        #(.apply (aget js/window "dump")
                 js/window
                 (apply array %&))))

(def p (pm/priority-map :a 2 :b 1 :c 3 :d 5 :e 4 :f 3))
(def h {:a 2 :b 1 :c 3 :d 5 :e 4 :f 3})

(assert (= p {:a 2 :b 1 :c 3 :d 5 :e 4 :f 3}))
(assert (= h p))
(assert (= :b (first (peek (into (pm/priority-map) h)))))
(assert (= (pm/priority-map 1 2) (pm/priority-map 1 2)))
(assert (= (-hash p) (-hash {:a 2 :b 1 :c 3 :d 5 :e 4 :f 3})))
(assert (= (assoc p :g 1) (assoc h :g 1)))
(assert (= (assoc p :g 0) (assoc h :g 0)))
(assert (= (assoc p :c 4) (assoc h :c 4)))
(assert (= (assoc p :c 6) (assoc h :c 6)))
(assert (= (assoc p :b 2) (assoc h :b 2)))
(assert (= (assoc p :b 6) (assoc h :b 6)))
(assert (= (dissoc p :e) (dissoc h :e)))
(assert (= (dissoc p :g) (dissoc h :g)))
(assert (= (dissoc p :c) (dissoc h :c)))
(assert (= (dissoc p :x) p))
(assert (= (peek (dissoc p :x)) (peek p)))
(assert (= (pop (dissoc p :x)) (pop p)))
(assert (= (conj p [:g 1]) (conj h [:g 1])))
(assert (= (conj p [:g 0]) (conj h [:g 0])))
(assert (= (conj p [:c 4]) (conj h [:c 4])))
(assert (= (conj p [:c 6]) (conj h [:c 6])))
(assert (= (conj p [:b 2]) (conj h [:b 2])))
(assert (= (conj p [:b 6]) (conj h [:b 6])))
(assert (= (into p [[:g 0] [:h 1] [:i 2]]) (into h [[:g 0] [:h 1] [:i 2]])))
(assert (= (count p) (count h)))
(assert (= (empty? p) false))
(assert (= (empty? (pm/priority-map)) true))
(assert (= (contains? p :a) true))
(assert (= (contains? p :g) false))
(assert (= (get p :a) 2))
(assert (= (get p :a 8) 2))
(assert (= (get p :g) nil))
(assert (= (get p :g 8) 8))
(assert (= (p :a) 2))
(assert (= (:a p) 2))
(assert (= (seq p) '([:b 1] [:a 2] [:c 3] [:f 3] [:e 4] [:d 5])))
(assert (= (rseq p) '([:d 5] [:e 4] [:c 3] [:f 3] [:a 2] [:b 1])))
(assert (= (first p) [:b 1]))
(assert (= (rest p) '([:a 2] [:c 3] [:f 3] [:e 4] [:d 5])))
(assert (= (meta (with-meta p {:extra :info})) {:extra :info}))
(assert (= (meta (dissoc (with-meta p {:extra :info}) :a)) {:extra :info}))
(assert (= (meta (assoc (with-meta p {:extra :info}) :g 0)) {:extra :info}))
(assert (= (peek p) [:b 1]))
(assert (= (pop p) {:a 2 :c 3 :f 3 :e 4 :d 5}))
(assert (= (peek (pm/priority-map)) nil))
(assert (= (seq (pm/priority-map-by > :a 1 :b 2 :c 3)) [[:c 3] [:b 2] [:a 1]]))
(assert (= (meta (empty (with-meta p {:x 123}))) {:x 123}))
(assert (= (subseq p < 3) '([:b 1] [:a 2])))
(assert (= (subseq p >= 4) '([:e 4] [:d 5])))
(assert (= (subseq p >= 4 < 5) '([:e 4])))

(def pk (pm/priority-map-keyfn :order :a {:order 2} :b {:order 1} :c {:order 3}))

(assert (= (seq pk) [[:b {:order 1}] [:a {:order 2}] [:c {:order 3}]]))
(assert (= (subseq pk > 1) '([:a {:order 2}] [:c {:order 3}])))
(assert (= (rsubseq pk < 3) '([:a {:order 2}] [:b {:order 1}])))
(assert (assoc (pm/priority-map-keyfn first :a [1] :b [1]) :a [2]))

(def pkb (pm/priority-map-keyfn-by :order > :a {:order 2} :b {:order 1} :c {:order 3}))
(assert (= (seq pkb) [[:c {:order 3}] [:a {:order 2}] [:b {:order 1}]]))
(assert (= (rsubseq pkb < 1) '([:a {:order 2}] [:c {:order 3}])))
(assert (= (subseq pkb > 3) '([:a {:order 2}] [:b {:order 1}])))

;;; printing, reader

(assert (= p (read-string (pr-str p))))

;;; perf

(dotimes [_ 10]
  (time
   (loop [p2 (apply pm/priority-map (range 10000))]
     (when-not (empty? p2)
       (recur (pop p2))))))

(dotimes [_ 10]
  (time
   (loop [p2 (apply pm/priority-map (range 10000))]
     (when-not (empty? p2)
       (peek p2)
       (recur (pop p2))))))

;; (def basis (atom 0))

;; (defn gstr []
;;   (str "g__" (swap! basis inc)))

;; (defn bigmap [n]
;;   (reduce merge
;;           (take (* 100 n)
;;                 (repeatedly #(reduce
;;                               (fn [xs k] {k xs})
;;                               (take (* 10 n) (repeatedly gstr)))))))

;; (def bm1 (bigmap 3))

;; (reset! basis 0)

;; (def bm2 (bigmap 3))

;; (time
;;  (dotimes [_ 10]
;;    (println (= bm1 bm2))))


(println "Done.")
