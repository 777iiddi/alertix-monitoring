# Alertix Monitoring - Architecture Document

## 📋 Overview

**Alertix Monitoring** is a modern, event-driven monitoring platform inspired by Nagios, built with cutting-edge technologies designed for scalability, real-time processing, and cloud-native deployments.

## 🏗️ System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Frontend Layer                            │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Angular 18 SPA (SSE/WebSocket for real-time updates)   │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         API Gateway                              │
│             (Spring Cloud Gateway / NGINX)                       │
│              JWT Authentication & Rate Limiting                  │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────────┐   ┌──────────────┐
│  Inventory   │    │   Scheduler/     │   │    Alert     │
│   Service    │    │   Checker        │   │   Engine     │
└──────────────┘    └──────────────────┘   └──────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Kafka Event Bus                            │
│  Topics: metrics.raw, metrics.agg, alerts, notifications        │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────────┐   ┌──────────────┐
│   Metrics    │    │   Notification   │   │    Auth      │
│  Processor   │    │    Service       │   │  Service     │
└──────────────┘    └──────────────────┘   └──────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PostgreSQL + TimescaleDB                      │
│  Tables: hosts, services, metrics, alerts, users, events        │
└─────────────────────────────────────────────────────────────────┘

    ┌──────────────────────────────────────────────────────┐
    │              Monitoring Agents (Go/Rust)             │
    │   Push system metrics → Kafka (metrics.raw topic)   │
    └──────────────────────────────────────────────────────┘
