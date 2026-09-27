# Mutation-Testing Improvement Loop — `toolkit`

A Java Maven utility library used as a live target for iterative mutation-testing experiments driven by IBM Bob's **mutation-climb** skill.

---

## What this is

This repository contains a small Java utility library (`toolkit/`) with five classes — `DateRangeUtils`, `MoneyUtils`, `SimpleCache`, `StringUtils`, and `Validators` — paired with an existing JUnit 5 test suite. It is a concrete, measurable target for demonstrating and improving test quality through PIT mutation analysis.

Key files:

| File | Purpose |
|------|---------|
| [`toolkit/BASELINE.md`](toolkit/BASELINE.md) | Mutation-coverage snapshot recorded before any improvement rounds |
| [`run_round.sh`](run_round.sh) | One-command script: runs PIT, copies `mutations.xml`, and regenerates `results/survivors.md` + `results/rounds.csv` |
| [`results/survivors.md`](results/survivors.md) | Latest list of surviving mutants the test suite has not yet killed |
| [`results/rounds.csv`](results/rounds.csv) | Round-by-round log of line coverage and mutation score. **Whole project only** (264 mutants), so every row is comparable |
| [`results/rounds_stringutils.csv`](results/rounds_stringutils.csv) | The T10 rounds on `StringUtils` alone (68 mutants). Kept apart because a single-class score is not comparable with the whole-project score |
| [`docs/`](docs/) | Write-ups: measurement comparison, video script, problem/solution and Bob-usage statements, equivalent-mutants analysis |

---

## The problem: line coverage says tests are good while bugs survive

Line coverage measures whether a line was *executed* — not whether the test *checked* that it did the right thing. The [`toolkit/BASELINE.md`](toolkit/BASELINE.md) snapshot makes the gap concrete:

| Metric | Value |
|--------|-------|
| Mutants generated | 264 |
| Mutants killed | 216 **(82%)** |
| Line coverage | 267 / 282 **(95%)** |

**95% line coverage. 82% mutation score.** That gap means roughly one in five logic mutations slips through the test suite undetected — the lines execute, but nothing asserts the outcome tightly enough to catch the change.

Three representative survivors from that baseline run:

| File | Line | Mutation | Why it survives |
|------|------|----------|-----------------|
| `MoneyUtils.java` | 26 | changed conditional boundary | Test covers the line but never exercises the exact boundary value |
| `Validators.java` | 128 | negated conditional | No test reaches this overload of `inRange` at all (`NO_COVERAGE`) |
| `SimpleCache.java` | 142 | `isExpired` returns `false` | Happy-path test never checks the expired branch |

Mutation score is the metric that matters.

---

## How to run one round

### Requirements

| Tool | Minimum version | Notes |
|------|----------------|-------|
| Java | 17 | `java -version` must report 17 or higher |
| Maven | 3.8 | `mvn -version` must report 3.8 or higher; used to compile and run PIT |
| Python | 3.9 | Used by `chart.py` to regenerate `chart.png`; requires `matplotlib` (`pip install matplotlib`) |

### Run

From the repo root:

```sh
sh run_round.sh
```

[`run_round.sh`](run_round.sh) does three things in sequence:

1. **Runs PIT** via `mvn clean test-compile org.pitest:pitest-maven:mutationCoverage` inside `toolkit/`, writing raw output to `results/pit-output.txt`. `clean` forces a full rebuild, so every number reproduces from a fresh clone.
2. **Copies** `toolkit/target/pit-reports/mutations.xml` → `toolkit/mutations.xml`.
3. **Runs `report.py`** to parse `mutations.xml` and regenerate [`survivors.md`](results/survivors.md) and append a new row to [`rounds.csv`](results/rounds.csv).

After the script finishes, review [`survivors.md`](results/survivors.md) for the next batch of mutants to kill, add or strengthen tests in `toolkit/src/test/java/`, and run the script again.

To regenerate the progress chart at any time:

```sh
python chart.py
```

This reads [`rounds.csv`](results/rounds.csv) and writes `chart.png` (requires `matplotlib`). To plot another log, pass its path: `python chart.py results/rounds_stringutils.csv --out stringutils.png`.

---

## Results

| Round | What changed | Line Coverage (%) | Mutation Score (%) | Δ Mutation Score |
|------:|--------------|------------------:|-------------------:|-----------------:|
| 1 | Baseline (the sample project's own tests) | 95.0 | 81.8 | — |
| 2 | First loop: new `StringUtils` tests | 95.0 | 84.5 | +2.7 |
| 3 | Subagents: new tests for all five classes | 99.0 | 95.8 | +11.3 |
| **4** | Stronger assertions (no new kills) | **99.0** | **95.8** | 0.0 |

> Target mutation score ≥ 90% — **reached at round 3 (95.8%)** and held at round 4. Every row was measured from a clean build, so it reproduces from a fresh clone: 264 mutants, 253 killed, 11 survivors, 396 tests. Seven of the 11 survivors are equivalent mutants that no test can kill — see [`equivalent-mutants.md`](docs/equivalent-mutants.md).
