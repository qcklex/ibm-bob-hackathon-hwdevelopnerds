# Measurement Comparison

Baseline recorded: `2026-09-26` — see [`toolkit/BASELINE.md`](toolkit/BASELINE.md).  
Current values taken from the final row of [`rounds.csv`](rounds.csv) (round 20) and the clean-clone run of `sh run_round.sh`.

| Metric | Baseline | Current (Round 20) | Δ |
|---|---|---|---|
| Line coverage | 95% (272 / 287) | 99% (278 / 282) | +4 pp |
| Mutation score | 82% (235 / 287 killed) | 95.8% (253 / 264 killed) | +13.8 pp |
| Mutants generated | 287 | 264 | −23 |
| Survivors / not-killed | 52 | 11 | −41 |
| Number of tests | — | 396 | — |
| Rounds run | — | 20 | — |
| PIT run time | 13 s | 24 s | — |

## Round-by-round log

| Round | Line Coverage (%) | Mutation Score (%) | Δ Mutation Score |
|------:|------------------:|-------------------:|-----------------:|
| Baseline | 95.0 | 82.0 | — |
| 7 | 95.0 | 81.9 | −0.1 |
| 8 | 95.0 | 81.9 | 0.0 |
| 9 | 95.0 | 81.9 | 0.0 |
| 10 | 95.0 | 81.9 | 0.0 |
| 11 | 95.0 | 81.9 | 0.0 |
| 12 | 95.0 | 81.9 | 0.0 |
| 17 | 95.0 | 84.3 | +2.4 |
| 18 | 99.0 | 96.2 | +11.9 |
| 19 | 99.0 | 96.2 | 0.0 |
| **20** | **99.0** | **95.8** | −0.4 |

> Target mutation score ≥ 90% — **achieved at round 18 (96.2%)** and held through round 20 (95.8%). ✅

> **Notes**
> - "Current" line coverage and mutation score are round-20 values from a clean `git clone` + `sh run_round.sh`.
> - Mutant count dropped from 287 (baseline) to 264 (round 20) because the test suite now reaches previously uncovered code, eliminating `NO_COVERAGE` mutants.
> - Survivor count fell from 52 (baseline) to 11 (round 20).
> - 396 tests ran in the round-20 PIT execution (1.5 tests per mutant).
