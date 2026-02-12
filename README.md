# Smart Ecom System

Smart Ecom System is a full‑stack e‑commerce application built with:

- **Backend**: Spring Boot 3, Java 21, JDBC/JPA, PostgreSQL/H2, Spring Security (JWT), Spring GraphQL, SpringDoc/OpenAPI.
- **Frontend**: Next.js 16 (App Router), React 19, TypeScript, Tailwind CSS, Zustand.

The backend exposes both a **REST API** and a **GraphQL API** for products, categories, users, carts, orders, inventory, and reviews. The Next.js app (included in this repository) provides a storefront and an admin console that talk to the same backend.

---

## Architecture Overview

### Backend (Java / Spring Boot)

Entry point:

- `com.amalitech.demo.SmartEcomSystemApplication`

Key packages under `src/main/java/com/amalitech/demo`:

- `config`
  - `SecurityConfig` – Spring Security, JWT filter chain, CORS.
  - `DataSourceConfig` – database configuration (PostgreSQL or H2).
  - `OpenApiConfig` – Swagger/OpenAPI setup.
  - `SortConfig` – wiring of `Sorter` / `MergeSorter` utilities.
  - `CorsConfig` – CORS for the frontend.
- `restcontroller`
  - REST endpoints for Users, Products, Categories, Inventory, Orders, Cart, Reviews.
- `graphqlcontroller`
  - GraphQL controllers: products, categories, users, inventory, orders, reviews.
- `services` and `services.interfaces`
  - Business logic for each domain; interfaces define contracts, implementations contain orchestration and validation.
- `dao.interfaces` and `dao.implementations`
  - DAO layer, mainly JDBC (`Jdbc*Dao`) for Users, Products, Categories, Orders, OrderItems, Inventory, Cart, CartItems, Reviews.
- `models`
  - Domain models / JPA entities: `User`, `Product`, `Category`, `Inventory`, `Orders`, `OrderItem`, `Cart`, `CartItems`, `Reviews`, etc.
- `dto.request` and `dto.response`
  - Request and response DTOs for REST and GraphQL (e.g. `UserRequest`, `ProductRequest`, `OrderRequest`, `ReviewRequest`, `UserResponse`, `ProductResponse`, `OrderResponse`, `ReviewResponse`).
- `mapper`
  - MapStruct and manual mappers (`UserMapper`, `ProductMapper`, `OrdersMapper`, `InventoryMapper`, `CategoryMapper`, `CartMapper`, `CartItemMapper`).
- `security`
  - `JwtService`, `JwtAuthenticationFilter`, `CustomUserDetailsService`, `CurrentUser` abstraction.
- `validation`
  - Custom annotations and validators such as `@UniqueUser` and `@StrongPassword`.
- `exceptions`
  - Custom exception types and a central `ExceptionHandlers` advice.
- `aop`
  - `LoggingAspect` for cross‑cutting logging.
- `utils`
  - Shared utilities (`PasswordUtils`, `Sorter`, `MergeSorter`).

### Frontend (Next.js / React / TypeScript)

Located under:

- `src/main/resources/frontend/ecommerce`

Key pieces:

- `app/**` – Next.js App Router structure
  - Storefront routes under `(storefront)`:
    - `/products`, `/products/[id]`
    - `/categories/[categoryId]`
    - `/search`
  - User/account routes:
    - `/login`, `/register`
    - `/account`, `/account/orders`, `/account/orders/[id]`
  - Checkout and cart:
    - `/cart`, `/checkout`, `/checkout/success`
  - Admin routes under `(admin)`:
    - `/admin`, `/admin/products`, `/admin/users`, `/admin/orders`, `/admin/categories`
- `components/**`
  - Layout components such as `Header`, `Footer`, `CartBootstrap`.
  - Storefront components such as `ProductGrid`, `ProductCard`.
- `lib/**`
  - Typed client libraries and state stores:
    - `auth-api.ts`, `user.ts`, `products.ts`, `categories.ts`, `orders.ts`, `cart-api.ts`, `reviews.ts`.
    - `secured-fetch.ts` – wraps `fetch` with JWT handling.
    - `auth-store.ts`, `cart-store.ts` – global state via Zustand.
- Tooling:
  - `package.json`, `tsconfig.json`, `next.config.ts`, Tailwind/PostCSS configs.

The frontend talks to the backend via:

- REST (for some flows like login and review creation).
- GraphQL (for rich product, order, and review queries).

The base backend URL is configured with `NEXT_PUBLIC_API_BASE_URL`.

---

## Features

### Authentication & Authorization

- Username/password authentication with JWT tokens issued by the backend.
- `JwtAuthenticationFilter` extracts and validates Bearer tokens on each request.
- `CustomUserDetailsService` integrates the domain `User` model with Spring Security.
- Role‑based access control using `@PreAuthorize`:
  - Admin‑only operations for user management and some order operations.
  - Admin/seller roles for product and category management.
  - Customer/admin roles for creating reviews and placing orders.

### Users

- Register a new account (REST and GraphQL).
- Login to obtain a JWT; frontend stores the token and attaches it via `secured-fetch.ts`.
- Admin can:
  - List users with pagination.
  - Get user by ID.
  - Update and delete users.
- Validation:
  - `@UniqueUser` to prevent duplicate usernames/emails.
  - `@StrongPassword` to enforce password complexity.

### Products & Categories

- Product features:
  - CRUD operations for products.
  - Price, stock quantity, and category association.
  - Pagination and (optionally) category filtering.
- Category features:
  - CRUD operations on product categories.
