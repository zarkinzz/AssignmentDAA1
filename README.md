# Assignment 1 – Divide and Conquer Algorithms

## Overview

This project implements and analyses classic Divide & Conquer (D&C) algorithms with:

* **Safe recursion patterns** (bounded depth)
* **Runtime recurrences** (Master Theorem & Akra–Bazzi intuition)
* **Experimental validation**: time, recursion depth, comparisons/allocations
  All work follows a clean GitHub workflow (branches, PRs, commits).

---

## Implemented Algorithms

### MergeSort (Master Theorem – Case 2)

* Linear merge with **one reusable buffer**
* **Small-n cutoff** → Insertion Sort

### QuickSort (robust)

* **Randomized pivot**
* **Recurse smaller side**, iterate larger one (stack ≈ O(log n) avg)

### Deterministic Select (Median-of-Medians)

* Groups of 5 → **median-of-medians** pivot
* **3-way partition**, recurse **only** the needed side (prefer smaller side)

### Closest Pair of Points (2D)

* Sort by **x**, recursive split
* **Strip** check by **y** order (≤ ~7 neighbor checks)

---

## Metrics

For each run we collect:

* **Execution time** (ns)
* **Max recursion depth**
* **Number of key comparisons**
* **Logical allocations** (e.g., merge buffer, strip buffer)

Results are written to **CSV** for plotting.

CSV header:

```
Algorithm,n,Trial,TimeNs,Depth,Comparisons,Allocations
```

---

## Analysis (short)

* **MergeSort:** (T(n)=2T(n/2)+\Theta(n)) → **Master Case 2** → (\Theta(n\log n)).
* **QuickSort (randomized):** not a clean Master; **Akra–Bazzi intuition** → expected (\Theta(n\log n)); depth (O(\log n)) with smaller-first recursion.
* **Select (MoM5):** (T(n)\le T(n/5)+T(7n/10)+\Theta(n)) → **(\Theta(n))**.
* **Closest Pair:** (T(n)=2T(n/2)+\Theta(n)) (strip linear) → **Master Case 2** → (\Theta(n\log n)).

---

## Testing

* Sorting: correctness on random/adversarial arrays; depth bounded for QuickSort.
* Select: result equals `Arrays.sort(a)[k]` (random trials).
* Closest Pair: matches (O(n^2)) brute force on small n (≤ 2000).

Run all tests:

```bash
mvn test
```

---

## Build & Packaging

```bash
# Clean & build
mvn clean package
```

This produces:

* CLI jar: `target/AssignmentDAA1-1.0-SNAPSHOT.jar`
* JMH fat-jar: `target/benchmarks.jar`

---

## How to Run (CLI)

**Main class:** `org.example.SortCLI`

### Command

```bash
java -jar target/AssignmentDAA1-1.0-SNAPSHOT.jar \
  --algo <merge|quick|select|closest> \
  --n <size> \
  --trials <t> \
  --csv <file> \
  [--seed <s>] \
  [--k <index>]     # only for select; default n/2
```

### Flag details

* `--algo` — which algorithm to run (`merge`, `quick`, `select`, `closest`)
* `--n` — number of elements (or points for closest pair)
* `--trials` — how many times to repeat (rows appended to CSV)
* `--csv` — output CSV file (default: `AssignmentDAA1.csv`)
* `--seed` — RNG seed (optional)
* `--k` — order statistic for Select (0..n-1); default `n/2`

### Examples

```bash
# MergeSort
java -jar target/AssignmentDAA1-1.0-SNAPSHOT.jar --algo merge   --n 200000 --trials 3 --csv results.csv

# QuickSort
java -jar target/AssignmentDAA1-1.0-SNAPSHOT.jar --algo quick   --n 200000 --trials 3 --csv results.csv

# Deterministic Select (k = n/2)
java -jar target/AssignmentDAA1-1.0-SNAPSHOT.jar --algo select  --n 200000 --trials 3 --csv results.csv

# Closest Pair (2D points)
java -jar target/AssignmentDAA1-1.0-SNAPSHOT.jar --algo closest --n  50000  --trials 3 --csv results.csv
```

---

## Benchmark (JMH)

The project includes a microbenchmark to compare **Select** vs **full sorting**.

### Run

```bash
# Build if not yet
mvn -q -DskipTests=true package

# JMH fat-jar
java -jar target/benchmarks.jar "SelectVsSortBench.*" -wi 3 -i 5 -f 1
# Optional: change params
# java -jar target/benchmarks.jar "SelectVsSortBench.*" -p n=20000 -p seed=7
# Optional: write JMH results to CSV
# java -jar target/benchmarks.jar "SelectVsSortBench.*" -rf csv -rff jmh_results.csv
```

---

## Notes

* **Cutoff + insertion sort** help cache on tiny ranges.
* **Reusable buffer** reduces allocations and GC noise.
* **Smaller-first recursion** keeps QuickSort stack ≈ (O(\log n)).
* **Strip** in Closest Pair uses few comparisons (≤ ~7 per point).

---
