# Project Architecture Rules (Non-Obvious Only)

- This is a **GitHub template repository** — there is no runtime architecture. The repo's sole purpose is to provide security scaffolding for teams to clone.
- The `.gitignore` is **forward-only by policy** — patterns above the `DO NOT REMOVE ABOVE PATTERNS` marker must not be removed or weakened. Only additive changes below that line are permitted.
- **`bob_sessions/` folder is a submission artifact** — it must remain untracked by git (not added to `.gitignore`) so hackathon judges can access exported session reports.
- **Filename namespace is heavily restricted** by `.gitignore` glob patterns. When designing any new file structure on top of this template, avoid names containing: `token`, `secret`, `password`, `credential`, `api_key`, `api-key`, `config` (standalone), `apikey`. These will be silently excluded from git tracking.
- The `.bobignore` and `.gitignore` cover overlapping but distinct sets of patterns — a file can be readable by Bob but gitignored, or unreadable by Bob but git-tracked. They are independent.
