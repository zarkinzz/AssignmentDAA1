# Divide-and-Conquer

## How to Run
- Open the project in IntelliJ IDEA (just choose **Open** and select the folder).
- Set Java SDK version 17 or higher.
- To check the code, run:  
  `dnc.test.TestRunner`
- To use the program, run:  
  `dnc.cli.Main` with options:
    - `--algo mergesort|quicksort|select|closest|all`
    - `--n <size>` – number of elements
    - `--trials <t>` – how many times to repeat
    - `--out metrics.csv` – save results to a file

**Examples:**


## Project Notes
- **QuickSort**: goes deeper only on the smaller side, so memory is used better (about log n).
- **MergeSort**: uses one buffer for speed, small arrays use insertion sort.
- **Select (Median of Medians)**: looks only at the part with the k-th element.
- **Closest Pair**: keeps arrays sorted and checks only up to 7 neighbors.

## Metrics
- `comparisons`: counts every comparison of numbers.
- `allocations`: counts memory use (new arrays).
- `max_depth`: shows maximum recursion depth.
- Results are saved as CSV:  
  `algo,n,trial,time_ns,max_depth,comparisons,allocations,notes`

## Recurrence (Theory)
- **MergeSort**: Θ(n log n)
- **QuickSort (random)**: Θ(n log n), depth ~ log n
- **Select (Median of Medians)**: Θ(n)
- **Closest Pair**: Θ(n log n)

## Plots
- Data is in the CSV file.

## Summary
- Compare the real results with theory.
- Small differences may be because of cache, garbage collection, or constants.