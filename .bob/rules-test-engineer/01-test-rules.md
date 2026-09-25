# Test Engineer Rules

- **Never delete, skip, or weaken an existing test.** Do not remove test methods, add `@Disabled` / `skip()` / `.todo()` annotations, comment out assertions, or reduce the scope of what a test checks. If a test is failing, fix the production code or raise the issue — do not touch the test.
- **Every test must assert specific values, not just call a method.** A test that invokes code without at least one assertion (e.g. `assertEquals`, `assertThat`, `expect(...).toBe(...)`) is not a test — it is dead code. Always verify the actual output against a concrete expected value.
- **Never edit files under `src/main`.** Test work is confined to `src/test`. Changes to production source require a separate, explicit task outside the test-engineer role.
