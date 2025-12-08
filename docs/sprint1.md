This folder is the main server of Alertix.

It exposes APIs, processes metrics, runs alerts, and sends notifications.

🔹 src/main/java/com/alertix/

Each subpackage corresponds to a major business component:

✅ inventory/

Hosts & devices management

Register machines

Organize systems being monitored

✅ scheduler/

Job scheduling service

Periodically triggers metric collection

Launches health checks

✅ metrics/

Metrics processor

Receives metrics from agents

Writes them to TimescaleDB

Prepares data for dashboards

✅ alerts/

Alert engine

Evaluates thresholds

Generates alerts when rules are violated

✅ notifications/

Notification system

Sends alerts by:

Email

Slack

Webhooks

PagerDuty

✅ auth/

Security & authentication

JWT login

User roles (Admin / Operator / Reader)

✅ common/

Shared backend utilities

DTOs

Entity mappers

Helpers and constants

🔹 src/main/resources/
✅ db/migration/

Flyway database scripts

Versioned SQL schema updates

Auto-runs on startup

✅ application.yml

Spring Boot configuration

Database connection details

Kafka brokers

Security secrets

Server settings

✅ pom.xml

Maven configuration

Defines all backend dependencies:

Spring Boot

Kafka

JPA

JWT

Flyway

Swagger

Tests

📡 agents/ — Monitoring Agents

This folder contains the programs that actually run on monitored machines.

✅ go-agent/

Go implementation

Collects system stats:

CPU

RAM

Disk

Network

Sends metrics & heartbeats to backend

✅ rust-agent/

Rust implementation (optional / future)

Same goals as Go agent

More low-level, memory-safe, ultra-performant

💬 These agents are the “sensors” of your monitoring system.
