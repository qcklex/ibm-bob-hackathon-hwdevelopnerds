# Project Coding Rules (Non-Obvious Only)

- **`.bobignore` blocks file reads** for any path matching credential patterns (`*config.json`, `*secrets.*`, `*credentials.*`, `.env*`, names with `api_key`/`secret`/`token`/`password`). If a file you need to read is blocked, it must be renamed.
- **`.gitignore` uses glob patterns on filenames**, not just paths — `*token*`, `*secret*`, `*credentials*`, `*password*` will silently exclude any file whose name contains those strings. Run `git check-ignore -v <file>` before trusting a file will be tracked.
- **Add custom `.gitignore` entries only below** the `DO NOT REMOVE ABOVE PATTERNS` comment (line ~119 of `.gitignore`). Editing above that line violates repo policy.
- **`bob_sessions/` must NOT be gitignored** — it is required for hackathon project submission per the `.gitignore` comment.
- There is no build system, test runner, or linter in this template. Any tooling must be added by the team building on top of it.
- Always use environment variables for credentials — never hardcode. Pattern: `process.env.VAR` (JS), `os.getenv('VAR')` (Python), `System.getenv("VAR")` (Java).
