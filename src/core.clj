(ns core
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.tools.cli :refer [parse-opts]])
  (:refer-clojure :exclude [parse-opts])
  (:gen-class))


(def cli-options
  [["-f" "--file" "File names to read"
    :multi true
    :update-fn (fnil conj [])]
   ["-e" "--encoding ENCODING" "Provide files encoding"
    :default "US-ASCII"]
   ["-h" "--help"]])

(defn str->int
  [str-val]
  (Integer/parseInt str-val))

(defn process-file
  [file-name]
  (with-open [rdr (io/reader file-name :encoding "US-ASCII")]
    (into (sorted-map)
          (comp (map #(s/split % #":"))
                (map (fn [[date id]]
                       [date (str->int id)])))
          (line-seq rdr))))

(defn process-records
  [file-records]
  (loop [ts file-records
         acc (sorted-map)]
    (if-not (seq ts)
      acc
      (recur (rest ts)
             (reduce (fn [acc [k v]]
                       (if (contains? acc k)
                         (update acc k (fnil (partial + v) 0))
                         (assoc acc k v)))
                     acc
                     (first ts))))))


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
                                (map process-file)
                                (process-records)
                                (map #(s/join ":" %)))]
              (.write writer (str record "\n"))))))
      (catch Exception e
        (prn (.getMessage e) (ex-data e))))))
