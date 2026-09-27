# Measurement Comparison

Baseline recorded: `2026-09-26` — see [`toolkit/BASELINE.md`](../toolkit/BASELINE.md).  
Current values are the final row of [`rounds.csv`](../results/rounds.csv) (round 4), produced by `sh run_round.sh` from the repo root.

| Metric | Baseline (round 1) | Current (round 4) | Δ |
|---|---|---|---|
| Line coverage | 95% (267 / 282) | 99% (278 / 282) | +4 pp |
| Mutation score | 81.8% (216 / 264 killed) | 95.8% (253 / 264 killed) | +14.0 pp |
| Mutants generated | 264 | 264 | 0 |
| Survivors / not-killed | 48 | 11 | −37 |
| Number of tests | 321 | 396 | +75 |
| Measured rounds | — | 4 | — |
| PIT run time | 17 s | 23 s | — |

## Round-by-round log

| Round | What changed | Line Coverage (%) | Mutation Score (%) | Δ Mutation Score |
|------:|--------------|------------------:|-------------------:|-----------------:|
| 1 | Baseline (the sample project's own tests) | 95.0 | 81.8 | — |
| 2 | First loop: new `StringUtils` tests | 95.0 | 84.5 | +2.7 |
| 3 | Subagents: new tests for all five classes | 99.0 | 95.8 | +11.3 |
| **4** | Stronger assertions (no new kills) | **99.0** | **95.8** | 0.0 |

> Target mutation score ≥ 90% — **reached at round 3 (95.8%)** and held at round 4.

## Where the 37 extra kills came from

The baseline and round 4 contain the same 264 mutants (the library code did not change), so they compare one-to-one.

- **16** were in code the old tests already executed but never checked well enough (sharper assertions).
- **21** were in code no test executed (`NO_COVERAGE`), so new tests had to reach it first.
- **11** mutants survive: 9 `SURVIVED` and 2 `NO_COVERAGE`. Seven of them are equivalent mutants that no test can kill — see [`equivalent-mutants.md`](equivalent-mutants.md).

## Notes

- Every figure here comes from a clean build (`mvn clean`) and reproduces from a fresh `git clone` followed by `sh run_round.sh`.
- The first logging of these rounds (287 mutants, 96.2%) used an incrementally built `target/` folder whose stale classes added 23 extra mutants. It did not reproduce from a fresh clone, so every round was re-measured from a clean build at the commit where the tests last changed. `run_round.sh` now runs `mvn clean` to prevent this.
- Rounds that repeated an unchanged test suite (the original rounds 7 to 12 and 19) are not listed, because they add no information.
- Single-class runs on `StringUtils` are kept separately in [`rounds_stringutils.csv`](../results/rounds_stringutils.csv): 85.3% to 95.6% of 68 mutants, with 3 equivalent survivors left.
- 396 tests ran in the round-4 PIT execution (1.5 tests per mutant).
