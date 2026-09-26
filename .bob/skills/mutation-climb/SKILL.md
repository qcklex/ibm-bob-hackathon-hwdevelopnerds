---
name: mutation-climb
description: >-
  Use when the user wants to run a mutation-testing improvement loop on a Java
  Maven project — runs PIT, parses survivors, writes targeted tests, and
  iterates until the mutation score stops rising or a target is reached.
---

# Mutation-Climb Skill

Drive a systematic loop that raises the PIT mutation score for a Java/Maven
project. Each round: run PIT, parse the XML report, identify surviving mutants,
write targeted JUnit 5 tests to kill them, then repeat.

---

## Prerequisites

- Java 17+ and Maven available on PATH (`mvn --version` to verify).
- `python3` available on PATH.
- `toolkit/pom.xml` contains the `pitest-maven` plugin (version 1.15.8+) with
  the `pitest-junit5-plugin` dependency, `<outputFormats><outputFormat>XML</outputFormat></outputFormats>`,
  and `<timestampedReports>false</timestampedReports>`. Refer to
  [`toolkit/pom.xml`](toolkit/pom.xml) as the reference configuration.
- Supporting script: [`.bob/skills/mutation-climb/scripts/report.py`](.bob/skills/mutation-climb/scripts/report.py)

---

## Step 1 — Establish the Baseline

1. Read [`toolkit/BASELINE.md`](toolkit/BASELINE.md) and [`rounds.csv`](rounds.csv) to understand
   the current mutation score and how many rounds have run.
2. Read [`survivors.md`](survivors.md) to see which mutants survived the last round.
3. If `rounds.csv` is empty or missing, run PIT once now (Step 2) to establish the baseline before
   proceeding.

---

## Step 2 — Run PIT

Execute PIT inside the `toolkit/` directory and capture stdout:

```bash
cd toolkit && mvn -q test-compile org.pitest:pitest-maven:mutationCoverage \
  | tee ../pit-output.txt ; cd ..
```

Expected report location: `toolkit/target/pit-reports/mutations.xml`.

If Maven exits non-zero, stop and report the error to the user — do not continue the loop.

---

## Step 3 — Parse the Report

Run the bundled parser to update `survivors.md` and `rounds.csv`:

```bash
python3 .bob/skills/mutation-climb/scripts/report.py \
  --xml        toolkit/target/pit-reports/mutations.xml \
  --output     survivors.md \
  --csv        rounds.csv \
  --pit-output pit-output.txt
```

Read the updated [`survivors.md`](survivors.md) to get the current survivor list, and read
[`rounds.csv`](rounds.csv) to record the new mutation score for this round.

---

## Step 4 — Evaluate Progress

- **Reached target?** If the mutation score is ≥ the user's stated target (default 90 % when none
  stated), proceed to Step 7.
- **No progress?** If the mutation score did not improve by at least 1 percentage point from the
  previous round in `rounds.csv`, proceed to Step 7 with a "plateau" explanation.
- **Continue?** Proceed to Step 5.

---

## Step 5 — Select Survivors to Kill

From `survivors.md`, group surviving mutants by source file. Prioritise:

1. **NO_COVERAGE** mutants first — they signal completely untested code paths.
2. **SURVIVED** mutants on methods with the most survivors, to maximise test density per edit.

Pick up to **10 survivors** per round to keep each commit focused. For each selected survivor, note:
- Source file, line number, class, method, and mutation description.
- The corresponding test file under `src/test/java/…`.

---

## Step 6 — Write Targeted Tests

Open the relevant test file(s) under `toolkit/src/test/java/…` and add new `@Test` methods that
directly exercise the mutated line with a concrete expected value. Rules:

- **One test per surviving mutant** (or one test that kills several mutants on the same line).
- Every test must contain at least one `assertEquals`, `assertThrows`, or equivalent JUnit 5 assertion.
- Tests must be deterministic and self-contained (no file I/O, no network, no `Thread.sleep`).
- Never edit files under `src/main/` — only `src/test/`.
- Never disable, skip, or weaken an existing test.
- Use the exact method and class names from `survivors.md` to target the mutation precisely.

After writing, verify the tests compile:

```bash
cd toolkit && mvn -q test-compile ; cd ..
```

If compilation fails, fix the test before continuing.

---

## Step 7 — Loop or Stop

- If the score improved and has not reached the target, return to Step 2 for the next round.
- If the target is reached, report success with a summary (rounds completed, score gained,
  mutants killed this session).
- If a plateau is detected (score unchanged for one full round), report the plateau, list any
  remaining difficult survivors (e.g. boundary mutants in `ConditionalsBoundary`), and stop.

---

## Reporting Format

After each round, output a one-line status line:

```
Round N | score: XX.X% (+Y.Y pp) | survivors: ZZ | target: TT%
```

At the end of the session, summarise the full `rounds.csv` trajectory.
