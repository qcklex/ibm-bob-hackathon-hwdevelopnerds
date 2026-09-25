# Project Documentation Rules (Non-Obvious Only)

- This repo contains **no application code** — it is a pure GitHub template scaffold. All files are security/config infrastructure only.
- The authoritative security rules live in [`SECURITY.MD`](../../SECURITY.MD) (note: uppercase `.MD` extension, not `.md`).
- `.env.example` **cannot be read by Bob** — it matches `.env.*` in `.bobignore`. Reference it via `git show HEAD:.env.example` or inspect it outside Bob.
- `.bobignore` pattern matching is filename-based glob, not path-relative. A file two directories deep named `config.json` is still blocked.
- There is no `docs/` folder, wiki, or additional documentation beyond `README.md` and `SECURITY.MD`.
