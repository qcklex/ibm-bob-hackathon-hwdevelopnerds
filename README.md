# Mutation-Testing Improvement Loop — `toolkit`

A Java Maven utility library used as a live target for iterative mutation-testing experiments driven by IBM Bob's **mutation-climb** skill.

---

## What this is

This repository contains a small Java utility library (`toolkit/`) with five classes — `DateRangeUtils`, `MoneyUtils`, `SimpleCache`, `StringUtils`, and `Validators` — paired with an existing JUnit 5 test suite. It is a concrete, measurable target for demonstrating and improving test quality through PIT mutation analysis.

Key files:

| File | Purpose |
|------|---------|
| [`toolkit/BASELINE.md`](toolkit/BASELINE.md) | Mutation-coverage snapshot recorded before any improvement rounds |
| [`run_round.sh`](run_round.sh) | One-command script: runs PIT, copies `mutations.xml`, and regenerates `survivors.md` + `rounds.csv` |
| [`survivors.md`](survivors.md) | Latest list of surviving mutants the test suite has not yet killed |
| [`rounds.csv`](rounds.csv) | Round-by-round log of line coverage and mutation score |

---

## The problem: line coverage says tests are good while bugs survive

Line coverage measures whether a line was *executed* — not whether the test *checked* that it did the right thing. The [`toolkit/BASELINE.md`](toolkit/BASELINE.md) snapshot makes the gap concrete:

| Metric | Value |
|--------|-------|
| Mutants generated | 287 |
| Mutants killed | 235 **(82%)** |
| Line coverage | 272 / 287 **(95%)** |

**95% line coverage. 82% mutation score.** That gap means roughly one in five logic mutations slips through the test suite undetected — the lines execute, but nothing asserts the outcome tightly enough to catch the change.

Three representative examples from [`survivors.md`](survivors.md):

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

1. **Runs PIT** via `mvn test-compile org.pitest:pitest-maven:mutationCoverage` inside `toolkit/`, writing raw output to `pit-output.txt`.
2. **Copies** `toolkit/target/pit-reports/mutations.xml` → `toolkit/mutations.xml`.
3. **Runs `report.py`** to parse `mutations.xml` and regenerate [`survivors.md`](survivors.md) and append a new row to [`rounds.csv`](rounds.csv).

After the script finishes, review [`survivors.md`](survivors.md) for the next batch of mutants to kill, add or strengthen tests in `toolkit/src/test/java/`, and run the script again.

To regenerate the progress chart at any time:

```sh
python chart.py
```

This reads [`rounds.csv`](rounds.csv) and writes `chart.png` (requires `matplotlib`).

---

## Results

| Round | Line Coverage (%) | Mutation Score (%) | Δ Mutation Score |
|-------|------------------:|-------------------:|----------------:|
| Baseline | 95.0 | 82.0 | — |
| 1 | | | |
| 2 | | | |
| 3 | | | |
| 4 | | | |
| 5 | | | |

> Fill in each row after running `bash run_round.sh`. Target: mutation score ≥ 90%.
