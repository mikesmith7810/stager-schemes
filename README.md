# Stager Schemes

A property staging management application for creating, pricing, and presenting furniture/decor schemes for properties being prepared for sale or rental.

---

## Features

- **Schemes** — create and manage complete staging plans for properties
- **Items** — manage individual furniture and decor pieces with pricing and stock
- **Rooms** — group items by room type (bedroom, living room, kitchen, etc.)
- **Packs** — define reusable bundles of items that can be added to a scheme in one step
- **Scheme Summary** — view itemised costs, calculate totals, and print/export a scheme
- **Inventory Management** — add new items, update pricing, track stock levels

---

## Tech Stack

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring MVC**
- **Gradle** (Groovy DSL)

---

## Prerequisites

- Java 21+
- Gradle 9+ (or use the included `./gradlew` wrapper)

---

## Getting Started

```bash
# Clone the repository
git clone <repo-url>
cd stager-schemes

# Build
./gradlew build

# Run
./gradlew bootRun
```

The application will start on `http://localhost:8080` by default.

---

## Project Structure

```
src/
  main/
    java/com/ms/stagerschemes/
      controller/    # MVC controllers
      service/       # Business logic
      repository/    # Data access
      model/         # Domain entities
      dto/           # Request/response objects
      config/        # Spring configuration
    resources/
      application.properties
  test/
    java/com/ms/stagerschemes/
```

---

## Running Tests

```bash
./gradlew test
```

---

## Domain Glossary

| Term | Meaning |
|------|---------|
| **Item** | An individual piece of furniture or decor with a name, category, cost, and stock count |
| **Room** | A named room type used to organise items within a scheme |
| **Pack** | A pre-defined bundle of items that can be applied to a scheme as a unit |
| **Scheme** | A complete staging plan for a property, containing rooms, packs, and/or individual items |

---

## Contributing

- Branch off `main` using `feature/`, `fix/`, or `chore/` prefixes
- Write tests for new functionality
- Keep controllers thin — business logic belongs in services