```

## 🔧 Technology Stack

### Frontend
- **Framework**: Angular 18
- **State Management**: NgRx (Redux pattern)
- **Real-time**: Server-Sent Events (SSE) / WebSockets
- **UI Library**: Angular Material / PrimeNG
- **Charts**: Chart.js / D3.js
- **HTTP Client**: HttpClient with interceptors

### Backend
- **Framework**: Spring Boot 3.2+
- **Language**: Java 17
- **API**: REST + SSE
- **Security**: Spring Security (JWT/OAuth2)
- **Messaging**: Spring Kafka
- **Database**: Spring Data JPA + Flyway migrations
- **Caching**: Redis (optional)
- **Observability**: Micrometer + Prometheus

### Message Broker
- **Apache Kafka**: 3.6+
- **ZooKeeper**: 3.8+ (or KRaft mode)

### Database
- **PostgreSQL**: 15+
- **TimescaleDB**: 2.13+ (time-series extension)
- **Schema Versioning**: Flyway

### Agents
- **Language**: Go 1.21+ or Rust 1.75+
- **Deployment**: Systemd service / Docker
- **Metrics**: System (CPU, RAM, Disk, Network)
- **Protocol**: HTTP/gRPC to Kafka

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **Orchestration**: Kubernetes (future)
- **CI/CD**: GitHub Actions
- **Reverse Proxy**: NGINX

## 📦 Microservices Architecture

### 1. **Inventory Service**
**Responsibility**: Manage hosts, services, and their configurations

**Endpoints**:
- `POST /api/hosts` - Register new host
- `GET /api/hosts` - List all hosts
- `GET /api/hosts/{id}` - Get host details
- `PUT /api/hosts/{id}` - Update host
- `DELETE /api/hosts/{id}` - Delete host
- `POST /api/services` - Register service
- `GET /api/services` - List services
- `GET /api/hosts/{hostId}/services` - Get host services

**Database Tables**:
- `hosts` (id, name, ip, status, created_at, updated_at)
- `services` (id, host_id, name, type, config, status)

### 2. **Scheduler/Checker Service**
**Responsibility**: Execute scheduled checks (ping, HTTP, TCP, custom)

**Features**:
- Cron-based scheduling (Spring Scheduler)
- Configurable check intervals
- Active checks execution
- Results published to Kafka

**Check Types**:
- ICMP Ping
- HTTP/HTTPS
- TCP Port
- Custom scripts
- SNMP (future)

**Kafka Producer**: `metrics.raw` topic

### 3. **Metrics Processor Service**
**Responsibility**: Consume raw metrics, aggregate, and store

**Kafka Consumer**: `metrics.raw` topic
**Kafka Producer**: `metrics.agg` topic

**Processing Pipeline**:
1. Consume raw metrics
2. Validate & transform
3. Store in TimescaleDB
4. Aggregate (1min, 5min, 1hour)
5. Publish aggregated metrics

**Database Tables**:
- `metrics_raw` (hypertable)
- `metrics_aggregated` (continuous aggregates)

### 4. **Alert Engine Service**
**Responsibility**: Evaluate alert conditions and trigger notifications

**Kafka Consumer**: `metrics.agg` topic
**Kafka Producer**: `alerts`, `notifications` topics

**Alert Rules**:
- Threshold-based (CPU > 80%, Memory > 90%)
- Pattern-based (consecutive failures)
- Custom expressions (CEL, SpEL)

**State Management**:
- Alert states: OK, WARNING, CRITICAL, UNKNOWN
- De-duplication logic
- Escalation policies

**Database Tables**:
- `alert_rules` (id, host_id, service_id, condition, threshold)
- `alerts` (id, rule_id, status, triggered_at, resolved_at)

### 5. **Notification Service**
**Responsibility**: Send notifications via multiple channels

**Kafka Consumer**: `notifications` topic

**Channels**:
- Email (SMTP)
- Webhooks (HTTP POST)
- Slack (future)
- PagerDuty (future)
- SMS (future)

**Database Tables**:
- `notification_channels` (id, type, config)
- `notification_log` (id, alert_id, channel_id, sent_at, status)

### 6. **Auth Service**
**Responsibility**: User authentication & authorization

**Endpoints**:
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register
- `POST /api/auth/refresh` - Refresh token
- `GET /api/auth/me` - Current user

**Security**:
- JWT tokens (access + refresh)
- Password hashing (BCrypt)
- Role-based access control (RBAC)

**Roles**:
- `ADMIN`: Full access
- `OPERATOR`: Read/write access
- `READER`: Read-only access

**Database Tables**:
- `users` (id, username, email, password_hash, role)
- `refresh_tokens` (id, user_id, token, expires_at)

## 🗄️ Database Schema

### PostgreSQL + TimescaleDB Schema

```sql
-- Hosts table
CREATE TABLE hosts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    ip_address INET NOT NULL,
    status VARCHAR(50) DEFAULT 'UNKNOWN',
    os VARCHAR(100),
    tags JSONB,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Services table
CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    host_id BIGINT REFERENCES hosts(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- PING, HTTP, TCP, etc.
    check_config JSONB, -- JSON with check parameters
    check_interval INTEGER DEFAULT 60, -- seconds
    status VARCHAR(50) DEFAULT 'UNKNOWN',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(host_id, name)
);

-- Metrics (TimescaleDB hypertable)
CREATE TABLE metrics (
    time TIMESTAMPTZ NOT NULL,
    host_id BIGINT REFERENCES hosts(id),
    service_id BIGINT REFERENCES services(id),
    metric_name VARCHAR(100) NOT NULL,
    value DOUBLE PRECISION,
    unit VARCHAR(50),
    tags JSONB
);

SELECT create_hypertable('metrics', 'time');
CREATE INDEX idx_metrics_host_time ON metrics (host_id, time DESC);
CREATE INDEX idx_metrics_service_time ON metrics (service_id, time DESC);

-- Alert rules
CREATE TABLE alert_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    host_id BIGINT REFERENCES hosts(id),
    service_id BIGINT REFERENCES services(id),
    condition VARCHAR(500), -- e.g., "cpu_usage > 80"
    threshold DOUBLE PRECISION,
    severity VARCHAR(50), -- WARNING, CRITICAL
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Alerts
CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT REFERENCES alert_rules(id),
    status VARCHAR(50), -- FIRING, RESOLVED
    message TEXT,
    triggered_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'READER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Notification channels
