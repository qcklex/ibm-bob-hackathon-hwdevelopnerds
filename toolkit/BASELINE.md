# PIT Mutation Coverage Baseline

Recorded from: `mvn clean test-compile org.pitest:pitest-maven:mutationCoverage`
Date: 2026-09-26 (re-recorded from a clean build of the baseline commit `37ecc77`)

## Summary

| Metric            | Value                                   |
| ----------------- | --------------------------------------- |
| Mutants generated | 264                                     |
| Mutants killed    | 216 (82%)                               |
| Survivors         | 48 (25`SURVIVED`, 23 `NO_COVERAGE`) |
| Line coverage     | 267/282 (95%)                           |
| Tests run         | 321                                     |
| Run time          | 17 seconds                              |

> The first recording of this baseline (287 mutants, 235 killed) came from an incrementally
> built `target/` folder and did not reproduce from a fresh clone. It was replaced with this
> clean-build measurement; `run_round.sh` now runs `mvn clean` so it cannot happen again.
