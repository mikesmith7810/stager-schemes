# Stager Schemes

A property staging management application for creating, pricing, and presenting furniture/decor schemes for properties being prepared for sale or rental.

---

## Features

- **Schemes** — create and manage complete staging plans for properties
- **Items** — manage individual furniture and decor pieces with pricing
- **Rooms** — define room templates with default items and packs
- **Packs** — reusable bundles of items that can be added to a scheme room in one step
- **Scheme Summary** — itemised cost breakdown with print support
- **Inventory Management** — CRUD for items, packs, and room templates

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 / Spring MVC |
| Build | Maven |
| Database | MySQL 8 |
| Security | Spring Security (single-user, session-based) |
| Frontend | React 18, Vite 5, React Router v6 |
| Container | Docker + Docker Compose |

---

## Running locally — one command

Requires [Docker](https://www.docker.com/) and Docker Compose.

```bash
git clone <repo-url>
cd stager-schemes
docker-compose up --build
```

The app will be available at **http://localhost:8080**.

Default credentials:

| Username | Password |
|---|---|
| `admin` | `changeme` |

To set a custom password, generate a bcrypt hash and pass it as an environment variable:

```bash
APP_PASSWORD='$2a$10$...' docker-compose up
```

---

## Running for development (hot reload)

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 20+
- A running MySQL instance (or use `docker-compose up db` to start just the database)

### 1. Start the database

```bash
docker-compose up db
```

### 2. Start the backend

```bash
cd backend
mvn spring-boot:run
```

The API will be available at **http://localhost:8080/api**.

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend dev server will be available at **http://localhost:5173**. API requests are proxied to the backend automatically.

---

## Building for production

```bash
# Build the frontend and copy it into the backend static resources
cd frontend && npm run build

# Package everything into a single deployable jar
cd ../backend && mvn package

# The jar is at backend/target/stager-schemes-backend-*.jar
java -jar backend/target/stager-schemes-backend-*.jar
```

---

## Running tests

```bash
cd backend && mvn test
```

---

## Project structure

```
stager-schemes/
  backend/                  Spring Boot Maven project
    src/main/java/com/ms/stagerschemes/
      api/                  API interfaces (request mappings)
      controller/           Controller implementations
      service/              Business logic
      component/            Reusable domain components
      repository/           Spring Data JPA repositories
      model/                JPA entities
      dto/                  Request/response records
      config/               Spring Security + Web config
    src/main/resources/
      static/               Built frontend (generated — do not edit)
      application.properties
    src/test/               Unit tests
  frontend/                 React + Vite project
    src/
      api/                  API client modules
      components/           Shared components (Nav, ProtectedRoute)
      pages/                Login, SchemeList, AddScheme, SchemeSummary, Inventory
      index.css             Design system (CSS custom properties)
  docker-compose.yml        Full stack: app + MySQL
  Dockerfile                Multi-stage build
  .circleci/config.yml      CI placeholder
```

---

## Domain glossary

| Term | Meaning |
|---|---|
| **Item** | An individual piece of furniture or decor with a name and price |
| **Pack** | A pre-defined bundle of items (e.g. "Bedroom Set") |
| **Room** | A room template with default items and packs |
| **Scheme** | A complete staging plan for a property, assembled from rooms, packs, and items |
| **SchemeRoom** | A snapshot copy of a room template added to a specific scheme — editable independently |
