# E-commerce Application with Stripe Integration

This is a full-stack e-commerce repository featuring Spring Boot (Backend) and React (Frontend). The project includes full JWT authentication, a product catalog with local images, and a functional Stripe payment checkout.

## Repository Contents
- **Backend**: Spring Boot 3.4.2 application with Spring Security/JPA.
- **Frontend**: React (Vite) frontend with Stripe Elements support.

## Setup Instructions

### Backend (Spring Boot)
1. Import the Maven project.
2. Add your Stripe Secret key in `application.properties` (stripe.api.key).
3. Run `ProductCartApplication.java`. Default port is 8080.

### Frontend (React)
1. Go to `/frontend`.
2. Run `npm install`.
3. Set your Stripe Publishable Key in `Checkout.jsx`.
4. Run `npm run dev`.

## Local Testing
Use the Stripe test card: `4242 4242 4242 4242`.
Zip Code: `12345`
Expiration: Any future date.
CVC: `123`