CREATE TABLE notification_channels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50), -- EMAIL, WEBHOOK, SLACK
    config JSONB,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);
```

## 🚀 Kafka Topics

### Topic: `metrics.raw`
**Purpose**: Raw metrics from agents and checks
**Partitions**: 6
**Retention**: 7 days
**Schema**:
```json
{
  "timestamp": "2025-12-07T15:30:00Z",
  "hostId": 1,
  "serviceId": 2,
  "metricName": "cpu_usage",
  "value": 75.5,
  "unit": "percent",
  "tags": {}
}
```

### Topic: `metrics.agg`
**Purpose**: Aggregated metrics
**Partitions**: 3
**Retention**: 30 days
**Schema**:
```json
{
  "timestamp": "2025-12-07T15:30:00Z",
  "hostId": 1,
  "metricName": "cpu_usage",
  "avg": 70.2,
  "min": 50.0,
  "max": 85.0,
  "count": 60
}
```

### Topic: `alerts`
**Purpose**: Alert events
**Partitions**: 3
**Retention**: 90 days
**Schema**:
```json
{
  "alertId": 123,
  "ruleId": 45,
  "status": "FIRING",
  "severity": "CRITICAL",
  "message": "CPU usage exceeded 80% threshold",
  "timestamp": "2025-12-07T15:30:00Z"
}
```

### Topic: `notifications`
**Purpose**: Notification requests
**Partitions**: 3
**Retention**: 30 days
**Schema**:
```json
{
  "alertId": 123,
  "channels": ["email", "webhook"],
  "message": "CRITICAL: CPU at 95%",
  "timestamp": "2025-12-07T15:30:00Z"
}
```

## 🛡️ Security Architecture

### Authentication Flow
1. User submits credentials to `/api/auth/login`
2. Backend validates credentials
3. JWT access token (15min TTL) + refresh token (7d TTL) issued
4. Frontend stores tokens (HttpOnly cookies or localStorage)
5. All API requests include `Authorization: Bearer <token>`
6. Gateway validates JWT signature & expiration

### Authorization (RBAC)
```
ADMIN:
  - Full CRUD on all resources
  - User management
  - System configuration

OPERATOR:
  - CRUD on hosts, services, alerts
  - View metrics
  - Acknowledge alerts

READER:
  - Read-only access
  - View dashboards
```

### Data Protection
- Passwords: BCrypt (cost factor 12)
- Tokens: HS256 or RS256
- HTTPS only in production
- CORS configuration
- Rate limiting (100 req/min per user)

## 📊 Performance Requirements

### Latency
- API response time: < 200ms (p95)
- SSE event delivery: < 500ms
- Alert detection: < 1s from metric ingestion

### Throughput
- Metrics ingestion: 10,000 metrics/sec
- Active checks: 1,000 checks/min
- Concurrent users: 100+

### Scalability
- Horizontal scaling for all services
- Kafka partitioning for parallel processing
- TimescaleDB compression & retention policies

## 🔍 Observability

### Metrics (Prometheus)
- JVM metrics (heap, GC, threads)
- HTTP request rates & latencies
- Kafka consumer lag
- Database connection pool

### Logging (ELK Stack - future)
- Structured JSON logs
- Correlation IDs for tracing
- Log levels: DEBUG, INFO, WARN, ERROR

### Tracing (Jaeger - future)
- Distributed tracing across services
- Request flow visualization

## 📁 Project Structure

```
alertix-monitoring/
├── frontend/               # Angular 18 application
│   ├── src/app/
│   │   ├── core/          # Services, guards, interceptors
│   │   ├── features/      # Feature modules (hosts, alerts, etc.)
│   │   ├── shared/        # Shared components, pipes, directives
│   │   └── state/         # NgRx store
│   └── package.json
├── backend/                # Spring Boot monolith (or multi-module)
│   ├── src/main/java/com/alertix/
│   │   ├── inventory/     # Inventory service
│   │   ├── scheduler/     # Scheduler service
│   │   ├── metrics/       # Metrics processor
│   │   ├── alerts/        # Alert engine
│   │   ├── notifications/ # Notification service
│   │   ├── auth/          # Auth service
│   │   └── common/        # Shared utilities
│   ├── src/main/resources/
│   │   ├── db/migration/  # Flyway migrations
│   │   └── application.yml
│   └── pom.xml
├── agents/                 # Monitoring agents
│   ├── go-agent/          # Go implementation
│   └── rust-agent/        # Rust implementation (optional)
├── deploy/                 # Deployment configs
│   ├── docker-compose.yml
│   ├── kubernetes/        # K8s manifests (future)
│   └── nginx/             # NGINX config
├── docs/                   # Documentation
│   ├── ARCHITECTURE.md
│   ├── API.md
│   └── ADRs/              # Architecture Decision Records
├── tests/                  # E2E and integration tests
│   ├── e2e/
│   └── integration/
└── .github/workflows/      # CI/CD pipelines
```

## 🗺️ Deployment Architecture

### Development
```
docker-compose.yml:
  - postgres-timescale
  - kafka + zookeeper
  - backend (Spring Boot)
  - frontend (ng serve / nginx)
  - agent (local)
