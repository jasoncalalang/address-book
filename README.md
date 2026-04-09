# Address Book

A minimal Spring Boot CRUD application used as the sample build target for CI/CD learning projects. Small on purpose — the lesson is the pipeline, not the business logic.

## Stack

- Java 21
- Spring Boot 4.x
- Spring Data JPA + H2 (in-memory)
- Gradle (wrapper included)
- JUnit 5 + MockMvc
- Checkstyle + SpotBugs

## Used by

- [jenkins-reference](https://github.com/jasoncalalang/jenkins-reference) — learn Jenkins with docker-compose
- [jenkins-kube-reference](https://github.com/jasoncalalang/jenkins-kube-reference) — learn Jenkins on Kubernetes with Kaniko builds

Both teaching projects point their Jenkins seed job at this repository, clone it, and run the included [Jenkinsfile](Jenkinsfile).

## Build and run locally

```bash
./gradlew build           # compile + package
./gradlew test            # run unit tests
./gradlew bootRun         # start on http://localhost:8080
```

No local Java or Gradle install required — the Gradle wrapper fetches everything.

## Endpoints

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/contacts` | List all contacts |
| GET | `/api/contacts/{id}` | Get one contact |
| POST | `/api/contacts` | Create a contact |
| PUT | `/api/contacts/{id}` | Update a contact |
| DELETE | `/api/contacts/{id}` | Delete a contact |
| GET | `/health` | Health check (used by the pipeline) |

Three sample contacts are seeded at startup from [`data.sql`](src/main/resources/data.sql).

## Build the container image

```bash
./gradlew bootJar
docker build -t address-book:local .
docker run --rm -p 8080:8080 address-book:local
```

## Pipeline

The [`Jenkinsfile`](Jenkinsfile) defines a full CI/CD pipeline:

```
Checkout → Build → Unit Test → Code Quality → Build Docker Image
        → Trivy Scan → Push to Registry → Deploy → Health Check
```

It's designed to run inside the Jenkins environments above, but it's just Groovy — nothing stops you from pointing your own Jenkins at it.

## License

MIT — see [LICENSE](LICENSE).
