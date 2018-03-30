# asyncify

An event loop library for Clojure

## Usage

Let's say we have a `fn` that hits the database:
```clojure
(defn get-name [id]
  (Thread/sleep 50)
  (str "name_" id))
```

That we use like this:
```clojure
(time
  (doseq [id (range 100)
          :let [name (get-name id)]]
    (println name)))

;; loop takes ~5000 ms
```

Each call to `get-name` blocks the calling thread when the system could be doing useful work.
The best way to solve this is to use an async database driver and modify your code accordingly.
But when an async driver is not available, we can simulate our own:
```clojure
(require '[asyncify.core :refer [asyncify]])

(def a:get-name
  (asyncify get-name))
```

Now our code becomes:
```clojure
(require '[clojure.core.async :as a])

(time
 (doseq [id (range 100)
         :let [name-chan (a:get-name id)]]
   (a/go
     (println (a/<! name-chan)))))

;; loop takes ~5 ms
```

Let's see what we did there:
* Used `asyncify` to ceate an event-driven version of `get-name` that runs on `core.async`'s threadpool. We can also supply a threadpool explicitly:
```clojure
(require '[com.climate.claypoole :as cp])

(def pool
  (cp/threadpool 3))

(def a:get-name
  (asyncify get-name pool))
```
* Called the async version of `get-name` instead of the original
* Moved code after the call to `a:get-name` into a `go` block

## License

Copyright © 2018 Divyansh Prakash

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