```

### Production (Kubernetes - future)
```
- Ingress (NGINX)
- Frontend pods (3 replicas)
- Backend pods (5 replicas, autoscaling)
- Kafka StatefulSet (3 brokers)
- PostgreSQL StatefulSet (1 primary + 2 replicas)
- Agent DaemonSet (on all nodes)
```

## 🔄 Data Flow Examples

### Example 1: CPU Metric Collection
1. Agent collects CPU usage every 60s
2. Agent sends HTTP POST to backend `/api/metrics/push`
3. Backend publishes to Kafka `metrics.raw`
4. Metrics Processor consumes, stores in TimescaleDB
5. Metrics Processor publishes aggregated data to `metrics.agg`
6. Alert Engine consumes, evaluates rules
7. If threshold exceeded → publishes to `alerts` topic
8. Notification Service consumes → sends email
9. Frontend receives SSE event → updates dashboard

### Example 2: HTTP Check
1. Scheduler triggers HTTP check for service
2. Checker executes HTTP GET request
3. Response time & status code captured
4. Published to Kafka `metrics.raw`
5. (Same flow as Example 1)

## 📝 Key Design Decisions (ADRs)

### ADR-001: Why Kafka?
- **Decision**: Use Kafka for event streaming
- **Rationale**: Decoupling, scalability, replay capability
- **Alternatives**: RabbitMQ (less throughput), Direct DB writes (tight coupling)

### ADR-002: Why TimescaleDB?
- **Decision**: Use TimescaleDB for time-series data
- **Rationale**: PostgreSQL compatibility, automatic partitioning, compression
- **Alternatives**: InfluxDB (less SQL-friendly), Prometheus (storage limitations)

### ADR-003: Monolith vs Microservices
- **Decision**: Start with modular monolith, extract services later
- **Rationale**: Faster initial development, easier testing
- **Future**: Extract to separate services as load increases

### ADR-004: JWT vs Session-based Auth
- **Decision**: JWT with refresh tokens
- **Rationale**: Stateless, suitable for distributed systems
- **Trade-off**: Cannot revoke access tokens before expiry

## 🎯 Success Metrics

### System Metrics
- Uptime: 99.9%
- Alert detection accuracy: 95%+
- False positive rate: < 5%

### User Metrics
- Dashboard load time: < 2s
- Real-time update latency: < 1s
- User satisfaction: 4.5/5

---

**Version**: 1.0  
**Last Updated**: 2025-12-07  
**Maintained By**: Alertix Team
