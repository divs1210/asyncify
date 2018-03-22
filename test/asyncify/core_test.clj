(ns asyncify.core-test
  (:require [asyncify.core :as ac]
            [asyncify.test-utils :as atu]
            [clojure.core.async :as as]
            [clojure.test :refer :all]))

;; function under test
(defn expensive-fn []
  (Thread/sleep 1000)
  (rand-int 100))


(deftest core-tests
  ;; sync
  (let [[res time] (atu/time+
                    (doall (repeatedly 5 expensive-fn)))]
    (is (= 5 (count res)))
    (println "sync took" time "ms for 5 calls")
    (println "results:" res))

  (println "=====")

  ;; async on default pool
  (let [async-fn (ac/asyncify expensive-fn)
        [res time] (atu/time+
                    (doall (repeatedly 5 async-fn)))]
    (is (= 5 (count res)))
    (println "async on default pool took" time "ms for 5 calls")
    (let [[res time] (atu/time+
                      (mapv #(as/<!! %) res))]
      (println "results:" res)
      (println "actual time:" time "ms")))

  (println "=====")

  ;; async on custom pool
  (let [async-fn (ac/asyncify expensive-fn 5)
        [res time] (atu/time+
                    (doall (repeatedly 5 async-fn)))]
    (is (= 5 (count res)))
    (println "async on custom pool took" time "ms for 5 calls")
    (let [[res time] (atu/time+
                      (mapv #(as/<!! %) res))]
      (println "results:" res)
      (println "actual time:" time "ms"))))
