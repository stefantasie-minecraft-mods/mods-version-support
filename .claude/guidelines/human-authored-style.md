# Human-Authored Style

Everything written in this repository — code, comments, commit messages, docs, issue text —
must read as if a single experienced developer wrote it by hand. The list below collects the
signals that readers and reviewers use to spot machine-written material. Avoid them.

## Prose tells

**Vocabulary that flags a text immediately**

- English: delve, leverage, foster, underscore, tapestry, realm, navigate (figurative), harness,
  pivotal, robust, intricate, nuanced, multifaceted, landscape (figurative), testament, beacon,
  cornerstone, embark, elevate, unlock, streamline, seamless, holistic, tailored, comprehensive,
  cutting-edge, game-changer, ensure (as filler), utilize (say "use").
- German: essenziell, vielfältig, nahtlos, maßgeschneidert, ganzheitlich, wegweisend,
  Kernstück, im Bereich, Wegbereiter.

**Filler phrases**

- "It is important to note that", "It is worth mentioning", "When it comes to",
  "In today's fast-paced world", "Plays a crucial role in", "A wide range of",
  "In the realm of", "Navigating the complexities of", "Let's dive in".
- German: "Es ist wichtig zu beachten, dass", "Insgesamt lässt sich festhalten",
  "In der heutigen schnelllebigen Welt".

**Rhetorical shapes**

- The negation formula: "not X, but Y", "it's not just X — it's Y", "X isn't about A; it's about B".
- "not only X, but also Y".
- Rule of three everywhere: three bullets, three adjectives, three-part summaries
  ("clear, concise, and compelling").
- Rhetorical question followed by its own answer.
- A closing paragraph that restates what was already said ("In summary…", "Overall…").
- Em dashes used for dramatic pauses several times per paragraph. Use commas, parentheses,
  or a full stop. An occasional em dash is fine; a habit is a fingerprint.

**Rhythm and structure**

- Uniform sentence length (everything between 15 and 25 words). Vary deliberately:
  short sentences next to long ones.
- Perfectly symmetric structure: intro of three sentences, three sections of equal size,
  bullet items of identical length.
- Bullet lists where flowing prose would be natural.
- Bold lead-ins on every single bullet.
- Emoji as section markers, decorative headers, "✅ / ❌" tables in prose docs.

## Code tells

- **Comment on every line or every block.** Explanatory narration of what the next line does
  ("// increment the counter"). This is the strongest signal in code.
- Docblocks on trivial getters, or javadoc that restates the signature in words.
- Section banner comments (`// ===== Helpers =====`), decorative separators.
- Comments addressed to a reader who is being taught ("Note that we…", "Here we…", "First, we…").
- Defensive scaffolding nobody asked for: try/catch around code that cannot throw,
  null checks on values that are never null, `TODO` markers for imaginary future work.
- Over-generic abstractions for a single implementation (`AbstractBaseManagerFactory`).
- Suffix soup: `*Manager`, `*Helper`, `*Util`, `*Processor`, `*Handler` when a concrete
  domain word exists.
- Variables named `result`, `data`, `temp`, `item`, `value` where the domain has a real name.
- Redundant local variables that are used exactly once immediately after assignment.
- Inconsistent style within a file: mixed quote styles, mixed brace styles, mixed naming.
- Every method exhaustively logged at info level.
- Deprecated or invented API calls that do not exist in the pinned dependency version —
  always verify against the actual jar or javadoc, never from memory.
- Test names that read like documentation sentences of 15 words, or tests that assert
  nothing meaningful ("assertNotNull(service)").
- Commit messages in the shape "feat: add comprehensive support for robust X handling".
  Write what changed, plainly: "Add Modrinth hash lookup for installed mods".

## Positive rules

- Write the way the surrounding file is already written; match its density and idiom.
- Let names carry the meaning, then delete the sentence that would have explained them.
- Uneven is human: some methods are three lines, some are twelve; some paragraphs are one
  sentence long.
- Say the specific thing. "Modrinth returns 429 above 300 requests per minute" beats
  "the API has rate limits that must be respected".
- When something is uncertain, say so once, in plain words, and move on.
