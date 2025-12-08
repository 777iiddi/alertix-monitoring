🎯 Sprint Goal

“Transform the project from architectural setup into a first working monitoring pipeline.”

Meaning:

✅ Agents send metrics
✅ Backend receives & stores data
✅ Frontend displays real dashboards
✅ Basic alerts work

✅ Main Objectives
1️⃣ Finish monitoring agents

Goal:

Make the Go agent fully functional

Tasks:

Collect system metrics:

CPU usage

Memory usage

Disk usage

Uptime

Send data to backend via REST API or Kafka

Add host identification + heartbeat

Deliverable:

✅ First real metrics arriving from an agent into the backend

2️⃣ Backend – Metric ingestion & storage

Goal:

Enable the backend to receive and persist metrics

Tasks:

Create REST endpoints:

POST /metrics

POST /heartbeat

Implement JPA entities:

Host

Metric

Alert

Persist data into PostgreSQL + TimescaleDB

Add basic metric aggregation services

Deliverable:

✅ Metrics stored in the database & visible in SQL queries

3️⃣ Basic alert engine

Goal:

Trigger alerts when metrics cross thresholds

Tasks:

Add rule definitions:

CPU > X %

Memory > Y %

Implement evaluation service

Store alerts in DB

Simple notification logging (console or email mock)

Deliverable:

✅ Alerts generated automatically

4️⃣ Frontend dashboards

Goal:

Display real data

Tasks:

Create dashboards:

CPU / Memory charts per host

List of alerts

Connect frontend to backend APIs

Implement real-time updates (SSE or polling)

Deliverable:

✅ UI showing live metrics & alerts

5️⃣ Observability & system validation

Goal:

Validate that the platform monitors itself

Tasks:

Prometheus scrapes backend metrics

Grafana dashboards for system health

Add actuator endpoints to backend

Deliverable:

✅ Working monitoring dashboards of Alertix itself
