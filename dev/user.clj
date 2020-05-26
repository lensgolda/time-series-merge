(ns user)

(comment
  (require '[clojure.java.io :as io])
  (import (java.time LocalDate)
          (java.time.format DateTimeFormatter))

  ;; Iterates through the time =)
  (def time-iterator
    (iterate
      (fn [val]
        (.plusDays val 1))
      (LocalDate/of 2000 1 1)))

  ;; check file exists
  (.exists (io/file "data/series_1.txt"))
  (.exists (io/file "data/series_2.txt"))

  ;; generate time records to files
  (with-open [w (io/writer "data/series_1.txt")]
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
      (into #{}
            (comp (map #(s/split % #":"))
                  (map (fn [[d id]]
                         [(str->date d) (str->int id)])))
            (line-seq rdr))))

  ;; process bunch of files
  (defn process-files
    [& files]
    (into #{}
          (comp (mapcat (fn [file] (process-file file))))
          files))

  (->> (process-files "data/series_1.txt" "data/series_2.txt")
       (process-result)))