(ns alandipert.priority-map
  (:require [cljs.core :as core])
  (:require-macros [cljs.core :as coreclj]))

(deftype PersistentPriorityMap [priority->set-of-items item->priority meta ^:mutable __hash]
  Object
  (toString [this]
    (str (seq this)))

  IWithMeta
  (-with-meta [this meta]
    (PersistentPriorityMap. priority->set-of-items item->priority meta __hash))

  IMeta
  (-meta [this] meta)

  ICollection
  (-conj [this entry]
    (if (vector? entry)
      (-assoc this (-nth entry 0) (-nth entry 1))
      (reduce -conj this entry)))

  IEmptyableCollection
  (-empty [this] (with-meta
                   alandipert.priority-map.PersistentPriorityMap/EMPTY
                   meta))

  IEquiv
  (-equiv [this other]
    (-equiv item->priority other))

  IHash
  (-hash [this]
    (coreclj/caching-hash this core/hash-imap __hash))

  ISeqable
  (-seq [this]
    (seq (for [[priority item-set] priority->set-of-items, item item-set]
           [item priority])))

  IReversible
  (-rseq [coll]
    (seq (for [[priority item-set] (rseq priority->set-of-items), item item-set]
           [item priority])))

  ICounted
  (-count [this]
    (count item->priority))

  ILookup
  (-lookup [this item]
    (get item->priority item))
  (-lookup [coll item not-found]
    (get item->priority item not-found))

  IStack
  (-peek [this]
    (when-not (empty? this)
      (let [f (first priority->set-of-items)]
        [(first (val f)) (key f)])))
  (-pop [this]
    (if (empty? this)
      (throw (js/Error. "Can't pop empty priority map"))
      (let [f (first priority->set-of-items)
            item-set (val f)
            item (first item-set)
            priority (key f)]
        (if (= (count item-set) 1)
          (PersistentPriorityMap.
           (dissoc priority->set-of-items priority)
           (dissoc item->priority item)
           meta
           nil)
          (PersistentPriorityMap.
           (assoc priority->set-of-items priority (disj item-set item)),
           (dissoc item->priority item)
           meta
           nil)))))

  IAssociative
  (-assoc [this item priority]
    (if-let [current-priority (get item->priority item nil)]
      (if (= current-priority priority)
        this
        (let [item-set (get priority->set-of-items current-priority)]
          (if (= (count item-set) 1)
            (PersistentPriorityMap.
             (assoc (dissoc priority->set-of-items current-priority)
               priority (conj (get priority->set-of-items priority #{}) item))
             (assoc item->priority item priority)
             meta
             nil)
            (PersistentPriorityMap.
             (assoc priority->set-of-items
               current-priority (disj (get priority->set-of-items current-priority) item)
               priority (conj (get priority->set-of-items priority #{}) item))
             (assoc item->priority item priority)
             meta
             nil))))
      (PersistentPriorityMap.
       (assoc priority->set-of-items
         priority (conj (get priority->set-of-items priority #{}) item))
       (assoc item->priority item priority)
       meta
       nil)))

  (-contains-key? [this item]
    (contains? item->priority item))

  IMap
  (-dissoc [this item]
    (let [priority (item->priority item ::not-found)]
      (if (= priority ::not-found)
        this
        (let [item-set (priority->set-of-items priority)]
          (if (= (count item-set) 1)
            (PersistentPriorityMap.
             (dissoc priority->set-of-items priority)
             (dissoc item->priority item)
             meta
             nil)
            (PersistentPriorityMap.
             (assoc priority->set-of-items priority (disj item-set item)),
             (dissoc item->priority item)
             meta
             nil))))))

  IFn
  (-invoke [this item]
    (-lookup this item))
  (-invoke [this item not-found]
    (-lookup this item not-found)))

(set! alandipert.priority-map.PersistentPriorityMap/EMPTY
      (PersistentPriorityMap. (sorted-map) {} {} nil))

(defn- pm-empty-by [comparator]
  (PersistentPriorityMap. (sorted-map-by comparator) {} {} nil))

(defn priority-map
  "keyval => key val
  Returns a new priority map with supplied mappings."
  ([& keyvals]
     (loop [in (seq keyvals) out alandipert.priority-map.PersistentPriorityMap/EMPTY]
       (if in
         (recur (nnext in) (assoc out (first in) (second in)))
         out))))

(defn priority-map-by
  "keyval => key val
  Returns a new priority map with supplied
  mappings, using the supplied comparator."
  ([comparator & keyvals]
     (loop [in (seq keyvals) out (pm-empty-by comparator)]
       (if in
         (recur (nnext in) (assoc out (first in) (second in)))
         out))))