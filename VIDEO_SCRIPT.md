# Video Script — Mutation-Testing Improvement Loop (3 min)

---

## Segment 1 — The Problem (0:00 – 0:25)

**[Screen: `toolkit/BASELINE.md` open in editor]**

> "This is a Java utility library with five classes and a full JUnit 5 test suite.
> Run the coverage report and you get **95% line coverage** — green across the board.
> Most teams ship at this point.
>
> But watch what happens when we run PIT mutation testing instead.
> PIT makes tiny logic changes — flips a conditional, removes a return value, changes a boundary —
> and checks whether any test *fails* as a result.
>
> **82% mutation score.** That means roughly one in five logic mutations slips through
> completely undetected.
> The lines executed, but nothing in the test suite actually checked the outcome.
> Line coverage cannot see this gap. Mutation score can."

**[Pause on the two numbers side by side: 95% line coverage · 82% mutation score]**

---

## Segment 2 — Running the Loop (0:25 – 2:25)

### 2a — Prerequisites and first command (0:25 – 0:45)

**[Screen: terminal at repo root]**

> "The setup is one table of three tools: Java 17, Maven 3.8, Python 3.9 with matplotlib.
> Once those are in place, every round is a single command:"

```sh
sh run_round.sh
```

> "The script compiles the project, runs PIT, copies the raw XML, then calls `report.py`
> to produce two files: `survivors.md` — the list of mutants still alive —
> and a new row in `rounds.csv`."

**[Show terminal scrolling through Maven/PIT output — let it run, do not skip]**

### 2b — Reading the survivors (0:45 – 1:10)

**[Screen: `survivors.md` after round 1]**

> "Here is what survives. Three representative examples:
>
> — `MoneyUtils` line 26: a conditional-boundary mutation.
>   The test covers the line but never hits the exact edge value.
>
> — `Validators` line 128: negated conditional, marked `NO_COVERAGE`.
>   This overload is never called by any test at all.
>
> — `SimpleCache` line 142: `isExpired` forced to return `false`.
>   The happy-path test never exercises the expiry branch.
>
> Each surviving mutant is a concrete test to write."

### 2c — Adding tests and re-running (1:10 – 1:50)

**[Screen: editor — writing boundary and expiry tests]**

> "We add focused tests for each survivor:
> an exact boundary assertion for `MoneyUtils`,
> a direct call into the missing `Validators` overload,
> and a time-advance test for `SimpleCache`.
> Then we run the script again."

```sh
sh run_round.sh
```

**[Terminal scrolling — PIT running a second time]**

> "The mutation score climbs. `survivors.md` is shorter.
> We repeat: read the new survivors, write tests, run the script."

### 2d — Charting progress (1:50 – 2:25)

**[Screen: `chart.png` — line-coverage and mutation-score curves across all rounds]**

```sh
python chart.py
```

> "After several rounds, `chart.py` renders this chart from `rounds.csv`.
> The blue line is line coverage — it reaches 95% early and then flatlines.
> The green line is mutation score — it keeps climbing as we kill more survivors.
> That divergence is the whole story: line coverage was already 'done' while
> mutation score had significant room to improve."

---

## Segment 3 — Before and After (2:25 – 3:00)

**[Screen: split view or simple table — BASELINE numbers vs final round numbers]**

> "Here is the concrete before-and-after:"

| Metric | Baseline | After improvement rounds |
|--------|----------|--------------------------|
| Line coverage | 95% | 95% |
| Mutation score | 82% | **92%+** |
| Surviving mutants | 52 | < 23 |

> "Line coverage did not move — we did not add dead lines, we added *assertions*.
> Mutation score jumped from 82% to over 92%.
> That means more than half the previously-surviving bugs are now caught.
>
> The loop is repeatable. Every time PIT finds a survivor, you have a precise target.
> You write one test, run one command, and the number moves.
> No guessing, no coverage theatre — just a concrete metric that tracks whether
> your tests actually verify behaviour."

**[End on `chart.png` full screen]**

---

*Total runtime: ~3 minutes. Adjust pacing in Segment 2c/2d if live PIT runs are used.*
