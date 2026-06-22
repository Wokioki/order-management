# Order Management

Full-stack order management application built with Spring Boot, PostgreSQL, JWT authentication and React.

The project demonstrates a complete customer and admin workflow: browsing products, creating orders, managing order statuses, and maintaining products/categories through an admin UI.

---

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- JWT authentication
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway migrations
- Bean Validation
- Swagger / OpenAPI
- JUnit / Mockito / MockMvc

### Frontend

- React
- TypeScript
- Vite
- React Router
- Fetch API
- Global CSS styling

### Infrastructure

- Docker
- Docker Compose
- Nginx for serving React production build
- PostgreSQL container

---

## Features

### Authentication

- User registration
- User login
- JWT-based authentication
- Current user endpoint
- Role-based access control

### Roles

- `CUSTOMER`
- `MANAGER`
- `ADMIN`

### Product Catalog

- Public product listing
- Public product details
- Search products
- Sort products
- View stock and price

### Orders

- Authenticated customers can create orders
- Product stock decreases after order creation
- Customers can view their own orders
- Admins/managers can view all orders
- Admins/managers can update order status
- Cancelling an order restores product stock

### Admin Panel

- Manage products
    - Create product
    - Edit product
    - Delete product if not used in orders
- Manage categories
    - Create category
    - Edit category
    - Delete category if not used by products
- Manage orders
    - View all customer orders
    - Update order status

---

## Seed Data

On startup, the backend creates demo data if it does not already exist.

### Admin User

```text
Email: admin@example.com
Password: admin123
Role: ADMIN
```

### Demo Categories

```text
Electronics
Books
Office
```

### Demo Products

```text
Laptop Pro
Smartphone Ultra
Mechanical Keyboard
Clean Code
Office Chair
```

---

## Running with Docker

Make sure Docker Desktop is running.

From the project root:

```bash
docker compose up --build
```

The application will be available at:

```text
Frontend: http://localhost:3000
Swagger:  http://localhost:8081/swagger-ui/index.html
API:      http://localhost:8081
Postgres: localhost:5434
```

To stop the application:

```bash
docker compose down
```

To reset the database completely:

```bash
docker compose down -v
docker compose up --build
```

---

## Local Development

### Backend

From the `server` directory:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend runs on:

```text
http://localhost:8081
```

### Frontend

From the `client` directory:

```bash
npm install
npm run dev
```

Frontend dev server runs on:

```text
http://localhost:5173
```

The Vite dev server proxies `/api` requests to the backend.

---

## Environment Variables

The backend supports the following environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION
SEED_ADMIN_EMAIL
SEED_ADMIN_PASSWORD
```

Example Docker values:

```text
DB_URL=jdbc:postgresql://postgres:5432/order_management
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=my-super-secret-key-for-order-management-app-2026
SEED_ADMIN_EMAIL=admin@example.com
SEED_ADMIN_PASSWORD=admin123
```

---

## API Documentation

Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

Main API groups:

```text
/api/auth
/api/products
/api/categories
/api/orders
```

---

## Main API Endpoints

### Auth

```http
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

### Products

```http
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

Product write operations require `ADMIN` or `MANAGER`.

### Categories

```http
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

Category write operations require `ADMIN` or `MANAGER`.

### Orders

```http
POST  /api/orders
GET   /api/orders/my
GET   /api/orders/{id}
GET   /api/orders
PATCH /api/orders/{id}/status
```

Admin order listing and status updates require `ADMIN` or `MANAGER`.

---

## Database Migrations

Flyway is used for schema migrations.

Migration files are located in:

```text
server/src/main/resources/db/migration
```

Current schema includes:

```text
users
categories
products
orders
order_items
```

---

## Testing

Run backend tests from the `server` directory:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Test coverage includes:

- Auth service tests
- Product service tests
- Order service tests
- Auth integration tests
- Product access integration tests
- Order flow integration tests

---

## Project Structure

```text
order-management
├── client
│   ├── src
│   │   ├── api
│   │   ├── components
│   │   ├── context
│   │   ├── pages
│   │   └── types
│   ├── Dockerfile
│   └── nginx.conf
│
├── server
│   ├── src/main/java/com/portfolio/ordermanagement
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── enums
│   │   ├── exception
│   │   ├── mapper
│   │   ├── repository
│   │   ├── security
│   │   ├── service
│   │   └── specification
│   ├── src/main/resources/db/migration
│   └── Dockerfile
│
├── docker-compose.yml
└── README.md
```

---

## Screenshots

Add screenshots here before sharing the project publicly.

Recommended screenshots:

```text
Home page
Products page
Product details page
My Orders page
Admin Orders page
Admin Products page
Admin Categories page
Swagger UI
```

Example:

```md
![Home page](docs/screenshots/home.png)
```

---

## Notes

Physical delete can fail for products/categories that are already referenced by orders or products. This is intentional for data integrity.

A production-ready version would typically use soft delete with an `active` flag instead of hard deletion.

---

## Author

Created as a portfolio full-stack project.

GitHub: https://github.com/Wokioki/order-management