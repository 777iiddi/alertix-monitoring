# ⚡ Alertix Monitoring - Quick Reference

## 🚀 5-Minute Quick Start

### 1. Start Infrastructure
```bash
docker-compose up -d postgres kafka
```

### 2. Run Backend
```bash
cd backend
./mvnw spring-boot:run
```

### 3. Run Frontend
```bash
cd frontend
npm install && npm start
```

### 4. Access
- **Frontend**: http://localhost:4200
- **Backend**: http://localhost:8080/api
- **Swagger**: http://localhost:8080/api/swagger-ui.html
- **Login**: admin / admin123

---

## 📁 Key Files

| File | Purpose |
|------|---------|
| `docs/ARCHITECTURE.md` | System design & architecture |
| `docs/ROADMAP.md` | 9-sprint implementation plan |
| `docs/PROJECT_SUMMARY.md` | Complete getting started guide |
| `docs/API_REFERENCE.md` | API endpoints documentation |
| `docs/FILE_TREE.md` | Project structure & status |
| `Makefile` | Development commands |
| `docker-compose.yml` | Full stack deployment |

---

## 🛠️ Essential Commands

```bash
# Using Makefile
make help                 # Show all commands
make dev-start            # Start development environment
make docker-start         # Start with Docker
make test-all             # Run all tests
make db-migrate           # Run database migrations
make kafka-topics         # List Kafka topics

# Backend
cd backend
./mvnw spring-boot:run    # Run backend
./mvnw test               # Run tests
./mvnw clean package      # Build JAR

# Frontend
cd frontend
npm start                 # Run dev server
npm test                  # Run tests
npm run build             # Build for production

# Docker
docker-compose up -d      # Start all services
docker-compose logs -f    # View logs
docker-compose down       # Stop all services
```

---

## 🌐 Important URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:4200 | admin / admin123 |
| Backend API | http://localhost:8080/api | - |
| Swagger UI | http://localhost:8080/api/swagger-ui.html | - |
| Kafka UI | http://localhost:8090 | - |
| Prometheus | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin / admin |
| H2 Console (dev) | http://localhost:8080/api/h2-console | JDBC: `jdbc:h2:mem:alertix` |

---

## 📊 Project Status

### ✅ Implemented (Sprint 0)
- Backend architecture & configuration
- Database schema (PostgreSQL + TimescaleDB)
- JWT authentication & security
- Host management API (full CRUD)
- Entity models (Host, Service, Alert, Metric, User)
- Repositories & services
- Docker Compose setup
- Kafka configuration
- Makefile (50+ commands)
- CI/CD pipeline (GitHub Actions)
- Comprehensive documentation

### ⏳ Next (Sprint 1)
- Service management API completion
- Frontend core components
- Host management UI
- NgRx state management
- Basic dashboard

---

## 🗂️ Project Structure

```
alertix-monitoring/
├── backend/              # Spring Boot 3
│   ├── src/main/java/com/alertix/
│   │   ├── inventory/    # ✅ Host/Service management
│   │   ├── alerts/       # ✅ Entities only
│   │   ├── metrics/      # ✅ Entities only
│   │   ├── auth/         # ✅ Entities only
│   │   ├── config/       # ✅ Complete
│   │   └── security/     # ✅ JWT complete
│   └── src/main/resources/
│       └── db/migration/ # ✅ V1 schema complete
├── frontend/             # Angular 18
│   └── src/app/
│       ├── core/         # ⏳ Pending
│       └── features/     # ⏳ Pending
├── docs/                 # ✅ 5 comprehensive docs
└── deploy/               # ⏳ Kubernetes pending
```

---

## 🔑 Default Credentials

⚠️ **Change these in production!**

| Service | Username | Password |
|---------|----------|----------|
| Application | admin | admin123 |
| PostgreSQL | alertix | alertix123 |
| Grafana | admin | admin |
| H2 Console | sa | (empty) |

---

## 🚨 Troubleshooting

### Backend won't start
```bash
docker ps | grep postgres    # Check if DB is running
make db-migrate              # Run migrations
./mvnw clean spring-boot:run -X  # Verbose logs
```

### Port already in use
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Or change port in application.yml
server.port: 8081
```

### Database connection failed
```bash
docker-compose up -d postgres  # Start PostgreSQL
docker logs alertix-postgres   # Check logs
make db-psql                   # Connect to DB
```

---

## 📋 API Quick Reference

### Authentication
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Hosts
```bash
# Get all hosts
curl http://localhost:8080/api/hosts \
  -H "Authorization: Bearer $TOKEN"

# Create host
curl -X POST http://localhost:8080/api/hosts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"server-01","ipAddress":"192.168.1.10"}'
```

---

## 📚 Documentation Links

- **Architecture**: `docs/ARCHITECTURE.md` - System design
- **Roadmap**: `docs/ROADMAP.md` - Sprint plan
- **Summary**: `docs/PROJECT_SUMMARY.md` - Full guide
- **API**: `docs/API_REFERENCE.md` - API docs
- **File Tree**: `docs/FILE_TREE.md` - Project structure

---

## 🎯 Next Steps

1. **Complete Sprint 0**:
   - Implement Service CRUD operations
   - Create basic frontend components
   
2. **Start Sprint 1** (Inventory UI):
   - Build host list/form components
   - Implement service management UI
   - Add NgRx store

3. **Sprint 2** (Scheduler):
   - Implement check executors (Ping, HTTP, TCP)
   - Add scheduling logic
   - Kafka metrics pipeline

---

## 💡 Tips

- Use `make help` to see all available commands
- Check `docs/PROJECT_SUMMARY.md` for complete guide
- Access Swagger UI for interactive API testing
- Use `docker-compose logs -f backend` for real-time logs
- Run `make health-check` to verify all services

---

## 📞 Need Help?

1. Check documentation in `docs/` folder
2. View logs: `make docker-logs`
3. Verify health: `make health-check`
4. Reset environment: `make dev-reset`

---

**🎉 You're ready to build a production-grade monitoring platform!**

**Quick Start**: `make init` → Wait 2 minutes → Access http://localhost:4200
