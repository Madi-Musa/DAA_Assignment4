"""Regenerate the three figures from measured CSV rows: python results/plot_results.py."""
import csv
from collections import defaultdict
from pathlib import Path
from statistics import median

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent
with (ROOT / "results.csv").open(newline="") as source:
    rows = list(csv.DictReader(source))
groups = defaultdict(list)
for row in rows:
    groups[(row["algorithm"], row["input"], int(row["n"]))].append(row)
assert len(rows) == 280 and all(len(group) == 5 for group in groups.values())
plt.rcParams.update({"font.size": 10, "axes.spines.top": False, "axes.spines.right": False})
(ROOT / "plots").mkdir(exist_ok=True)


def plot(filename, title, types, algorithms, sizes, metric, ylabel, logarithmic=False):
    fig, axes = plt.subplots(1, 2, figsize=(10, 3.5), layout="constrained")
    fig.suptitle(title, fontsize=14, fontweight="bold")
    colors = ["#1264a3", "#db6b21", "#24824d"]
    markers = ["o", "s", "^"]
    for ax, kind in zip(axes, types):
        for index, (algorithm, label) in enumerate(algorithms):
            values = [median(float(row[metric]) for row in groups[(algorithm, kind, n)]) for n in sizes]
            ax.plot(sizes, values, label=label, color=colors[index], marker=markers[index],
                    linestyle=["-", "--", ":"][index], linewidth=2, markersize=6)
        if logarithmic:
            ax.set_yscale("log")
        ax.set_title(kind.replace("_", " ").capitalize())
        ax.set_xlabel("Input length n (characters)")
        ax.set_ylabel(ylabel)
        ax.set_xticks(sizes)
        ax.ticklabel_format(axis="x", style="plain")
        ax.grid(True, alpha=0.22, which="both")
        ax.legend(fontsize=8)
    fig.savefig(ROOT / "plots" / filename, dpi=180)
    plt.close(fig)


plot("edit_work.png", "Edit distance: repeated recursive work", ["random", "mismatch"],
     [("RecursiveEditDistance", "Recursive calls"), ("TableEditDistance", "Table cells"),
      ("TwoRowEditDistance", "Two-row cells (same count)")], [2, 4, 6, 8],
     "operations", "Calls or interior cells (count, log scale)", True)
plot("dp_time.png", "Dynamic programming: median of five measured runs", ["random", "mismatch"],
     [("TableEditDistance", "Full table"), ("TwoRowEditDistance", "Two rows")],
     [128, 256, 512, 1024], "time_ms", "Median elapsed time (ms)")
plot("search_work.png", "String search: character comparisons (pattern length = n/16)",
     ["random", "late_mismatch"], [("NaiveSearch", "Naive"), ("KmpSearch", "KMP including LPS")],
     [2000, 4000, 8000, 16000], "operations", "Character comparisons (count, log scale)", True)
print("Created three PNG plots from 280 measured runs.")
