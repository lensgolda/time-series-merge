(ns core
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import (java.time LocalDate)
           (java.time.format DateTimeFormatter)))

(defn -main [])

(defn date->str
  [date]
  (.format date DateTimeFormatter/ISO_LOCAL_DATE))

(defn str->date
  [str-date]
  (LocalDate/parse str-date))

(defn str->int
  [str-val]
  (Integer/parseInt str-val))

(defn process-file
  [file-name]
  (with-open [rdr (io/reader file-name :encoding "US-ASCII")]
    (into #{}
          (comp (map #(s/split % #":"))
                (map (fn [[d id]]
                       [(str->date d) (str->int id)])))
          (line-seq rdr))))

(defn process-files
  [& files]
  (into #{}
        (comp (mapcat (fn [file] (process-file file))))
        files))

(defn process-result
  [file-records]
  (->> file-records
       (group-by first)
       (map (fn [[date v]]
              (let [ids-grouped (reduce (fn [acc v1]
                                          (conj acc (second v1)))
                                        []
                                        v)]
                [(date->str date) (apply + ids-grouped)])))
       (into (sorted-set) (comp (map #(s/join ":" %))))))

(with-open [wrtr (io/writer "data/results.txt" :encoding "US-ASCII")]
  (doseq [record (->> (process-files "data/series_1.txt" "data/series_2.txt")
                      (process-result))]
    (.write wrtr (str record "\n"))))
