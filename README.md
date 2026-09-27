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

---

## Using this on another project

Three things here are specific to this repo: the `toolkit/` subfolder path, the `com.example.toolkit.*` package in `pom.xml`, and the fact that `report.py` parses PIT's XML report format. Everything else — the loop logic, the CSV/chart tracking, the Bob skill itself — is already generic.

### Another Java/Maven repo (no rewrite needed)

Copy four things into the new repo:

- [`.bob/skills/mutation-climb/SKILL.md`](.bob/skills/mutation-climb/SKILL.md) + [`.bob/skills/mutation-climb/scripts/report.py`](.bob/skills/mutation-climb/scripts/report.py)
- [`.bob/custom_modes.yaml`](.bob/custom_modes.yaml) (the Test Engineer mode)
- [`run_round.sh`](run_round.sh)
- [`chart.py`](chart.py)

Then edit three spots to match the new project:

1. **`pom.xml`** — add the same `pitest-maven` + `pitest-junit5-plugin` block as [`toolkit/pom.xml`](toolkit/pom.xml), but point `targetClasses` at the new package:
   ```xml
   <targetClasses>
       <param>com.example.toolkit.*</param>   <!-- → the new project's package -->
   </targetClasses>
   ```
2. **`run_round.sh`** — it hardcodes `toolkit/` in the `mvn -f` and `cp` lines. If the new repo's `pom.xml` is at the repo root, drop the `toolkit/` prefix from both; otherwise point it at the real path. `report.py` and `chart.py` need no changes — they only know about `mutations.xml`, `rounds.csv`, and `survivors.md`, all of which are project-agnostic.
3. **`SKILL.md`** and **`custom_modes.yaml`** — both reference `toolkit/src/test/java/` and `toolkit/pom.xml` by name. Find/replace `toolkit/` with the new project's real path (or remove it if `pom.xml` is at root).

That's it — Bob follows the same loop (`/mutation-climb`, read survivors, write tests, re-run) on the new codebase.

### A non-Java project

The loop's shape — run tool → parse report → append `rounds.csv` → chart → Bob writes tests targeting survivors — carries over to any mutation-testing tool. What actually has to change is one function:

| Piece | Java / PIT (this repo) | JS/TS | Python |
|---|---|---|---|
| Mutation tool | PIT | Stryker | mutmut |
| `run_round.sh`'s tool-invocation line | `mvn ... pitest-maven:mutationCoverage` | `npx stryker run` | `mutmut run` |
| `report.py`'s `parse_xml()` | reads PIT's `mutations.xml` | rewrite to read Stryker's JSON report | rewrite to read `mutmut results` output |
| Everything downstream (CSV append, `survivors.md`, `chart.py`, the skill's Steps 3–7) | — | unchanged | unchanged |

`parse_xml()` in [`report.py`](.bob/skills/mutation-climb/scripts/report.py) is the only piece tied to PIT's output format. The skill's decision logic — plateau detection, target percentage, survivor prioritization — is already language-agnostic.
