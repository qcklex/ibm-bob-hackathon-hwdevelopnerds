#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
RESULTS_DIR="$REPO_ROOT/results"
mkdir -p "$RESULTS_DIR"
PIT_OUTPUT="$RESULTS_DIR/pit-output.txt"
MUTATIONS_XML="$REPO_ROOT/toolkit/mutations.xml"

echo "[run_round.sh] Running PIT mutation coverage..."
mvn -f "$REPO_ROOT/toolkit/pom.xml" clean test-compile org.pitest:pitest-maven:mutationCoverage \
    | tee "$PIT_OUTPUT"

echo "[run_round.sh] Copying mutations.xml out of target/..."
cp "$REPO_ROOT/toolkit/target/pit-reports/mutations.xml" "$MUTATIONS_XML"

echo "[run_round.sh] Running report.py..."
python3 "$REPO_ROOT/.bob/skills/mutation-climb/scripts/report.py" \
    --xml        "$MUTATIONS_XML" \
    --output     "$RESULTS_DIR/survivors.md" \
    --csv        "$RESULTS_DIR/rounds.csv" \
    --pit-output "$PIT_OUTPUT"

echo "[run_round.sh] Done."
