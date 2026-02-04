# Smart E-Commerce System

Smart E-Commerce System — Spring Boot backend for a demo e-commerce platform.

This README documents how to set up, run, test, and contribute to the project. It also explains architectural choices (DAO → Service → Controller), the in-project merge-sort utility, recommended repository migration to Spring Data JPA, caching guidance, and where to store performance results.

Date: 2026-02-03

---

Contents
- Project overview
- Requirements
- Quick start (local development)
- Configuration (databases, profiles)
- Running the application
- API surface (REST + GraphQL)
- Pagination, sorting, and merge-sort usage
- Caching (what to add and where)
- Transactions and testing rollback behavior
- Performance measurement and where to store results
- Contributing, coding standards, and testing
- Troubleshooting & common tasks

---

Project overview
----------------
This Spring Boot application exposes REST and GraphQL endpoints for users, products, categories, carts, orders and reviews. The codebase currently contains:
- JPA-annotated domain models under `src/main/java/com/amalitech/demo/models`
- Manual JDBC DAO implementations under `src/main/java/com/amalitech/demo/dao/implementations`
- Service layer under `src/main/java/com/amalitech/demo/services`
- REST controllers under `src/main/java/com/amalitech/demo/restcontroller`
- GraphQL controllers under `src/main/java/com/amalitech/demo/graphqlcontroller`
- Utility `Sorter` + `MergeSorter` under `src/main/java/com/amalitech/demo/utils`

Per project requirements, the next development stage is to migrate DAOs to Spring Data JPA repositories and enable features such as caching and advanced query optimizations.

Requirements
------------
- Java 21 (tested with JDK 21)
- Maven wrapper included (`mvnw` / `mvnw.cmd`)
- MySQL or PostgreSQL for production-like runs (examples below use PostgreSQL)
- (Optional) `wrk` or `ab` for quick HTTP benchmarking

Quick start (local development)
-------------------------------
1. Clone repo (already in this workspace)
2. Configure database (see Configuration section)
3. Build and run the app:

```bash
# from project root
./mvnw clean package -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

Or run from your IDE using `SmartEcomSystemApplication` main class.

Configuration
-------------
Profiles: `application.properties`, `application-dev.properties`, `application-test.properties` exist under `src/main/resources`.

Typical PostgreSQL example (application-dev.properties):

```properties
spring.profiles.active=dev
spring.datasource.url=jdbc:postgresql://localhost:5432/smart_ecom_db
spring.datasource.username=useername
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

If you prefer MySQL, adjust the JDBC URL and driver accordingly.

Running the application
-----------------------
- Default REST base: `http://localhost:8080/api/v1/`
- Swagger/OpenAPI doc: the project includes Springdoc OpenAPI config — visit `/swagger-ui.html` or `/swagger-ui/index.html` depending on Springdoc version.
- GraphQL: `/graphql` and GraphiQL/Playground if enabled in config.

API surface (high-level)
------------------------
- Products: `/api/v1/products` (list/get/create/update/delete)
- Users: `/api/v1/users` (auth, CRUD)
- Orders: `/api/v1/orders` (order creation, listing, status updates)
- Reviews: `/api/v1/reviews`

Refer to the controller classes in `src/main/java/com/amalitech/demo/restcontroller` for endpoint specifics and request/response DTOs.

Pagination, sorting, and merge-sort
-----------------------------------
- Controllers accept `Pageable` for paginated endpoints. Many service methods currently call DAO methods with limit/offset and then build a `PageImpl`.
- The project includes a stable, reusable merge-sort implementation under `com.amalitech.demo.utils` (`Sorter` interface and `MergeSorter` implementation). A `Sorter` bean is provided and injected into services.

Notes and recommendations:
- For large datasets prefer DB-side sorting and pagination (via Spring Data JPA `Pageable` support) to avoid loading full result sets into memory.
- The current service-level sorting picks the first `Sort.Order` from the incoming `Pageable`. If you need multi-field sorting, update the comparator factory to combine comparators.
- Property names used for sorting are defined as string literals today — prefer constants or an enum for robustness.

