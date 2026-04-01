# E-commerce Backend System

A backend REST API for an e-commerce platform built with Spring Boot, Spring Security, and Stripe payment integration.

## Tech Stack

- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with H2 (in-memory) database
- **Stripe** for payment processing
- **Lombok** for boilerplate reduction
- **JUnit 5 + Mockito** for unit testing
- **Swagger UI** for API documentation

## Features

- User registration and login with JWT tokens
- Role-based access control (USER / ADMIN)
- Product management (CRUD)
- Shopping cart management
- Order creation from cart
- Stripe payment intent generation
- Global exception handling

## Running the Application

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`

## API Documentation (Swagger)

```
http://localhost:8080/swagger-ui/index.html
```

## Running Tests

```bash
./mvnw clean test
```

## API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login and get JWT | No |
| GET | `/api/products` | List all products | No |
| GET | `/api/products/{id}` | Get product by ID | No |
| POST | `/api/products` | Create product | ADMIN |
| PUT | `/api/products/{id}` | Update product | ADMIN |
| DELETE | `/api/products/{id}` | Delete product | ADMIN |
| GET | `/api/cart` | View cart | USER |
| POST | `/api/cart/items` | Add item to cart | USER |
| PUT | `/api/cart/items` | Update item quantity | USER |
| DELETE | `/api/cart/items/{productId}` | Remove item from cart | USER |
| POST | `/api/orders/checkout` | Place order (creates Stripe PaymentIntent) | USER |
| GET | `/api/orders` | View your orders | USER |
| POST | `/api/orders/{id}/payment-status` | Update payment status | USER |

## Database

Uses H2 in-memory database for development. H2 console available at:
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:ecomdb
Username: sa
Password: (empty)
```