- Exposed both via REST and GraphQL.
- GraphQL:
  - `products(page, size): ProductPage` and `productById(id)`.
  - `createProduct`, `updateProduct`, `deleteProduct` mutations.
  - `categories`, `categoryById`, `createCategory`, `updateCategory`, `deleteCategory`.
  - `SchemaMapping` for `Product.category` resolves the category from the product.

### Inventory

- Track per‑product inventory:
  - `stockQuantity`, `reservedQuantity`, `stockStatus`.
- CRUD operations via REST and GraphQL.
- GraphQL `Inventory` type resolves the related `Product` via `@SchemaMapping`.

### Cart & Checkout

- Domain model for cart and cart items.
- REST endpoints to:
  - Add items to cart.
  - Update or remove cart items.
  - Change cart status.
- Frontend uses Zustand (`cart-store.ts`) for client‑side state and `cart-api.ts` to persist to the backend.

### Orders

- Place orders for one or more items using `OrderRequest` / `OrderInput`.
- Track status via `OrderStatus` enum (pending, processing, delivered, cancelled).
- View order history (REST and GraphQL).
- GraphQL:
  - `orders(page, size)` and `orderById(id)` queries.
  - `createOrder`, `updateOrderStatus`, `deleteOrder` mutations.
  - `SchemaMapping` for `OrderItem.product` resolves each item’s product.

### Reviews

- Users can create reviews for products, including rating and description.
- Retrieve reviews:
  - All reviews.
  - By product.
  - By user.
- REST: `ReviewsController` exposes `/api/v1/reviews/**` endpoints.
- GraphQL: `ReviewGraphqlController` exposes `reviews`, `reviewById`, `reviewsByProduct`, `reviewsByUser` plus create/delete mutations.
- GraphQL `Review` type exposes both:
  - Flat fields from `ReviewResponse` (`id`, `productId`, `reviewerDisplay`, `rating`, `description`, `createdAt`).
  - Derived `product` and `user` fields resolved via schema mappings.
- Frontend `lib/reviews.ts` uses GraphQL to fetch reviews for a product.

### Documentation & Observability

- OpenAPI / Swagger UI via SpringDoc for the REST API.
- Substantial use of `@Operation`, `@ApiResponses`, and `@Schema` annotations on controllers and DTOs.
- `LoggingAspect` (AOP) for cross‑cutting logging around service methods.

---

## Getting Started

### Backend (Spring Boot)

#### Prerequisites

- Java 21
- Maven 3.x
- PostgreSQL (for persistence) or rely on H2 for local/dev.

#### Run tests

From the project root:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

> Note: At the time of writing, some tests depend on security configuration (JWT filter and `JwtService`) and may need a test‑specific security configuration or mocks.

#### Run the application

From the project root:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

By default the backend starts on:

- `http://localhost:8080`

#### REST API

Representative endpoints (non‑exhaustive):

- `/api/v1/users/**` – user registration, login, retrieval, admin management.
- `/api/v1/products/**` – product catalog management.
- `/api/v1/categories/**` – category management.
- `/api/v1/inventory/**` – inventory records per product.
- `/api/v1/cart/**` – cart operations.
- `/api/v1/orders/**` – order management.
- `/api/v1/reviews/**` – review management.

Swagger UI (if enabled) is available at:

- `http://localhost:8080/swagger-ui/index.html`

#### GraphQL API

GraphQL endpoint:

- `POST /graphql`

Schema definition is in:

- `src/main/resources/graphql/schema.graphqls`

You can use GraphiQL or any GraphQL client (Insomnia, Postman, Altair) to:

- Query products, categories, users, inventory, orders, reviews.
- Perform mutations such as `createProduct`, `createOrder`, `createReview`, etc.

---

### Frontend (Next.js)

#### Prerequisites

- Node.js 18+
- npm (or yarn/pnpm/bun)

#### Install dependencies

From the frontend directory:

```bash
cd src/main/resources/frontend/ecommerce
npm install
```

#### Configure backend URL

Create a `.env.local` file in `src/main/resources/frontend/ecommerce`:

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

#### Run the development server

```bash
npm run dev
```

Then open:

- `http://localhost:3000`

You should see the storefront home page. The app will:

- Use REST and GraphQL to talk to the backend at `NEXT_PUBLIC_API_BASE_URL`.
- Respect auth state stored in the client (JWT) for protected actions like placing orders or accessing the admin area.

---

## Development Notes

This codebase follows a layered, domain‑oriented structure and a few conventions worth keeping in mind when extending it:

- **DTOs over entities**: Controllers (REST and GraphQL) exchange DTOs (`*Request`, `*Response`). Do not expose JPA entities directly.
- **Service as the source of truth**: All business rules (validation, authorization checks beyond simple role guards, invariants) should live in services. Controllers should delegate and perform minimal orchestration.
- **GraphQL schema first**: When you add new capabilities, update `schema.graphqls` and mirror that in:
  - Appropriate `@QueryMapping` / `@MutationMapping` methods.
  - `@SchemaMapping` resolvers for nested fields.
  - Frontend GraphQL queries in `src/main/resources/frontend/ecommerce/lib/*`.
- **Security alignment**: When you change roles/permissions:
  - Update `SecurityConfig` and `@PreAuthorize` annotations.
  - Adjust integration/unit tests and any assumptions in the frontend (e.g., admin‑only sections).
- **Testing**:
  - The project ships with REST controller and service tests.
  - When adding new endpoints, follow the same testing patterns.
  - For security‑sensitive endpoints, prefer tests that cover both authorized and unauthorized scenarios.

---

## License

This repository does not currently declare an explicit license in `pom.xml` or a LICENSE file. Before using it in production or redistributing, clarify the licensing terms with the code owner or add an appropriate license file.
