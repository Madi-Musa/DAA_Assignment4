# DAA Assignment 4 - Dynamic Programming and String Algorithms

Java implementations of recursive, full-table and two-row Levenshtein Edit Distance, plus Naive and KMP string search. Search returns all zero-based matches, including overlaps; empty patterns throw `IllegalArgumentException`. Algorithms use Java UTF-16 `char` units. Each family shares an interface, and counters are recorded inside the implementations.

## Build and test

Use JDK 17 or newer and Maven 3.9 or newer, with `JAVA_HOME` pointing to the JDK and Maven on `PATH`. Open `pom.xml` as a Maven project in IntelliJ IDEA.

```sh
mvn clean verify
mvn test
```

JUnit 5 is the only application test dependency. Eight test methods cover the assignment references, 100 random edit pairs, 100 random search texts, edge cases, counters and generator repeatability. Maven writes the test reports to `target/surefire-reports/`. The recorded run used Windows, Oracle JDK 25.0.1, Java 17 bytecode and IntelliJ's bundled Maven; its launcher emitted compatibility warnings but the build and tests succeeded.

## Reproduce the experiments

Run from the project root:

```sh
mvn package
java -cp target/classes edu.daa.BenchmarkRunner
```

This replaces `results/results.csv` with 280 individual measured runs (56 cases, five runs each) and prints their median times. Three warm-ups per case are excluded. The seed is 20261102, with deterministic per-case seeds. Input fingerprints, operation counts and answers reproduce exactly; elapsed times vary with the JVM and system conditions. Optional output path, useful for checking reproducibility without replacing the submitted data:

```sh
java -cp target/classes edu.daa.BenchmarkRunner target/recheck.csv
```

CSV columns: `algorithm,input,n,m,run,time_ms,operations,lps_comparisons,search_comparisons,answer,seed,input_sha256`. `answer` is edit distance or number of matches. `operations` means recursive calls including base cases, interior DP cells, or character comparisons; KMP includes LPS and search comparisons. Non-search rows have zero in the two search-specific columns. Input generation, answer checks, fingerprinting and file writing occur outside the timed region; algorithm allocation and KMP preprocessing are included. Counters reset per call; instances are intended for sequential use.

## Regenerate plots

Python 3 and Matplotlib are only needed to regenerate the charts, not to build or run the Java project:

```sh
python -m pip install matplotlib
python results/plot_results.py
```

The plotting script reads the CSV and writes three PNGs in `results/plots/`. The saved plots were generated with Matplotlib 3.11.2. `REPORT.md` contains the original committed design card, measured findings, figure takeaways and four short answers. It is organized into four print sections using HTML page breaks (print at A4 with normal margins and images fitted to page width; Markdown viewers differ in pagination).

## Layout

```text
src/main/java/edu/daa/     Interfaces, five algorithms, input generator, runner
src/test/java/edu/daa/     Algorithm and counter JUnit tests
results/results.csv      All 280 measured runs
results/plot_results.py   Reproducible plotting script
results/plots/            Three PNG figures
REPORT.md                Experiment report
README.md
pom.xml
```

## Submission and AI acknowledgement

Repository: https://github.com/Madi-Musa/DAA_Assignment4 (branch `main`, final tag `v1.0`). The submission ZIP is produced from the final tagged tracked files, excluding `.git`, IDE configuration, dependencies and compiled output. Rename `DAA_Assignment4_name_surname_group.zip` with your actual name, surname and group before uploading it to Moodle, and include the repository link. Personal details were not supplied and have not been inferred from the repository account. The assignment deadline is 8 November 2026 at 23:59, as stated in the assignment document.

AI-use acknowledgement: OpenAI Codex generated the implementation, tests, experiment design, benchmark and plotting code, and documentation, and ran the reported checks and measurements in this workspace. The student should review the code and conclusions, understand the algorithms, and comply with the course's AI-use rules. The measurements are actual program output; no personal review or authorship by the student is claimed here.
