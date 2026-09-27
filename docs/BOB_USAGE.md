# IBM Bob Usage — Mutation-Testing Improvement Loop

This project was built almost entirely with IBM Bob in Agent mode. Bob contributed across four areas: a custom skill, a custom mode, subagents, and direct code and content authorship.

## The mutation-climb skill

The primary tool is the `mutation-climb` skill (`.bob/skills/mutation-climb/SKILL.md`), written by Bob and stored in the repository. The skill encodes the full improvement loop: run `run_round.sh`, parse `survivors.md`, identify the highest-value surviving mutants, generate targeted JUnit 5 tests, write them to disk, and decide whether to continue or stop based on the delta between rounds. Activating the skill with `/mutation-climb` in Agent mode replaces a manual, hours-long process with a single prompt.

## The custom mode

A custom Bob mode — Test Engineer — was created for this project. The mode focuses Bob on Java test quality: it understands PIT mutation operators, JUnit 5 patterns, boundary-value analysis, and equivalent-mutant identification. Bob authored the mode definition during task 11 (`developnerds_task11_test-engineer_mutation_climb.png`). The mode is stored in Bob's settings and was active for all test-writing rounds.

## Subagents

Bob used subagents to keep long improvement rounds from exhausting the context window. During multi-round mutation-climb sessions, Bob spawned an `explore` subagent to parse `survivors.md` and summarise the next batch of mutants to target, returning only a compact summary to the parent agent. This let the main context stay focused on writing tests rather than re-reading large XML and Markdown files.

## What Bob wrote

Bob authored the following project artifacts directly:

- All JUnit 5 tests added across the measured rounds, targeting boundary conditions, missing overload coverage, and expired-cache branches in `MoneyUtils`, `Validators`, `SimpleCache`, `StringUtils`, and `DateRangeUtils`.
- `run_round.sh` — the one-command automation script.
- `report.py` — XML parser that produces `results/survivors.md` and appends `results/rounds.csv`.
- `chart.py` — progress chart generator.
- `README.md` — full project documentation.
- `VIDEO_SCRIPT.md` — the three-minute narration script.
- `MEASURE.md` — the before/after measurement comparison.
- `equivalent-mutants.md` — classification of mutants that cannot be killed.
- All task-summary screenshots saved to `bob_sessions/`.

The human role was task direction: writing the task descriptions, reviewing Bob's output, accepting or rejecting changes, and deciding when a round was complete. Every file listed above was produced by Bob in response to a natural-language prompt, without the developer writing code manually.
