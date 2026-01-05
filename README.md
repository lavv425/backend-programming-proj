# 📅 Booker - Appointment Management System

Backend REST API for a complete appointment and booking management system, built with Spring Boot 3.5.7 and Java 21.

## 🚀 Key Features

- **Appointment Management**: Complete booking and appointment management system
- **Multi-role Support**: Support for Admin, Professional, and Customer with differentiated permissions
- **Authentication & Authorization**: JWT-based authentication with Spring Security
- **Payment Processing**: Stripe integration for online payments
- **File Management**: Upload and storage with MinIO
- **Review System**: Reviews and ratings for professionals
- **Email Notifications**: Automatic email sending with MailHog (dev)
- **Advanced Logging**: Custom logging system for operation tracking

## 🏗️ Architecture

### Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + OAuth2 Resource Server + JWT
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

### External Services

- **MinIO**: Object storage for avatar and file management
- **Stripe**: Payment processing (with stripe-mock for development)
- **MailHog**: SMTP testing for emails (development)
- **PostgreSQL**: Relational database

## 📦 Application Modules

```
backend/src/main/java/com/booker/
├── modules/
│   ├── admin/          # Admin management
│   ├── appointment/    # Appointment management
│   ├── auth/           # Authentication and registration
│   ├── customer/       # Customer management
│   ├── professional/   # Professional management
│   ├── payment/        # Stripe payment management
│   ├── review/         # Review system
│   ├── service/        # Service catalog
│   ├── user/           # User management
│   └── log/            # Application logging
├── security/           # Security configuration and JWT
├── services/           # Support services (Email, MinIO, Stripe)
└── utils/              # Utilities and global error handling
```

## 🛠️ Setup and Installation

### Prerequisites

- Docker & Docker Compose
- Java 21 (for local development)
- Maven 3.9+ (for local development)

### Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd be-proj
   ```

2. **Configure environment variables** (optional)
   
   Create a `.env` file in the root:
   ```env
   JWT_SECRET=your-secret-key-min-32-characters-here
   MINIO_ROOT_USER=minioadmin
   MINIO_ROOT_PASSWORD=minioadmin
   STRIPE_API_KEY=sk_test_mock_key
   MAIL_FROM=noreply@booker.local
   ```

3. **Start all services**
   ```bash
   docker compose up -d
   ```

4. **The application will be available at:**
   - Backend API: http://localhost:8080/api/v1
   - MinIO Console: http://localhost:9021
   - MailHog Web UI: http://localhost:8025
   - PostgreSQL: localhost:5432

### Local Development (without Docker)

1. **Start support services**
   ```bash
   docker compose up -d db minio stripe-mock mailhog
   ```

2. **Run the application**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

## 🧪 Testing

### Run tests

```bash
# Inside the container
docker compose exec backend sh -c "cd /app && ./mvnw test"

# Or locally
cd backend
./mvnw test
```

### Test Coverage

Tests cover:
- Authentication and authorization
- Appointment management
- Payment system
- Ownership checks for security
- Review and log management

## 🔒 Security

- **JWT Authentication**: Token-based authentication with configurable expiration
- **Ownership Checks**: Automatic permission verification on resources
- **Password Encoding**: BCrypt for password hashing
- **CORS**: Customizable CORS configuration
- **Validation**: Bean Validation for input sanitization

## 📡 API Documentation

The APIs follow a RESTful structure:

```
/api/v1/
├── /auth              # Login, registration, refresh token
├── /appointments      # Appointment CRUD
├── /professionals     # Professional management
├── /customers         # Customer management
├── /services          # Service catalog
├── /payments          # Payment management
├── /reviews           # Review system
└── /admin             # Administrative functions
```

### Postman Collection

A complete Postman collection is available at `postman/booker.postman_collection.json`

To use it:
1. Import the collection into Postman
2. Configure environment variables if needed
3. Test the APIs

## 🗄️ Database

The PostgreSQL database is automatically initialized with the schema via Hibernate.

Main schema:
- Users (multi-role: admin, professional, customer)
- Appointments
- Services
- Payments
- Reviews
- Logs

## 📧 Email System

In development, emails are captured by MailHog:
- SMTP Server: `mailhog:1025`
- Web UI: http://localhost:8025

In production, configure a real SMTP provider in the environment variables.

## 💳 Payments

Stripe integration for payments:
- **Development**: Uses stripe-mock at http://localhost:12111
- **Production**: Configure `STRIPE_API_KEY` with real key

## 📦 File Storage

MinIO for avatar and file management:
- **Endpoint**: http://localhost:9020
- **Console**: http://localhost:9021
- **Default bucket**: `avatars`

## 🔧 Configuration

Main configurations are located in:
- `backend/src/main/resources/application.yaml`
- `docker-compose.yaml` for environment variables
- `.env` for secrets (git-ignored)

### Main Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL database host | localhost |
| `DB_PORT` | Database port | 5432 |
| `DB_NAME` | Database name | booker_db |
| `JWT_SECRET` | Secret for JWT signing | (required in prod) |
| `MINIO_ENDPOINT` | MinIO endpoint | http://localhost:9020 |
| `STRIPE_API_KEY` | Stripe API key | sk_test_mock_key |
| `MAIL_HOST` | SMTP host | mailhog |

## 📝 Logging

Custom logging system with types:
- `SUCCESS`: Successfully completed operations
- `INFO`: General information (disable in production)
- `WARNING`: Warnings
- `ERROR`: Errors

Logs are saved to the database for traceability.

## 🐳 Docker

### Docker Compose Services

- **db**: PostgreSQL database
- **minio**: Object storage
- **stripe-mock**: Mock Stripe API
- **mailhog**: Email testing
- **backend**: Spring Boot application

### Hot Reload

The Docker setup includes hot reload for development:
- Changes in `src/` are automatically synced
- Changes in `pom.xml` trigger rebuild

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is developed for educational/portfolio purposes.

## 👤 Author

Developed as a backend project for a booking system.

## 🔗 Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MinIO Documentation](https://min.io/docs/minio/linux/index.html)
- [Stripe API Documentation](https://stripe.com/docs/api)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

**Note**: Remember to change the default secrets before deploying to production! 🔐
