# Mutation-Testing Improvement Loop — Problem & Solution

## The problem

Software teams routinely ship test suites that achieve 90–100% line coverage and declare quality "done." Line coverage measures whether a line of code was *executed* during a test run — not whether any test actually *checked* that the code produced the correct result. The gap is invisible until production finds it.

PIT mutation testing makes the gap visible. PIT introduces tiny, deliberate logic changes — flipping a conditional, swapping an arithmetic operator, removing a return value — and re-runs the test suite for each change. If the suite still passes, the mutant "survives," which means that logic change could have been a real bug and no test would have caught it.

## Who it is for

Java developers and QA engineers who already have a JUnit test suite and believe their coverage numbers reflect real quality. It is especially useful for teams maintaining utility or domain libraries where boundary conditions and edge cases carry the most risk.

## How a developer uses it

The developer clones the repository and runs a single command from the project root:

```sh
sh run_round.sh
```

The script compiles the project with Maven, runs PIT across all 264 mutants, and produces two output files: `results/survivors.md`, a precise list of every surviving mutant with its file, line number, and mutation type; and a new row in `results/rounds.csv` recording line coverage and mutation score for that round. The developer reads `survivors.md`, writes one or more focused JUnit tests targeting the surviving mutants, and runs the script again. Running `python chart.py` at any point renders a progress chart from `rounds.csv`. The loop repeats until the mutation score reaches the target or no improvable survivors remain.

## Why it is original

Most mutation-testing guides show PIT output once and stop. This project treats mutation testing as a *feedback loop*: structured round logging, a diff-able survivor file, an automated chart, and a documented baseline make each iteration measurable and reproducible. The skill implemented in IBM Bob automates the loop end-to-end — reading survivors, generating targeted tests, re-running PIT, and deciding whether to continue — so a developer can drive a full improvement campaign from a single Bob prompt.

## Measured results

| Metric | Baseline | After improvement rounds |
|---|---|---|
| Line coverage | 95% | 99% |
| Mutation score | 81.8% (216 / 264 killed) | 95.8% (253 / 264 killed) |
| Surviving mutants | 48 | 11 (7 are equivalent mutants no test can kill) |
| Measured rounds | — | 4 |

Line coverage rose from 95% to 99%, but 25 of the 48 baseline survivors sat in lines the old tests already executed. Mutation score climbed from 81.8% to 95.8% over four measured rounds: 37 of the 48 survivors (77%) are now killed. Every figure comes from a clean build, so it reproduces from a fresh clone.
