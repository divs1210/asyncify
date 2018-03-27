(ns asyncify.test-utils)

(defn time* [f]
  (let [start (System/currentTimeMillis)
        _ (f)
        end (System/currentTimeMillis)]
    (- end start)))

(defmacro time+ [& body]
  `(time*
    (fn []
      ~@body)))