Caching
-------
- Caching is not yet implemented. Recommended steps:
  1. Add dependency: Spring Cache + a provider (e.g., Caffeine or Redis). Example Maven dependency for Caffeine:

```xml
<dependency>
  <groupId>com.github.ben-manes.caffeine</groupId>
  <artifactId>caffeine</artifactId>
  <version>3.1.6</version>
</dependency>
```

  2. Enable caching: add `@EnableCaching` to `SmartEcomSystemApplication` or a `@Configuration` class.
  3. Annotate read-heavy service methods with `@Cacheable("products")`, and update methods that modify data with `@CacheEvict` or `@Caching` rules.
  4. Document cache sizes/TTL in `application-*.properties`.

Transactions and rollback testing
---------------------------------
- Order creation and inventory updates are annotated with `@Transactional`. To test rollback, write tests that simulate a failure (e.g., throw exception after inventory update) and assert DB state unchanged.

Performance measurement & where to store results
-----------------------------------------------
Recommended storage for performance records (pick one based on your workflow):
- `docs/performance/` directory in the repository (recommended for reproducible records and versioning). Use CSV/JSON files, and a small markdown report per run.
- `performance/` top-level folder for raw results (tools output), and `docs/performance/summary.md` for analysis.

Suggested file structure and naming:
```
docs/performance/
  2026-02-03-bench-products-cache-off.json
  2026-02-03-bench-products-cache-on.json
  summary.md
```

Recommended metrics to store per run (JSON or CSV):
- timestamp, endpoint, concurrency, requests/sec, p50/p95/p99 latency, CPU/RAM usage, database query times (if available)

Quick benchmark example (wrk):
```bash
wrk -t4 -c100 -d30s http://localhost:8080/api/v1/products/
```

Also consider integrating Micrometer and Prometheus for long-running metrics.

Development & migration notes (Spring Data JPA)
-----------------------------------------------
The requirements call for migrating to Spring Data JPA. Plan:
1. Create repository interfaces in `src/main/java/com/amalitech/demo/repository` that extend `JpaRepository<T, ID>` for each aggregate (User, Product, Category, Orders, OrderItem, Reviews).
2. Replace service DAO usages with injected repositories. Start with `ProductRepository` as a proof-of-concept.
3. Implement derived queries (e.g. `List<Product> findByCategory_Id(Long categoryId)`) and custom `@Query` JPQL/native when needed.
4. Remove the corresponding manual JDBC DAO once covered by tests.

Contributing, coding standards and testing
-----------------------------------------
- Follow package layering: `restcontroller` → `services` → `dao`/`repository` → `models` → `dto` → `mapper`.
- Use constructor injection (already used across services).
- Return immutable DTOs where possible for API responses; avoid exposing entity objects directly.
- Logging: avoid logging sensitive data (passwords) in AOP. The project has `LoggingAspect` under `aop/` — keep it but exclude sensitive arguments.

Testing
-------
- Unit tests: `src/test/java` contains starters. Run:

```bash
./mvnw test
```

- Integration tests: ensure test database (Postgres/MySQL) or use Testcontainers for ephemeral DB.
- Add tests for:
  - Transaction rollback scenarios (insufficient stock)
  - Sorting and pagination behavior
  - Cache correctness and eviction
  - Performance regression (optional)

README: examples & useful commands
---------------------------------
Build and run:
```bash
./mvnw clean package -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar
```
Run tests:
```bash
./mvnw test
```
=
---

README created by the project maintainer tooling. If you'd like, I will now: (choose one)
1. Add `docs/performance/` with a README template and sample JSON schema for metrics.
2. Create a `ProductRepository` and migrate `ProductService` to use it.
3. Add a `CONTRIBUTING.md` and coding-style snippets.

Which of those should I do next? (or say "none" to stop here)
