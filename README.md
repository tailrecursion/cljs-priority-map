# cljs-priority-map

[![Under Construction Gifs](https://dl.dropbox.com/u/12379861/underconstruction.gif)](http://www.animatedgif.net/underconstruction/construction.shtml)

*This library is currently under construction.  Code and documentation are subject to frequent change.*

A priority map is very similar to a sorted map, but whereas a sorted
map produces a sequence of the entries sorted by key, a priority map
produces the entries sorted by value.

This implementation is a ClojureScript port of Mark Engelberg's
[clojure.data.priority-map](https://github.com/clojure/data.priority-map)
for Clojure.

[![Build Status](https://travis-ci.org/alandipert/cljs-priority-map.png?branch=master)](https://travis-ci.org/alandipert/cljs-priority-map)

## Usage

### Dependency

```clojure
[alandipert/cljs-priority-map "1.0.0-SNAPSHOT"]
```

### Example

```clojure
;; Require or use alandipert.priority-map in your namespace.

(ns your-ns
  (:require [alandipert.priority-map :refer [priority-map])))

;; The standard way to construct a priority map is with priority-map:

(def p (priority-map :a 2 :b 1 :c 3 :d 5 :e 4 :f 3))

p ;=> {:b 1, :a 2, :c 3, :f 3, :e 4, :d 5}

;; So :b has priority 1, :a has priority 2, and so on.  Notice how the
;; priority map prints in an order sorted by its priorities (i.e., the
;; map's values)

;; We can use assoc to assign a priority to a new item:

(assoc p :g 1) ;=> {:b 1, :g 1, :a 2, :c 3, :f 3, :e 4, :d 5}

;; or to assign a new priority to an extant item:

(assoc p :c 4) ;=> {:b 1, :a 2, :f 3, :c 4, :e 4, :d 5}

;; We can remove an item from the priority map:

(dissoc p :e) ;=> {:b 1, :a 2, :c 3, :f 3, :d 5}

;; An alternative way to add to the priority map is to conj a [item priority] pair:

(conj p [:g 0]) ;=> {:g 0, :b 1, :a 2, :c 3, :f 3, :e 4, :d 5}

(into p [[:g 0] [:h 1] [:i 2]]) ;=> {:g 0, :b 1, :h 1, :a 2, :i 2, :c 3, :f 3, :e 4, :d 5}

;; Priority maps are countable:

(count p) ;=> 6

;; Like other maps, equivalence is based not on type, but on contents.
;; In other words, just as a sorted-map can be equal to a hash-map, so
;; can a priority-map.

(= p {:b 1, :a 2, :c 3, :f 3, :e 4, :d 5}) ;=> true

;; You can test them for emptiness:

(empty? (priority-map)) ;=> true

(empty? p) ;=> false

;; You can test whether an item is in the priority map:

(contains? p :a) ;=> true

(contains? p :g) ;=> false

;; It is easy to look up the priority of a given item, using any of
;; the standard map mechanisms:

(get p :a) ;=> 2

(get p :g 10) ;=> 10

(p :a) ;=> 2

(:a p) ;=> 2

;; Priority maps derive much of their utility by providing priority-based
;; seq.  Note that no guarantees are made about the order in which items
;; of the same priority appear.

(seq p) ;=> ([:b 1] [:a 2] [:c 3] [:f 3] [:e 4] [:d 5])

;; Because no guarantees are made about the order of same-priority items,
;; note that rseq might not be an exact reverse of the seq.  It is only
;; guaranteed to be in descending order.

(rseq p) ;=> ([:d 5] [:e 4] [:c 3] [:f 3] [:a 2] [:b 1])

;; This means first/rest/next/for/map/etc. All operate in priority order.

(first p) ;=> [:b 1]

(rest p) ;=> ([:a 2] [:c 3] [:f 3] [:e 4] [:d 5])

;; Priority maps support metadata:

(meta (with-meta p {:extra :info})) ;=> {:extra :info}

;; But perhaps most importantly, priority maps can also function as
;; priority queues.  peek, like first, gives you the first
;; [item priority] pair in the collection.  pop removes the first
;; [item priority] from the collection.  (Note that unlike rest, which
;; returns a seq, pop returns a priority map).

(peek p) ;=> [:b 1]

(pop p) ;=> {:a 2, :c 3, :f 3, :e 4, :d 5}

;; It is also possible to use a custom comparator:

(priority-map-by > :a 1 :b 2 :c 3) ;=> {:c 3, :b 2, :a 1}
```

## Testing

[PhantomJS](http://phantomjs.org/) is used for unit testing.  With it
installed, you can run the tests like so:

    lein cljsbuild test

## License

Copyright © 2013 Alan Dipert

Distributed under the Eclipse Public License, the same as Clojure.
