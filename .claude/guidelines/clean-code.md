# Clean Code Rules

Binding for every file in this repository. Derived from Robert C. Martin's *Clean Code*,
narrowed to the decisions that actually come up here.

## Naming

- Intention-revealing names. `remainingRetries`, not `n`. `checkedVersions`, not `list2`.
- A name that needs a comment is the wrong name. Rename instead of explaining.
- No type or scope encodings: no `strName`, no `m_field`, no `IService` interfaces.
- Pronounceable and searchable. No invented abbreviations; keep only ones the domain uses
  (`sha1`, `url`, `api`, `mc`).
- One word per concept across the whole codebase: pick `fetch` or `load` or `retrieve` and
  never mix them for the same operation.
- Classes are nouns, methods are verbs. Boolean methods read as predicates (`isStable`,
  `supportsVersion`).
- Avoid `Manager`, `Helper`, `Util`, `Data`, `Info`, `Processor` unless the domain really
  has no better word.
- Longer names are fine when they buy clarity; a name's length may grow with its scope.

## Functions

- Small. If a method does not fit on one screen, it is doing too much.
- One level of abstraction per function. High-level orchestration and byte fiddling never
  share a body.
- Do one thing. If you can extract a meaningful method out of it, it did more than one thing.
- Few arguments: zero is best, three is the ceiling. Group related parameters into a record.
- No flag arguments. Two behaviours mean two methods.
- Command–query separation: a method either changes state or answers a question, never both.
- No hidden side effects. The name states everything the method does.
- Prefer exceptions over error codes; extract try/catch bodies into their own methods.
- Return early; avoid deep nesting.

## Comments

**Comments are a last resort.** A comment is an admission that the code failed to say it.

- Allowed: minimal javadoc on public API types and methods that others call — one or two
  lines stating contract, not narration; a warning about a genuine non-obvious consequence
  (a rate limit, a Mojang quirk, a thread-safety requirement); a legal header if required.
- Forbidden: narration of the next line, redundant javadoc on trivial members, commented-out
  code, changelog comments, section banners, attribution comments, `TODO` left behind.
- Delete stale comments on sight. A wrong comment is worse than none.

## Classes and structure

- **Single Responsibility.** One reason to change per class. When the class description needs
  "and", split it.
- Small classes, measured in responsibilities rather than lines.
- **Many small files, deeply nested packages.** Package by feature first, then by layer inside
  the feature, nesting as deep as the domain warrants. A package with fifteen classes of mixed
  purpose is a smell.
- High cohesion: if a subset of fields is used by a subset of methods, that subset is a class.
- Depend on abstractions, not concretions; the UI must not know `HttpClient`, the network layer
  must not know about screens.
- Open–closed: add a new provider by adding a class, not by editing a switch.
- Interface segregation: narrow interfaces per consumer.
- Immutability by default — `record`, `final` fields, unmodifiable collections. Mutable state
  lives in one owner, never shared casually across threads.
- No cyclic dependencies between packages.

## General

- DRY: extract the third occurrence at the latest, the second usually.
- Boy Scout Rule: leave every file cleaner than you found it.
- Newspaper metaphor / stepdown rule: a file reads top-down from high level to detail.
- Fail fast and loudly; never swallow an exception silently. Errors carry context.
- No magic numbers or strings — named constants, and constants live next to their use.
- No dead code, no speculative generality, no unused parameters.
- Prefer standard library and existing project dependencies over new abstractions.
- Vertical formatting: related lines dense, concepts separated by blank lines, variables
  declared next to their use.

## Tests

- Tests are production code and follow every rule above.
- One assert-concept per test; the name states the scenario and the expectation.
- Given/when/then structure without writing the words as comments.
- Fast, independent, repeatable; no live network calls — stub the HTTP layer.
