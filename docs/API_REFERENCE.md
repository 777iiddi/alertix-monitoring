# 🌐 Alertix Monitoring - API Reference

**Base URL**: `http://localhost:8080/api`

**Version**: 0.0.1-SNAPSHOT

---

## 📋 Table of Contents

1. [Authentication](#authentication)
2. [Hosts](#hosts)
3. [Services](#services)
4. [Alerts](#alerts)
5. [Metrics](#metrics)
6. [Notifications](#notifications)
7. [Users](#users)
8. [Health & Monitoring](#health--monitoring)

---

## 🔐 Authentication

All API endpoints (except public ones) require JWT authentication.

### Login

```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK)**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 900,
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@alertix.local",
    "role": "ADMIN"
  }
}
```

### Refresh Token

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Get Current User

```http
GET /auth/me
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@alertix.local",
  "role": "ADMIN",
  "enabled": true,
  "createdAt": "2025-12-07T15:30:00Z"
}
```

---

## 🖥️ Hosts

### Get All Hosts

```http
GET /hosts
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
[
  {
    "id": 1,
    "name": "web-server-01",
    "ipAddress": "192.168.1.10",
    "status": "UP",
    "os": "Ubuntu 22.04",
    "description": "Production web server",
    "tags": {
      "environment": "production",
      "region": "us-east"
    },
    "createdAt": "2025-12-01T10:00:00Z",
    "updatedAt": "2025-12-07T15:30:00Z"
  }
]
```

### Get Host by ID

```http
GET /hosts/{id}
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
{
  "id": 1,
  "name": "web-server-01",
  "ipAddress": "192.168.1.10",
  "status": "UP",
  "os": "Ubuntu 22.04",
  "description": "Production web server",
  "tags": {
    "environment": "production",
    "region": "us-east"
  },
  "createdAt": "2025-12-01T10:00:00Z",
  "updatedAt": "2025-12-07T15:30:00Z"
}
```

### Create Host

```http
POST /hosts
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "db-server-01",
  "ipAddress": "192.168.1.20",
  "os": "Ubuntu 22.04",
  "description": "PostgreSQL database server",
  "tags": {
    "environment": "production",
    "type": "database"
  }
}
```

**Response (201 Created)**:
```json
{
  "id": 2,
  "name": "db-server-01",
  "ipAddress": "192.168.1.20",
  "status": "UNKNOWN",
  "os": "Ubuntu 22.04",
  "description": "PostgreSQL database server",
  "tags": {
    "environment": "production",
    "type": "database"
  },
  "createdAt": "2025-12-07T15:30:00Z",
  "updatedAt": "2025-12-07T15:30:00Z"
}
```

### Update Host

```http
PUT /hosts/{id}
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "db-server-01",
  "ipAddress": "192.168.1.20",
  "os": "Ubuntu 22.04 LTS",
  "description": "Updated description",
  "tags": {
    "environment": "production",
    "type": "database"
  }
}
```

**Response (200 OK)**:
```json
{
  "id": 2,
  "name": "db-server-01",
  "ipAddress": "192.168.1.20",
  "status": "UP",
  "os": "Ubuntu 22.04 LTS",
  "description": "Updated description",
  "tags": {
    "environment": "production",
    "type": "database"
  },
  "createdAt": "2025-12-07T15:30:00Z",
  "updatedAt": "2025-12-07T16:00:00Z"
}
```

### Delete Host

```http
DELETE /hosts/{id}
Authorization: Bearer {access_token}
```

**Response (204 No Content)**

### Get Hosts by Status

```http
GET /hosts/status/{status}
Authorization: Bearer {access_token}
```

**Parameters**:
- `status`: `UP`, `DOWN`, `UNKNOWN`, `UNREACHABLE`

**Response (200 OK)**:
```json
[
  {
    "id": 1,
    "name": "web-server-01",
    "ipAddress": "192.168.1.10",
    "status": "UP",
    ...
  }
]
```

### Get Problematic Hosts

```http
GET /hosts/problematic
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
[
  {
    "id": 3,
    "name": "backup-server",
    "ipAddress": "192.168.1.30",
    "status": "DOWN",
    ...
  }
]
```

---

## ⚙️ Services

### Get All Services

```http
GET /services
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
[
  {
    "id": 1,
    "hostId": 1,
    "name": "NGINX",
    "type": "HTTP",
    "checkConfig": {
      "url": "http://192.168.1.10:80",
      "expectedStatus": 200,
      "timeout": 10
    },
    "checkInterval": 60,
    "timeout": 30,
    "retries": 3,
    "status": "OK",
    "lastCheckTime": "2025-12-07T15:29:00Z",
    "nextCheckTime": "2025-12-07T15:30:00Z",
    "createdAt": "2025-12-01T10:00:00Z"
  }
]
```

### Get Services by Host

```http
GET /services?hostId={hostId}
Authorization: Bearer {access_token}
```

### Create Service

```http
POST /services
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "hostId": 1,
  "name": "PostgreSQL",
  "type": "TCP",
  "checkConfig": {
    "port": 5432,
    "timeout": 10
  },
  "checkInterval": 60,
  "timeout": 30,
  "retries": 3
}
```

**Service Types**: `PING`, `HTTP`, `HTTPS`, `TCP`, `SSH`, `CUSTOM`

---

## 🚨 Alerts

### Get All Alerts

```http
GET /alerts
Authorization: Bearer {access_token}
```

**Query Parameters**:
- `status`: Filter by status (`FIRING`, `RESOLVED`, `ACKNOWLEDGED`)
- `severity`: Filter by severity (`INFO`, `WARNING`, `CRITICAL`)
- `from`: Start date (ISO 8601)
- `to`: End date (ISO 8601)

**Response (200 OK)**:
```json
[
  {
    "id": 1,
    "ruleId": 1,
    "hostId": 1,
    "serviceId": 1,
    "status": "FIRING",
    "severity": "CRITICAL",
    "message": "CPU usage exceeded 80% threshold (current: 92%)",
    "value": 92.5,
    "triggeredAt": "2025-12-07T15:25:00Z",
    "resolvedAt": null,
    "acknowledgedAt": null,
    "acknowledgedBy": null
  }
]
```

### Get Alert by ID

```http
GET /alerts/{id}
Authorization: Bearer {access_token}
```

### Acknowledge Alert

```http
POST /alerts/{id}/acknowledge
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
{
  "id": 1,
  "status": "ACKNOWLEDGED",
  "acknowledgedAt": "2025-12-07T15:30:00Z",
  "acknowledgedBy": "admin",
  ...
}
```

### Get Alert Rules

```http
GET /alert-rules
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
[
  {
    "id": 1,
    "name": "High CPU Usage",
    "hostId": 1,
    "serviceId": null,
    "condition": "cpu_usage > 80",
    "threshold": 80.0,
    "duration": 300,
    "severity": "WARNING",
    "enabled": true,
    "description": "Triggers when CPU usage exceeds 80% for 5 minutes"
  }
]
```

### Create Alert Rule

```http
POST /alert-rules
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "High Memory Usage",
  "hostId": 1,
  "condition": "memory_usage > 90",
  "threshold": 90.0,
  "duration": 300,
  "severity": "CRITICAL",
  "enabled": true,
  "description": "Critical alert for memory usage"
}
```

---

## 📊 Metrics

### Query Metrics

```http
GET /metrics
Authorization: Bearer {access_token}
```

**Query Parameters**:
- `hostId`: Host ID (required)
- `metricName`: Metric name (e.g., `cpu_usage`, `memory_usage`)
- `from`: Start time (ISO 8601)
- `to`: End time (ISO 8601)

**Response (200 OK)**:
```json
{
  "hostId": 1,
  "metricName": "cpu_usage",
  "data": [
    {
      "time": "2025-12-07T15:00:00Z",
      "value": 45.2,
      "unit": "percent"
    },
    {
      "time": "2025-12-07T15:01:00Z",
      "value": 47.8,
      "unit": "percent"
    }
  ]
}
```

### Push Metrics (Agent API)

```http
POST /agents/metrics
Content-Type: application/json
X-API-Key: {agent_api_key}

{
  "agentId": "agent-01",
  "hostId": 1,
  "timestamp": "2025-12-07T15:30:00Z",
  "metrics": [
    {
      "name": "cpu_usage",
      "value": 52.3,
      "unit": "percent"
    },
    {
      "name": "memory_usage",
      "value": 68.5,
      "unit": "percent"
    },
    {
      "name": "disk_usage",
      "value": 45.0,
      "unit": "percent"
    }
  ]
}
```

**Response (202 Accepted)**

---

## 📧 Notifications

### Get Notification Channels

```http
GET /notification-channels
Authorization: Bearer {access_token}
```

**Response (200 OK)**:
```json
[
  {
    "id": 1,
    "name": "Email - Ops Team",
    "type": "EMAIL",
    "config": {
      "to": "ops@example.com",
      "from": "alerts@alertix.local"
    },
    "enabled": true
  },
  {
    "id": 2,
    "name": "Webhook - Slack",
    "type": "WEBHOOK",
    "config": {
      "url": "https://hooks.slack.com/services/...",
      "method": "POST"
    },
    "enabled": true
  }
]
```

### Create Notification Channel

```http
POST /notification-channels
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "PagerDuty Integration",
  "type": "WEBHOOK",
  "config": {
    "url": "https://events.pagerduty.com/v2/enqueue",
    "method": "POST",
    "headers": {
      "Authorization": "Token token=..."
    }
  },
  "enabled": true
}
```

---

## 👥 Users

### Get All Users

```http
GET /users
Authorization: Bearer {access_token}
```

**Requires**: ADMIN role

**Response (200 OK)**:
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@alertix.local",
    "role": "ADMIN",
    "enabled": true,
    "createdAt": "2025-12-01T10:00:00Z"
  }
]
```

### Create User

```http
POST /users
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "username": "operator1",
  "email": "operator1@example.com",
  "password": "SecurePassword123!",
  "role": "OPERATOR"
}
```

**Roles**: `ADMIN`, `OPERATOR`, `READER`

---

## ❤️ Health & Monitoring

### Health Check

```http
GET /actuator/health
```

**Response (200 OK)**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "kafka": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 250790436864,
        "free": 100790436864
      }
    }
  }
}
```

### Application Info

```http
GET /actuator/info
```

### Prometheus Metrics

```http
GET /actuator/prometheus
```

**Response (200 OK)**:
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="PS Eden Space",} 1.2345678E8
...
```

---

## 🔄 Server-Sent Events (Real-time)

### Subscribe to Alert Stream

```http
GET /stream/alerts
Authorization: Bearer {access_token}
Accept: text/event-stream
```

**Response** (continuous stream):
```
event: alert
data: {"id":1,"severity":"CRITICAL","message":"High CPU usage","triggeredAt":"2025-12-07T15:30:00Z"}

event: alert
data: {"id":2,"severity":"WARNING","message":"Disk space low","triggeredAt":"2025-12-07T15:31:00Z"}
```

### Subscribe to Metrics Stream

```http
GET /stream/metrics?hostId={hostId}
Authorization: Bearer {access_token}
Accept: text/event-stream
```

---

## ❌ Error Responses

All errors follow this format:

```json
{
  "timestamp": "2025-12-07T15:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Host not found with ID: 999",
  "path": "/api/hosts/999"
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created |
| 204 | No Content - Request successful, no response body |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Missing or invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |
| 500 | Internal Server Error |

---

## 📌 Rate Limiting

- **Limit**: 100 requests per minute per user
- **Headers**:
  - `X-RateLimit-Limit`: Total allowed requests
  - `X-RateLimit-Remaining`: Remaining requests
  - `X-RateLimit-Reset`: Time when limit resets

---

## 🔗 Interactive Documentation

For interactive API testing, use Swagger UI:

**URL**: http://localhost:8080/api/swagger-ui.html

---

## 📝 Examples

### Complete Monitoring Setup Example

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Save the token
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 2. Create a host
curl -X POST http://localhost:8080/api/hosts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "web-server",
    "ipAddress": "192.168.1.10",
    "os": "Ubuntu 22.04"
  }'

# 3. Create a service
curl -X POST http://localhost:8080/api/services \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "hostId": 1,
    "name": "NGINX",
    "type": "HTTP",
    "checkConfig": {
      "url": "http://192.168.1.10:80",
      "expectedStatus": 200
    },
    "checkInterval": 60
  }'

# 4. Create an alert rule
curl -X POST http://localhost:8080/api/alert-rules \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "High CPU",
    "hostId": 1,
    "condition": "cpu_usage > 80",
    "threshold": 80.0,
    "severity": "WARNING"
  }'

# 5. Query metrics
curl "http://localhost:8080/api/metrics?hostId=1&metricName=cpu_usage&from=2025-12-07T00:00:00Z" \
  -H "Authorization: Bearer $TOKEN"
```

---

**For detailed API testing, use the Swagger UI at: http://localhost:8080/api/swagger-ui.html**
