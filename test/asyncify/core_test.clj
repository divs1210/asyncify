(ns asyncify.core-test
  (:require [asyncify.core :as ac]
            [asyncify.test-utils :as atu]
            [clojure.core.async :as as]
            [clojure.test :refer :all]
            [com.climate.claypoole :as cp]))

(def ^:const WAIT-MS 500)

;; function under test
(defn get-name [id]
  (Thread/sleep WAIT-MS)
  (str "name_" id))

(defonce pool
  (cp/threadpool 2))

(defonce a:get-name
  (ac/asyncify get-name pool))


(deftest core-tests
  (let [n 10
        res-chan (as/chan n)
        _ (doseq [i (range n)
                  :let [name-chan (a:get-name i)]]
            (as/go
              (as/>! res-chan
                     (as/<! name-chan))))
        time (atu/time+
              (dotimes [_ n]
                (as/<!! res-chan)))]
    (is (< time (* n WAIT-MS))
        "asyncified fn runs on the given threadpool")
    (println "Time taken to run" n "queries @" WAIT-MS "ms/query:" time "ms")
    (as/close! res-chan)))
