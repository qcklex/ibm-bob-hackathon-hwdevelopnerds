#!/usr/bin/env python3
"""
report.py — Parse PIT mutations.xml and update survivors.md / rounds.csv.

Usage:
    python report.py \
        --xml   toolkit/mutations.xml \
        --output results/survivors.md \
        --csv   results/rounds.csv \
        --pit-output results/pit-output.txt   # optional; used to read line coverage

If --pit-output is absent, line_coverage is left as "" in rounds.csv.
"""

import argparse
import csv
import os
import re
import sys
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from zoneinfo import ZoneInfo


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def parse_args():
    p = argparse.ArgumentParser(description="PIT report parser")
    p.add_argument("--xml",        default="toolkit/mutations.xml",  help="Path to mutations.xml")
    p.add_argument("--output",     default="results/survivors.md",   help="Output survivors markdown")
    p.add_argument("--csv",        default="results/rounds.csv",     help="Rounds CSV to append to")
    p.add_argument("--pit-output", default="results/pit-output.txt", help="Raw PIT stdout for line coverage")
    return p.parse_args()


# ---------------------------------------------------------------------------
# Parse mutations.xml
# ---------------------------------------------------------------------------

def parse_xml(path):
    tree = ET.parse(path)
    root = tree.getroot()

    survivors = []
    total = 0
    killed = 0

    for m in root.iter("mutation"):
        total += 1
        status = m.get("status", "")
        if m.get("detected", "false").lower() == "true":
            killed += 1

        if status in ("SURVIVED", "NO_COVERAGE"):
            survivors.append({
                "status":      status,
                "file":        (m.findtext("sourceFile") or "").strip(),
                "line":        (m.findtext("lineNumber") or "").strip(),
                "class":       (m.findtext("mutatedClass") or "").strip(),
                "method":      (m.findtext("mutatedMethod") or "").strip(),
                "description": (m.findtext("description") or "").strip(),
            })

    mutation_score = round(100.0 * killed / total, 1) if total else 0.0
    return survivors, total, mutation_score


# ---------------------------------------------------------------------------
# Read line coverage from pit-output.txt
# ---------------------------------------------------------------------------

def read_line_coverage(path):
    """
    Looks for a line like:
        >> Line Coverage (for mutated classes only): 278/282 (99%)
    Returns the percentage string, e.g. "95", or "" if not found.
    """
    if not os.path.isfile(path):
        return ""
    pattern = re.compile(r"Line Coverage[^:]*:\s*\d+/\d+\s*\((\d+(?:\.\d+)?)%\)", re.IGNORECASE)
    with open(path, encoding="utf-8") as fh:
        for line in fh:
            m = pattern.search(line)
            if m:
                return m.group(1)
    return ""


# ---------------------------------------------------------------------------
# Write survivors.md
# ---------------------------------------------------------------------------

def write_survivors(path, survivors, total, mutation_score, line_coverage):
    ts = datetime.now(ZoneInfo("Europe/London")).strftime("%Y-%m-%d %H:%M %Z")
    lines = [
        "# Survivors",
        "",
        f"_Generated: {ts}_  ",
        f"_Mutants generated: {total} · Mutation score: {mutation_score}% · "
        f"Line coverage: {line_coverage + '%' if line_coverage else 'n/a'}_",
        "",
        f"**{len(survivors)} mutant(s) not killed** (SURVIVED or NO_COVERAGE).",
        "",
        "| File | Line | Class.Method | Status | Description |",
        "|------|------|--------------|--------|-------------|",
    ]

    for s in sorted(survivors, key=lambda x: (x["file"], int(x["line"]) if x["line"].isdigit() else 0)):
        class_method = f"{s['class']}.{s['method']}"
        lines.append(
            f"| {s['file']} | {s['line']} | `{class_method}` | {s['status']} | {s['description']} |"
        )

    lines.append("")
    with open(path, "w", encoding="utf-8") as fh:
        fh.write("\n".join(lines))

    print(f"[report.py] Wrote {len(survivors)} survivors to {path}")


# ---------------------------------------------------------------------------
# Append to rounds.csv
# ---------------------------------------------------------------------------

def append_csv(path, line_coverage, mutation_score):
    # Determine next round number
    round_num = 1
    if os.path.isfile(path):
        with open(path, newline="", encoding="utf-8") as fh:
            reader = csv.DictReader(fh)
            rows = list(reader)
            if rows:
                try:
                    round_num = max(int(r.get("round", 0)) for r in rows) + 1
                except ValueError:
                    round_num = len(rows) + 1

    row = {
        "round":          round_num,
        "line_coverage":  line_coverage,
        "mutation_score": mutation_score,
    }

    file_exists = os.path.isfile(path)
    with open(path, "a", newline="", encoding="utf-8") as fh:
        writer = csv.DictWriter(fh, fieldnames=["round", "line_coverage", "mutation_score"])
        if not file_exists:
            writer.writeheader()
        writer.writerow(row)

    print(f"[report.py] Appended round {round_num} to {path}  "
          f"(line_coverage={line_coverage}, mutation_score={mutation_score})")


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    args = parse_args()

    if not os.path.isfile(args.xml):
        print(f"[report.py] ERROR: XML not found: {args.xml}", file=sys.stderr)
        sys.exit(1)

    survivors, total, mutation_score = parse_xml(args.xml)
    line_coverage = read_line_coverage(args.pit_output)

    write_survivors(args.output, survivors, total, mutation_score, line_coverage)
    append_csv(args.csv, line_coverage, mutation_score)


if __name__ == "__main__":
    main()
