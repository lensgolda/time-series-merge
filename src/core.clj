(ns core
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.tools.cli :refer [parse-opts]])
  (:refer-clojure :exclude [parse-opts])
  (:import (java.time LocalDate)
           (java.time.format DateTimeFormatter))
  (:gen-class))


(declare cli-options date->str str->date str->int process-file process-files process-result)

(defn -main [& args]
  (let [{:keys [arguments options] :as cli} (parse-opts args cli-options)]
    (when (:help options)
      (println (:summary cli))
      (System/exit 0))
    (try
      (if (empty? arguments)
        (throw (ex-info "No files provided to merge" {:arguments arguments}))
        (let [encoding (get options :encoding "US-ASCII")]
          (doseq [file arguments]
            (when-not (.exists (io/file file))
              (throw (ex-info "File not exists: " {:file file}))))
          (with-open [writer (io/writer "data/result" :encoding encoding)]
            (doseq [record (->> arguments
                                (apply process-files)
                                (process-result))]
              (.write writer (str record "\n"))))))
      (catch Exception e
        (prn (.getMessage e) (ex-data e))))))

(def cli-options
  [["-f" "--file" "File names to read"
    :multi true
    :update-fn (fnil conj [])]
   ["-e" "--encoding ENCODING" "Provide files encoding"
    :default "US-ASCII"]
   ["-h" "--help"]])

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
       (map (fn [[date value]]
              (let [ids-grouped (reduce (fn [acc inner-value]
                                          (conj acc (second inner-value)))
                                        []
                                        value)]
                [(date->str date) (apply + ids-grouped)])))
       (into (sorted-set) (comp (map #(s/join ":" %))))))
