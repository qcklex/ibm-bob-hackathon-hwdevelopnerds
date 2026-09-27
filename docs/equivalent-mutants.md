# Equivalent Mutants

_Checked against the clean-build PIT run of 2026-09-26 23:42 BST · 264 mutants · score 95.8%. The same 7 survivors are equivalent._

The following 7 surviving mutants are **equivalent** — no test can ever distinguish them
from the original because the observable behaviour is identical for every possible input.

---

## T8 — `MoneyUtils.java:57` · `split` · changed conditional boundary

```java
// original
boolean negative = totalUnits < 0;
// mutant
boolean negative = totalUnits <= 0;
```

When `totalUnits == 0` every `share` is 0, so `negative ? -share : share` evaluates to 0
regardless of the flag. The sign of zero is irrelevant to the result.

---

## T10 — `MoneyUtils.java:136` · `clamp` · changed conditional boundary

```java
// original
if (amount.compareTo(min) < 0)  return min;
// mutant
if (amount.compareTo(min) <= 0) return min;
```

The only new case is `amount == min`. The original falls through and returns `amount`;
the mutant returns `min`. Since `amount == min` they are the same value.

---

## T11 — `MoneyUtils.java:137` · `clamp` · changed conditional boundary

```java
// original
if (amount.compareTo(max) > 0)  return max;
// mutant
if (amount.compareTo(max) >= 0) return max;
```

The only new case is `amount == max`. The original returns `amount`; the mutant returns
`max`. Since `amount == max` they are the same value.

---

## T12 — `StringUtils.java:78` · `repeat` · changed conditional boundary

```java
// original
if (times <= 0) return "";
// mutant
if (times < 0)  return "";
```

When `times == 0` the mutant falls through to `s.repeat(0)`, which returns `""` by the
Java specification — identical to the early return.

---

## T13 — `StringUtils.java:88` · `leftPad` · changed conditional boundary

```java
// original
if (s.length() >= totalWidth) return s;
// mutant
if (s.length() >  totalWidth) return s;
```

When `s.length() == totalWidth` the mutant computes
`padChar.repeat(totalWidth - s.length()) + s` = `"" + s` = `s` — the same value the
early return would have produced.

---

## T14 — `StringUtils.java:97` · `rightPad` · changed conditional boundary

```java
// original
if (s.length() >= totalWidth) return s;
// mutant
if (s.length() >  totalWidth) return s;
```

Identical reasoning to T13: when lengths are equal the padding expression appends
`padChar.repeat(0)` = `""`, leaving the string unchanged.

---

## T17 — `Validators.java:86` · `isCreditCard` · replaced integer addition with subtraction

```java
// original
sum += n;
// mutant
sum -= n;
```

Replacing every addend with its negation negates the final `sum`. The only thing that
matters is `sum % 10 == 0`. Because `k % 10 == 0 ⟺ (−k) % 10 == 0` in Java integer
arithmetic, the boolean return value is identical for every possible input string.
