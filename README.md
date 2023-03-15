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

### Solution

Tested with:

- Clojure v. `1.11.1` (Default)
- Java `11`


Result stores into `./data/result file`

Zero dependencies accept clojure.tools.cli used for CLI

### How to use

#### REPL

Starts socketed REPL on port 50505:
```shell script
clj -A:socket
```

#### Run with CLI

help:
```shell script
clj -M -m core -h
```

Outputs:
```shell script
-f, --file                         File names to read
-e, --encoding ENCODING  US-ASCII  Provide files encoding (Default US-ASCII)
-h, --help
```

Process files with encoding.
By default runs with 1.10.1 Clojure version:
```shell script
clj -M -m core -f data/file_1 data/file_2 data/file_3 -e US-ASCII
```


#### uberjar

Build the uberjar without AOT compilation:
```shell script
clojure -A:uberjar -m hf.depstar.uberjar TimeSeriesMerge.jar
```

Run it:
```shell script
java -cp TimeSeriesMerge.jar clojure.main -m core args
```