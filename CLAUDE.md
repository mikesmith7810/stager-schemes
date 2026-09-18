# CLAUDE.md — Project Context for Claude Code

This file provides context, conventions, and guardrails for Claude Code when working in this repository.

---

## Project Overview

**Stager Schemes** is a property staging management application. Property stagers use it to plan, price, and present furniture/decor schemes for properties being prepared for sale or rental.

### Core Domain Concepts

| Concept | Description |
|---------|-------------|
| **Item** | An individual furniture or decor piece with a name, category, cost, and stock quantity |
| **Room** | A named room type (e.g. Master Bedroom, Living Room) that groups items |
| **Pack** | A pre-defined bundle of items that can be added to a scheme as a unit |
| **Scheme** | A complete staging plan for a property — contains rooms, packs, and/or individual items with a total cost |

### Screens / Features

- **Schemes List** — browse and manage all current schemes
- **Add Scheme** — create a new scheme, assign items/rooms/packs
- **Scheme Summary** — view itemised costs, totals, and print/export the scheme
- **Inventory / Item Management** — add new items, manage stock and pricing

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Build tool | Gradle (Groovy DSL) |
| Web | Spring MVC (`spring-boot-starter-webmvc`) |
| Database | TBD — add to this file when chosen |
| Frontend | TBD — add to this file when chosen |

---

## Project Structure (target)

```
src/
  main/
    java/com/ms/stagerschemes/
      controller/      # REST or MVC controllers
      service/         # Business logic
      repository/      # Data access (Spring Data)
      model/           # JPA entities / domain objects
      dto/             # Request/response DTOs
      config/          # Spring configuration classes
    resources/
      application.properties
      templates/       # Thymeleaf templates (if used)
      static/          # CSS, JS, images (if used)
  test/
    java/com/ms/stagerschemes/
      controller/
      service/
```

---

## Build & Run Commands

```bash
# Build
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# Check for dependency updates
./gradlew dependencyUpdates
```

---

## Coding Conventions

### Formatting
- All code **must** be formatted with the [Google Java Formatter](https://github.com/google/google-java-format)
- Import statements follow Google style: no wildcard imports, static imports first then third-party then JDK, each group separated by a blank line
- The IntelliJ `google-java-format` plugin is already configured in `.idea/` — use it before committing

### Naming
- `PascalCase` for classes, `camelCase` for methods and fields, `SCREAMING_SNAKE_CASE` for constants
- Variable and parameter names must be **descriptive** — no single-letter names, no abbreviations (e.g. `schemeRepository` not `repo`, `itemTotalCost` not `cost`)
- Boolean fields/methods should read as a question: `isAvailable()`, `hasItems()`

### Self-Documenting Code
- **No comments** — code should be clear enough to not need them
- If a method needs a comment to explain what it does, rename it or break it up instead
- Javadoc is also not required; expressive naming and small focused methods are preferred

### Class Structure
Each class should be organised in this order:
1. `static final` String and primitive constants
2. Other `static final` fields
3. Instance fields
4. Constructors
5. Public methods
6. Private methods

### Architecture Layers
The layering is strict — each layer only talks to the one directly below it:

```
Controller  →  Service  →  Business Component  →  Repository
```

- **Controllers** implement a dedicated API interface (e.g. `SchemeApi`) and only delegate to a service — no logic
- **API interfaces** define the contract (method signatures, request mappings); controllers implement them
- **Services** orchestrate business flow; they call business components or repositories, never other services
- **Business components** contain reusable domain logic (e.g. cost calculation, pack expansion); they may call repositories
- **Repositories** handle all data access via Spring Data; no business logic

### Package structure
```
com.ms.stagerschemes.
  api/          # API interfaces implemented by controllers
  controller/   # Controller implementations
  service/      # Service classes
  component/    # Business components
  repository/   # Spring Data repositories
  model/        # JPA entities
  dto/          # Request/response DTOs
  config/       # Spring configuration
```

### Other Rules
- **Entities**: annotate with `@Entity`; use `Long` primary keys named `id`; no business logic in entities
- **Services and components**: constructor injection only — no `@Autowired` on fields
- **DTOs**: separate request and response DTOs; always defined as Java `record` types; never expose entities directly through the API
- **Tests**: unit tests are mandatory for all services and business components; mock dependencies with Mockito; integration test controllers with `@WebMvcTest`; every public method on a service or component must have at least one test
- **No Lombok** unless explicitly added as a dependency

---

## Guardrails

These rules apply to all Claude Code actions in this repository:

### Do NOT
- **Commit or push under any circumstances** without explicit instruction from the user — not even a WIP commit
- Run any command that modifies a database directly (e.g. raw SQL `DROP`, `DELETE`, `UPDATE` without a WHERE clause)
- Modify `application.properties` to point at a production database or external service without asking first
- Delete source files — ask before removing any `.java` file
- Add new Gradle dependencies without flagging them for review
- Generate or hardcode secrets, API keys, or credentials anywhere in source code
- Add comments to code — write self-documenting code instead

### Do
- Ask before making changes to existing working code if the scope is unclear
- Create feature branches for any significant work (`feature/`, `fix/`, `chore/` prefixes)
- Write or update tests when adding new functionality
- Keep controllers thin and services testable
- Add database migrations (when a migration tool is chosen) rather than relying on `ddl-auto=create`

---

## Architecture Decisions (log changes here)

| Date | Decision | Rationale |
|------|----------|-----------|
| — | Spring MVC chosen over WebFlux | Simpler for a CRUD-heavy admin tool; team familiarity |
| — | Database TBD | Decide between PostgreSQL and H2+Flyway before first persistence layer |

---

## Notes for Claude

- The domain is **property staging** (furnishing properties for sale/rental) — not software deployment staging
- "Scheme" in this codebase always means a **staging plan for a property**, not a database schema
- Costs in this domain are typically per-week or per-month rental fees for furniture — clarify with the user which model applies
- When adding new screens, check the Screens section above to stay consistent with the planned UX flow
