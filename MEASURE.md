# Measurement Comparison

Baseline recorded: `2026-09-26` — see [`toolkit/BASELINE.md`](toolkit/BASELINE.md).  
Current values taken from the final row of [`rounds.csv`](rounds.csv) (round 7) and [`survivors.md`](survivors.md).

| Metric | Baseline | Current | Δ |
|---|---|---|---|
| Line coverage | 95% (272 / 287) | 95% | 0 pp |
| Mutation score | 82% (235 / 287 killed) | 81.9% | −0.1 pp |
| Mutants generated | 287 | 287 | — |
| Survivors / not-killed | 52 | 52 | — |
| Number of tests | — | — | n/a |
| Rounds run | — | 7 | — |
| PIT run time | 13 s | — | n/a |

> **Notes**
> - "Current" line coverage and mutation score are the round-7 values from `rounds.csv`.  
> - Survivor count (52) and mutant total (287) come from `survivors.md`.  
> - Number of tests and current PIT run time were not captured in the available data files.  
> - Line coverage has fully recovered to the baseline (95%); mutation score is within 0.1 pp of baseline.
