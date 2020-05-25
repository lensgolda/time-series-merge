(ns user)

(comment
  (require '[clojure.spec.gen.alpha :as gen])
  (require '[clojure.spec.alpha :as s])
  (require '[clojure.java.io :as io])
  (import (java.time LocalDate)
          (java.time.format DateTimeFormatter))

  (def time-iterator
    (iterate
      (fn [val]
        (.plusDays val 1)
        (LocalDate/of 2000 1 1))))

  (.exists (io/file "data/series.txt"))

  (with-open [w (io/writer "data/series.txt")]
    (letfn [(t-format [date]
              (.format date DateTimeFormatter/ISO_LOCAL_DATE))
            (make-record [date cnt]
              (str (t-format date) ":" cnt "\n"))]
      (doseq [record (map
                       make-record
                       (take 10 time-iterator)
                       (range 1 11))]
        (.write w record)))))