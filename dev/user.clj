(ns user)

(comment
  (require '[clojure.java.io :as io])
  (require '[clojure.string :as s])
  (import (java.time LocalDate)
          (java.time.format DateTimeFormatter))

  (add-tap clojure.pprint/pprint)

  ;; Iterates through the time ) to generate time series, lazy
  (def time-iterator
    (iterate
     (fn [val]
       (.plusDays val 1))
     (LocalDate/of 2000 1 1)))


  (defn str->int
    [str-val]
    (Integer/parseInt str-val))


  ;; check file exists
  (.exists (io/file "data/series_1"))
  (.exists (io/file "data/series_2"))

  ;; generate time records to files
  (with-open [w (io/writer "data/series_1")]
    (letfn [(t-format [date]
              (.format date DateTimeFormatter/ISO_LOCAL_DATE))
            (make-record [date cnt]
              (str (t-format date) ":" cnt "\n"))]
      (doseq [record (map
                      make-record
                      (take 10 time-iterator)
                      (range 1 11))]
        (.write w record))))

  ;; process file
  (defn process-file
    [file-name]
    (with-open [rdr (io/reader file-name :encoding "US-ASCII")]
      (into (sorted-map)
            (comp (map #(s/split % #":"))
                  (map (fn [[date id]]
                         [date (str->int id)])))
            (line-seq rdr))))

  (process-file "data/series_1")

  ;; process-all
  (defn process-all
    [& files]
    (->> files
         (filter #(.exists (io/file %)))
         (map process-file)))


  ;;main loop
  (loop [ts (process-all "data/series_1" "data/series_2" "data/series_3")
         acc (sorted-map)]
    (if-not (seq ts)
      acc
      (recur (rest ts)
             (reduce (fn [acc [date id]]
                       (if (contains? acc date)
                         (update acc date (fnil (partial + id) 0))
                         (assoc acc date id)))
                     acc
                     (first ts)))))

  ;; process bunch of files


  )