# 🚀 Alertix Monitoring

![Alertix Logo](docs/assets/logo.png)

**Alertix Monitoring** is a modern, event-driven monitoring platform inspired by Nagios, built with cutting-edge technologies for cloud-native deployments.

## ✨ Features

- 🖥️ **Host & Service Monitoring**: Monitor servers, services, and applications
- 📊 **Real-time Metrics**: Live dashboards with Server-Sent Events (SSE)
- 🚨 **Smart Alerting**: Threshold-based and pattern-based alert rules
- 📧 **Multi-channel Notifications**: Email, Webhooks, Slack, PagerDuty
- 📈 **Time-series Storage**: PostgreSQL + TimescaleDB for efficient metrics storage
- 🔄 **Event-driven Architecture**: Apache Kafka for scalable event processing
- 🔒 **Role-based Access Control**: Admin, Operator, and Reader roles
- 🐳 **Containerized Deployment**: Docker Compose and Kubernetes-ready
- 📱 **Modern UI**: Angular 18 with Material Design
- 🧪 **Agent-based Monitoring**: Lightweight Go/Rust agents for system metrics

## 🏗️ Architecture

```
┌─────────────────┐
│  Angular 18 UI  │
└────────┬────────┘
         │
    ┌────▼────┐
    │ API GW  │
    └────┬────┘
         │
    ┌────▼────┬──────┬──────┬──────┐
    │Inventory│Sched │Metric│Alert │
    │ Service │uler  │ Proc │Engine│
    └────┬────┴──┬───┴──┬───┴──┬───┘
         │       │      │      │
    ┌────▼───────▼──────▼──────▼───┐
    │      Apache Kafka             │
    └────┬─────────────────────┬───┘
         │                     │
    ┌────▼────┐          ┌────▼────┐
    │PostreSQL│          │  Agents │
    │TimescaleDB          └─────────┘
    └─────────┘
```

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 17+ (for local development)
- Node.js 18+ (for frontend development)
- Maven 3.8+ (for backend development)

### Run with Docker Compose

```bash
# Clone the repository
git clone https://github.com/yourusername/alertix-monitoring.git
cd alertix-monitoring

# Start all services
docker-compose up -d

# Check services status
docker-compose ps

# View logs
docker-compose logs -f backend
```

Access the application:
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **Kafka UI**: http://localhost:8090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090

### Run Locally (Development)

#### Backend

```bash
cd backend

# Using Maven
./mvnw spring-boot:run

# Or with profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm start

# Navigate to http://localhost:4200
```

## 📂 Project Structure

```
alertix-monitoring/
├── backend/               # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/alertix/
│   │   │   │   ├── alerts/       # Alert engine
│   │   │   │   ├── auth/         # Authentication
│   │   │   │   ├── config/       # Configuration
│   │   │   │   ├── inventory/    # Host/Service management
│   │   │   │   ├── metrics/      # Metrics processing
│   │   │   │   ├── notifications/# Notification service
│   │   │   │   └── scheduler/    # Check scheduler
│   │   │   └── resources/
│   │   │       ├── db/migration/ # Flyway migrations
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/              # Angular 18 frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/     # Services, guards, interceptors
│   │   │   ├── features/ # Feature modules
│   │   │   └── shared/   # Shared components
│   │   ├── assets/
│   │   └── environments/
│   ├── Dockerfile
│   └── package.json
├── agents/                # Monitoring agents
│   ├── go-agent/         # Go implementation
│   └── rust-agent/       # Rust implementation
├── deploy/                # Deployment configs
│   ├── kubernetes/       # K8s manifests
│   ├── nginx/            # NGINX config
│   ├── prometheus/       # Prometheus config
│   └── grafana/          # Grafana dashboards
├── docs/                  # Documentation
│   ├── ARCHITECTURE.md
│   ├── API.md
│   └── ROADMAP.md
├── tests/                 # E2E tests
├── docker-compose.yml
└── README.md
```

## 📖 Documentation

- [Architecture Guide](docs/ARCHITECTURE.md)
- [Implementation Roadmap](docs/ROADMAP.md)
- [API Documentation](http://localhost:8080/api/swagger-ui.html) (when running)
- [User Guide](docs/USER_GUIDE.md)
- [Developer Guide](docs/DEVELOPER_GUIDE.md)

## 🔧 Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.yml`:

```yaml
alertix:
  jwt:
    secret: your-secret-key
  cors:
    allowed-origins:
      - http://localhost:4200
  checks:
    default-interval: 60  # seconds
```

### Environment Variables

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/alertix
DATABASE_USER=alertix
DATABASE_PASSWORD=alertix123

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_SECRET=your-super-secret-jwt-key

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## 🧪 Testing

### Backend Tests

```bash
cd backend

# Run unit tests
./mvnw test

# Run integration tests
./mvnw verify

# Generate coverage report
./mvnw jacoco:report
```

### Frontend Tests

```bash
cd frontend

# Run unit tests
npm test

# Run e2e tests
npm run e2e
```

## 📦 Building for Production

### Backend

```bash
cd backend
./mvnw clean package -DskipTests
# JAR will be in target/alertix-backend.jar
```

### Frontend

```bash
cd frontend
npm run build --prod
# Build artifacts will be in dist/
```

### Docker Images

```bash
# Build all images
docker-compose build

# Build specific service
docker-compose build backend
```

## 🔐 Security

- **Authentication**: JWT with access and refresh tokens
- **Authorization**: Role-based access control (RBAC)
- **Password Hashing**: BCrypt with cost factor 12
- **HTTPS**: Enforced in production
- **CORS**: Configurable allowed origins
- **Rate Limiting**: 100 requests/minute per user

### Default Credentials

⚠️ **Change these in production!**

- Username: `admin`
- Password: `admin123`

## 📊 Monitoring & Observability

The platform includes built-in observability:

- **Metrics**: Prometheus metrics at `/api/actuator/prometheus`
- **Health Checks**: `/api/actuator/health`
- **Grafana Dashboards**: Pre-configured dashboards
- **Logs**: Structured JSON logging

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by [Nagios](https://www.nagios.org/)
- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- UI powered by [Angular](https://angular.io/)
- Event streaming with [Apache Kafka](https://kafka.apache.org/)
- Time-series data with [TimescaleDB](https://www.timescale.com/)

## 📞 Support

- 📧 Email: support@alertix.io
- 💬 Discord: [Join our community](https://discord.gg/alertix)
- 🐛 Issues: [GitHub Issues](https://github.com/yourusername/alertix-monitoring/issues)
- 📚 Documentation: [Wiki](https://github.com/yourusername/alertix-monitoring/wiki)

## 🗺️ Roadmap

See [ROADMAP.md](docs/ROADMAP.md) for the detailed project roadmap covering Sprints S0-S9.

**Current Status**: Sprint 0 - Foundation ✅

**Next Milestone**: Sprint 1 - Inventory Management (Q1 2025)

---

**Made with ❤️ by the Alertix Team**
