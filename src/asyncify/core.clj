(ns asyncify.core
  (:require [clojure.core.async :as a]
            [com.climate.claypoole :as cp]))

(defonce in-chan
  (a/chan))


(defn- execute
  "Call `f` with `args` and put the
  returned value on to `out-chan`."
  [f args out-chan]
  (let [result (try
                 (apply f args)
                 (catch Exception _))]
    (if (some? result)
      (a/put! out-chan result)
      (a/close! out-chan))))


(defonce event-loop
  (a/thread
    (loop []
      (let [[pool f args out-chan] (a/<!! in-chan)]
        (if (= :builtin pool)
          (a/go
            (execute f args out-chan))
          (cp/future
            pool
            (execute f args out-chan))))
      (recur))))


(defn asyncify
  "Returns an asynchronous version of `f`: `af`,
  that takes the same parameters,
  but returns a channel immediately.
  `af` runs on the `core.async` threadpool,
  unless one is explicitly passed.
  `threadpool` = ExecutorService or claypoole/threadpool"
  ([f]
   (asyncify f :builtin))
  ([f threadpool]
   {:pre [(fn? f)
          (or (cp/threadpool? threadpool)
              (= :builtin threadpool))]}
   (fn [& args]
     (let [out-chan (a/chan)]
       (a/go
         (a/>! in-chan [threadpool f args out-chan])
         (a/<! out-chan))))))
