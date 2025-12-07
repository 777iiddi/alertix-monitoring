# ============================================================================
# Alertix Monitoring - Makefile
# Convenient commands for development and deployment
# ============================================================================

.PHONY: help build start stop logs clean test deploy

# Default target
.DEFAULT_GOAL := help

# Colors for help output
BLUE := \033[0;34m
NC := \033[0m # No Color

## help: Display this help message
help:
	@echo "$(BLUE)Alertix Monitoring - Available Commands:$(NC)"
	@echo ""
	@grep -E '^##' Makefile | sed 's/##/  /' | column -t -s ':'

# ============================================================================
# Development
# ============================================================================

## dev-start: Start all services in development mode
dev-start:
	docker-compose up -d postgres kafka zookeeper redis
	@echo "Starting backend..."
	cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev &
	@echo "Starting frontend..."
	cd frontend && npm start &

## dev-stop: Stop development services
dev-stop:
	docker-compose down
	@pkill -f "spring-boot:run" || true
	@pkill -f "ng serve" || true

## dev-reset: Reset development environment (clean DB, Kafka, etc.)
dev-reset: dev-stop
	docker-compose down -v
	docker volume prune -f

# ============================================================================
# Docker
# ============================================================================

## docker-build: Build all Docker images
docker-build:
	docker-compose build

## docker-start: Start all services with Docker Compose
docker-start:
	docker-compose up -d

## docker-stop: Stop all Docker services
docker-stop:
	docker-compose down

## docker-restart: Restart all Docker services
docker-restart: docker-stop docker-start

## docker-logs: Show logs from all services
docker-logs:
	docker-compose logs -f

## docker-logs-backend: Show backend logs
docker-logs-backend:
	docker-compose logs -f backend

## docker-logs-frontend: Show frontend logs
docker-logs-frontend:
	docker-compose logs -f frontend

## docker-clean: Remove all containers, volumes, and images
docker-clean:
	docker-compose down -v --rmi all
	docker system prune -f

# ============================================================================
# Backend
# ============================================================================

## backend-build: Build backend JAR
backend-build:
	cd backend && ./mvnw clean package -DskipTests

## backend-test: Run backend tests
backend-test:
	cd backend && ./mvnw test

## backend-test-coverage: Run tests with coverage report
backend-test-coverage:
	cd backend && ./mvnw clean verify
	@echo "Coverage report: backend/target/site/jacoco/index.html"

## backend-run: Run backend locally
backend-run:
	cd backend && ./mvnw spring-boot:run

## backend-run-prod: Run backend with production profile
backend-run-prod:
	cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

## backend-lint: Run checkstyle on backend code
backend-lint:
	cd backend && ./mvnw checkstyle:check

# ============================================================================
# Frontend
# ============================================================================

## frontend-install: Install frontend dependencies
frontend-install:
	cd frontend && npm install

## frontend-build: Build frontend for production
frontend-build:
	cd frontend && npm run build --prod

## frontend-test: Run frontend tests
frontend-test:
	cd frontend && npm test

## frontend-lint: Run linting on frontend code
frontend-lint:
	cd frontend && npm run lint

## frontend-serve: Serve frontend in development mode
frontend-serve:
	cd frontend && npm start

# ============================================================================
# Database
# ============================================================================

## db-migrate: Run database migrations
db-migrate:
	cd backend && ./mvnw flyway:migrate

## db-clean: Clean database (drop all tables)
db-clean:
	cd backend && ./mvnw flyway:clean

## db-info: Show migration info
db-info:
	cd backend && ./mvnw flyway:info

## db-psql: Connect to PostgreSQL with psql
db-psql:
	docker exec -it alertix-postgres psql -U alertix -d alertix

## db-backup: Backup database
db-backup:
	docker exec alertix-postgres pg_dump -U alertix alertix > backup_$$(date +%Y%m%d_%H%M%S).sql
	@echo "Backup created: backup_$$(date +%Y%m%d_%H%M%S).sql"

## db-restore: Restore database from backup (usage: make db-restore FILE=backup.sql)
db-restore:
	@if [ -z "$(FILE)" ]; then echo "Usage: make db-restore FILE=backup.sql"; exit 1; fi
	cat $(FILE) | docker exec -i alertix-postgres psql -U alertix alertix

# ============================================================================
# Kafka
# ============================================================================

## kafka-topics: List Kafka topics
kafka-topics:
	docker exec alertix-kafka kafka-topics --bootstrap-server localhost:9092 --list

## kafka-create-topics: Create required Kafka topics
kafka-create-topics:
	docker exec alertix-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic metrics.raw --partitions 6 --replication-factor 1 --if-not-exists
	docker exec alertix-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic metrics.agg --partitions 3 --replication-factor 1 --if-not-exists
	docker exec alertix-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic alerts --partitions 3 --replication-factor 1 --if-not-exists
	docker exec alertix-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic notifications --partitions 3 --replication-factor 1 --if-not-exists

## kafka-consume-metrics: Consume metrics.raw topic
kafka-consume-metrics:
	docker exec alertix-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic metrics.raw --from-beginning

## kafka-consume-alerts: Consume alerts topic
kafka-consume-alerts:
	docker exec alertix-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic alerts --from-beginning

# ============================================================================
# Testing
# ============================================================================

## test-all: Run all tests (backend + frontend)
test-all: backend-test frontend-test

## test-integration: Run integration tests
test-integration:
	cd backend && ./mvnw verify -P integration-tests

## test-e2e: Run end-to-end tests
test-e2e:
	cd tests/e2e && npm run test

# ============================================================================
# Code Quality
# ============================================================================

## lint-all: Run linting on all code
lint-all: backend-lint frontend-lint

## format-backend: Format backend code
format-backend:
	cd backend && ./mvnw spotless:apply

## format-frontend: Format frontend code
format-frontend:
	cd frontend && npm run format

# ============================================================================
# Deployment
# ============================================================================

## deploy-local: Deploy to local Docker environment
deploy-local: docker-build docker-start
	@echo "Waiting for services to be healthy..."
	@sleep 30
	@echo "Alertix Monitoring is now running!"
	@echo "Frontend: http://localhost:4200"
	@echo "Backend: http://localhost:8080/api"
	@echo "Swagger UI: http://localhost:8080/api/swagger-ui.html"

## deploy-prod: Build production images
deploy-prod:
	docker-compose -f docker-compose.prod.yml build
	docker-compose -f docker-compose.prod.yml up -d

## health-check: Check health of all services
health-check:
	@echo "Checking service health..."
	@curl -f http://localhost:8080/api/actuator/health || echo "❌ Backend is down"
	@curl -f http://localhost:4200 || echo "❌ Frontend is down"
	@curl -f http://localhost:9090/-/healthy || echo "❌ Prometheus is down"
	@curl -f http://localhost:3000/api/health || echo "❌ Grafana is down"

# ============================================================================
# Monitoring
# ============================================================================

## monitoring-start: Start monitoring stack (Prometheus + Grafana)
monitoring-start:
	docker-compose up -d prometheus grafana

## monitoring-stop: Stop monitoring stack
monitoring-stop:
	docker-compose stop prometheus grafana

# ============================================================================
# Utilities
# ============================================================================

## clean-all: Clean all build artifacts and dependencies
clean-all:
	cd backend && ./mvnw clean
	cd frontend && rm -rf node_modules dist
	rm -rf logs/*.log

## install-all: Install all dependencies (backend + frontend)
install-all:
	cd backend && ./mvnw dependency:resolve
	cd frontend && npm install

## version: Show version information
version:
	@echo "Alertix Monitoring v0.0.1-SNAPSHOT"
	@cd backend && ./mvnw --version
	@cd frontend && npm --version

## init: Initialize project (install dependencies, create topics, migrate DB)
init: install-all docker-start kafka-create-topics
	@echo "Waiting for database to be ready..."
	@sleep 10
	$(MAKE) db-migrate
	@echo "✅ Initialization complete!"

## status: Show status of all services
status:
	@echo "Docker Services:"
	@docker-compose ps
	@echo ""
	@echo "Kafka Topics:"
	@$(MAKE) kafka-topics

# ============================================================================
# Agent
# ============================================================================

## agent-build-go: Build Go agent
agent-build-go:
	cd agents/go-agent && go build -o alertix-agent

## agent-run-go: Run Go agent
agent-run-go:
	cd agents/go-agent && ./alertix-agent

## agent-build-rust: Build Rust agent
agent-build-rust:
	cd agents/rust-agent && cargo build --release

## agent-run-rust: Run Rust agent
agent-run-rust:
	cd agents/rust-agent && cargo run

# ============================================================================
# CI/CD
# ============================================================================

## ci-build: Build for CI pipeline
ci-build: backend-build frontend-build

## ci-test: Run tests for CI pipeline
ci-test: test-all lint-all

## ci-package: Package application for deployment
ci-package:
	@echo "Creating deployment package..."
	tar -czf alertix-deployment.tar.gz docker-compose.yml deploy/ README.md

# ============================================================================
# Documentation
# ============================================================================

## docs-serve: Serve documentation locally
docs-serve:
	cd docs && python3 -m http.server 8000

## docs-api: Generate API documentation
docs-api:
	cd backend && ./mvnw springdoc-openapi:generate
	@echo "API docs generated at backend/target/openapi.json"
