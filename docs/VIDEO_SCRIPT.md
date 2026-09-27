# Video Script — Mutation-Testing Improvement Loop (~2:20)

---

## Segment 1 — The Problem (0:00 – 0:20)

**[Screen: `toolkit/BASELINE.md` open in editor]**

> "This Java library has a full JUnit 5 suite and 95% line coverage — green across the board.
> But run PIT mutation testing instead: it flips a conditional, changes a boundary, removes a
> return value, and checks whether any test actually fails.
>
> **82% mutation score.** Roughly one in five logic mutations slips through completely
> undetected — the lines ran, but nothing checked the outcome. Line coverage can't see that
> gap. Mutation score can."

**[Pause on the two numbers: 95% line coverage · 82% mutation score]**

---

## Segment 2 — Running the Loop (0:20 – 2:00)

### 2a — First command (0:20 – 0:35)

**[Screen: terminal at repo root]**

> "With Java, Maven and Python installed, every round is one command:"

```sh
sh run_round.sh
```

> "It compiles the project, runs PIT, and writes two files: `results/survivors.md` — the
> mutants still alive — and a new row in `results/rounds.csv`."

**[Show terminal scrolling through Maven/PIT output — let it run, do not skip]**

### 2b — Reading the survivors (0:35 – 0:55)

**[Screen: `results/survivors.md` after round 1]**

> "Three survivors: `MoneyUtils` line 26 misses the exact boundary value. `Validators` line
> 128 — an overload no test ever calls. `SimpleCache` line 142 — the expiry branch never
> runs. Each one is a precise test to write."

### 2c — Adding tests and re-running (0:55 – 1:25)

**[Screen: editor — writing boundary and expiry tests]**

> "One focused test per survivor — a boundary assertion, a direct call into the missing
> overload, a time-advance test for the cache — then run the script again."

```sh
sh run_round.sh
```

**[Terminal scrolling — PIT running a second time]**

> "The score climbs, `results/survivors.md` gets shorter. Repeat: read, write a test, run."

### 2d — Charting progress (1:25 – 2:00)

**[Screen: `chart.png` — line-coverage and mutation-score curves across all rounds]**

```sh
python chart.py
```

> "After a few rounds, `chart.py` plots this from `results/rounds.csv`. Line coverage —
> blue — barely moves, 95 to 99%. Mutation score — green — climbs from 82 to 95.8%. That
> gap is the whole point: coverage was already 'done'; mutation score wasn't."

---

## Segment 3 — Before and After (2:00 – 2:20)

**[Screen: split view or simple table — baseline numbers vs final round numbers]**

> "Here's the before and after:"

| Metric            | Baseline | After improvement rounds |
| ----------------- | -------- | ------------------------ |
| Line coverage     | 95%      | 99%                      |
| Mutation score    | 82%      | **95.8%**                |
| Surviving mutants | 48       | 11                       |

> "Coverage barely moved. Mutation score jumped from 82% to 95.8% — 37 of 48 previously
> surviving mutants are now caught. The loop repeats: one survivor, one test, one command.
> No guessing — just a number that proves your tests actually check behaviour."

**[End on `chart.png` full screen]**

---

*Narration ≈ 290 words / 1:56 at 150 wpm, plus real command time (~25s across the two
live `run_round.sh` runs) → total ≈ 2:20, well under the 3:00 limit. Segment 2 (the live
demo) alone runs 1:40, clearing the 90-second minimum.*
