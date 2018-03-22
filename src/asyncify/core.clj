(ns asyncify.core
  (:require [clojure.core.async :as a]
            [com.climate.claypoole :as cp]))

(defn asyncify
  "Returns an asynchronous version of `f` (`af`),
  that takes the same parameters,
  but returns a channel instantly.
  Takes one optional parameter: `n-or-threadpool`
  that determines on which threadpool `af` will run:
  - nil: Clojure's default agent pool
  - number: a new threadpool of given size
  - threadpool: given threadpool
  (threadpool = ExecutorService or claypoole/threadpool)"
  ([f]
   (asyncify f nil))
  ([f n-or-threadpool]
   (let [in-chan (a/chan)
         threadpool (condp #(%1 %2) n-or-threadpool
                      nil? :builtin
                      number? (cp/threadpool n-or-threadpool)
                      cp/threadpool? n-or-threadpool)]
     (cp/future threadpool
                (loop []
                  (let [[out-chan args] (a/<!! in-chan)
                        result (apply f args)]
                    (a/put! out-chan result))
                  (recur)))
     (fn [& args]
       (let [out-chan (a/chan)]
         (a/go
           (a/>! in-chan [out-chan args])
           (a/<! out-chan)))))))
