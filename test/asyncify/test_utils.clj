(ns asyncify.test-utils)

(defn time* [f]
  (let [start (System/currentTimeMillis)
        result (f)
        end (System/currentTimeMillis)]
    [result
     (- end start)]))

(defmacro time+ [& body]
  `(time*
    (fn []
      ~@body)))
