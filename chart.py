import csv
import matplotlib.pyplot as plt


def load_csv(path: str) -> tuple[list[int], list[float], list[float]]:
    rounds, line_cov, mut_score = [], [], []
    with open(path, newline="") as f:
        for row in csv.DictReader(f):
            # Skip rows where either metric is missing
            if not row["line_coverage"] or not row["mutation_score"]:
                continue
            rounds.append(int(row["round"]))
            line_cov.append(float(row["line_coverage"]))
            mut_score.append(float(row["mutation_score"]))
    return rounds, line_cov, mut_score


def label_endpoints(ax, x: list, y: list, color: str) -> None:
    """Annotate the first and last data points of a line."""
    indices = [0, -1] if len(x) > 1 else [0]
    for idx in indices:
        ax.annotate(
            f"{y[idx]:.1f}",
            xy=(x[idx], y[idx]),
            xytext=(6, 4),
            textcoords="offset points",
            fontsize=8,
            color=color,
        )


def main() -> None:
    rounds, line_cov, mut_score = load_csv("rounds.csv")

    fig, ax = plt.subplots(figsize=(8, 5))

    color_lc = "#2563eb"   # blue
    color_ms = "#16a34a"   # green

    ax.plot(rounds, line_cov,  marker="o", color=color_lc, label="Line Coverage (%)")
    ax.plot(rounds, mut_score, marker="s", color=color_ms, label="Mutation Score (%)")

    label_endpoints(ax, rounds, line_cov,  color_lc)
    label_endpoints(ax, rounds, mut_score, color_ms)

    ax.set_xlabel("Round")
    ax.set_ylabel("Score (%)")
    ax.set_title("Line Coverage & Mutation Score per Round")
    ax.set_xticks(rounds)
    ax.legend()
    ax.grid(axis="y", linestyle="--", alpha=0.4)

    fig.tight_layout()
    fig.savefig("chart.png", dpi=150)
    print("Saved chart.png")


if __name__ == "__main__":
    main()
