# Submission fields — ready to paste

## Title (≤50 chars)
Mutation Score Climber

## Short description (≤255 chars)
IBM Bob's mutation-climb skill runs an automated PIT loop that turns surviving mutants into precise JUnit tests, lifting mutation score from 82% to 95.8% while line coverage only inches up.
[189 chars]

## Long description (≤500 words)
Line coverage is the metric most teams trust, and it lies. A test can execute a line without checking that the code did the right thing. On a small Java utility library, line coverage was already at 95% — green across the board — but PIT mutation testing showed the real picture: only 81.8% of deliberately-broken code variants (mutants) were caught. Roughly one in five logic changes could slip into production with every test still passing.

Mutation Score Climber turns that gap into a repeatable, one-command loop. `sh run_round.sh` compiles the project, runs PIT, and writes two files: `survivors.md` (every mutant the tests missed, with file, line and mutation type) and a new row in `rounds.csv`. IBM Bob then reads `survivors.md` through a custom `mutation-climb` skill and a Test Engineer custom mode scoped to `src/test/java/`, writes the exact JUnit assertion each survivor needs, and the loop repeats.

Any developer with a JUnit suite and a coverage number they don't fully trust can run this on their own project. The loop is language-agnostic in concept — swap PIT for Stryker or mutmut and the same skill drives it.

Results, all from a clean build so they reproduce from a fresh clone: mutation score rose from 81.8% to 95.8% (+14.0 pp) across 4 measured rounds; line coverage from 95% to 99%; survivors from 48 to 11, seven of which are proven equivalent mutants no test can ever kill. 37 of 48 baseline survivors are now caught.
[244 words]

## IBM Bob usage statement (≤500 words)
This project was built in IBM Bob's Agent mode from the first commit. Bob authored the sample Java library, the JUnit test suite across every round, `run_round.sh`, `report.py` (the PIT-XML parser), `chart.py`, and all project documentation.

The core of the submission is a custom `mutation-climb` skill (`.bob/skills/mutation-climb/SKILL.md`) that encodes the full improvement loop: run PIT, parse survivors, rank them by kill probability, write targeted JUnit 5 tests for the highest-value mutants, and decide whether to continue or stop based on the score delta between rounds. A paired Test Engineer custom mode restricts Bob's tool access to `src/test/java/` so it can strengthen tests without touching production code.

During the highest-volume round, Bob split survivor analysis across parallel subagents — one per class under test — so five classes could be triaged at once instead of serially, then merged the results into one coherent commit.

The developer's role was direction and review: writing task prompts, approving diffs, and deciding when a round was done. Every test, script and document Bob touched is in the repository's git history and screenshotted in `bob_sessions/`.
[179 words]

## Technology tags
Java, JUnit, Mutation Testing, PIT, Testing Automation, Developer Tools, IBM Bob

## Demo platform / link
GitHub repository (public): https://github.com/qcklex/ibm-bob-hackathon-hwdevelopnerds — clone and run `sh run_round.sh` from the repo root (Java 17, Maven 3.8+).

## GitHub link
https://github.com/qcklex/ibm-bob-hackathon-hwdevelopnerds

## Cover image
submission/cover.png

## Slides
submission/slides.pdf (also: submission/slides-editable.pptx)

## Video
submission/<your-video-file>.mp4  — fill in once recorded
