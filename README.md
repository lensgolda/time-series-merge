# time-series-merge

### Problem: Time Series Merge

Time series are stored in files with the following format:

- files are multiline plain text files in ASCII encoding
- each line contains exactly one record
- each record contains date and integer value; records are encoded like so: YYYY-MM-DD:X
- dates within single file are non-duplicate and sorted in ascending order
- files can be bigger than RAM available on target host

Implement an algorithm accepting names of files as arguments, which merges two input files into one output file. Result file
should follow the same format conventions as described above. Records with the same date value should be merged into
one by summing up X values.
Optional bonus points can be acquired by implementing the same merge function for any or all of the following:

- solution implemented in Clojure
- arbitrary number of input files
- duplicate dates within single file, sorted in ascending order

### Run

```clojure
;; runs socket repl on port 50505
clj -A:socket

;; run program
clj -m core
```
