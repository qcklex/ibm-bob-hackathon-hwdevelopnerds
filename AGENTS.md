# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Overview

This is a GitHub template repository for IBM Hackathon projects. It has **no application code, build system, or tests** — it is a starter scaffold that teams clone and build on top of.

## Critical Security Constraints

### `.bobignore` — affects what Bob can read
[`.bobignore`](ibm-hackathon-template/.bobignore) blocks Bob from reading files matching credential patterns. This includes:
- Any file matching `*config.json`, `*config.yaml`, `*secrets.*`, `*credentials.*`
- All `.env` and `.env.*` files
- Any filename containing `api_key`, `secret`, `password`, `token`, `credential`, etc.

**Impact:** If a project file has a "safe" name but lands under a blocked pattern, Bob will refuse to read it. Rename files to avoid blocked patterns when agent access is needed.

### `.gitignore` — intentionally aggressive
The `.gitignore` blocks entire filename patterns (e.g., `*token*`, `*secret*`, `*credentials*`), not just specific files. A file named `access_token_flow.md` **would be ignored by git**. Always check with `git check-ignore -v <file>` before assuming a file will be committed.

**Note from `.gitignore` comments:** The `bob_sessions/` folder is **required for project submission** — do not add it to `.gitignore`.

### Never modify security files
Per repo policy, do **not** remove or modify patterns in `.gitignore` or `.bobignore`. Add project-specific patterns only below the designated section at the bottom of `.gitignore`.

## Environment Variables

All credentials must go in `.env` (copied from `.env.example`). The `.env` file is gitignored. Reference credentials only via environment variables — never hardcode them. Use `process.env.VAR` (Node.js), `os.getenv('VAR')` (Python), or `System.getenv("VAR")` (Java).

## Adding Application Code

When building on this template:
- Add project-specific `.gitignore` entries **below** the `DO NOT REMOVE ABOVE PATTERNS` comment at the bottom of `.gitignore`.
- Do not create files whose names contain `secret`, `token`, `password`, `credential`, `api_key`, or `config` — they will be gitignored automatically.
