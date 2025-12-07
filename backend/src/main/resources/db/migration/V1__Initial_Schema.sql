-- ============================================================================
-- Alertix Monitoring - Initial Database Schema
-- Version: V1__Initial_Schema.sql
-- Description: Creates the base tables for the monitoring platform
-- ============================================================================

-- ============================================================================
-- USERS AND AUTHENTICATION
-- ============================================================================

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'READER' CHECK (role IN ('ADMIN', 'OPERATOR', 'READER')),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refresh tokens table
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

-- ============================================================================
-- INVENTORY
-- ============================================================================

-- Hosts table
CREATE TABLE hosts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    ip_address INET NOT NULL,
    status VARCHAR(50) DEFAULT 'UNKNOWN' CHECK (status IN ('UP', 'DOWN', 'UNKNOWN', 'UNREACHABLE')),
    os VARCHAR(100),
    description TEXT,
    tags JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hosts_status ON hosts(status);
CREATE INDEX idx_hosts_name ON hosts(name);

-- Services table
CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    host_id BIGINT NOT NULL REFERENCES hosts(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('PING', 'HTTP', 'HTTPS', 'TCP', 'SSH', 'CUSTOM')),
    check_config JSONB,
    check_interval INTEGER DEFAULT 60,
    timeout INTEGER DEFAULT 30,
    retries INTEGER DEFAULT 3,
    status VARCHAR(50) DEFAULT 'UNKNOWN' CHECK (status IN ('OK', 'WARNING', 'CRITICAL', 'UNKNOWN')),
    last_check_time TIMESTAMP,
    next_check_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(host_id, name)
);

CREATE INDEX idx_services_host_id ON services(host_id);
CREATE INDEX idx_services_status ON services(status);
CREATE INDEX idx_services_next_check_time ON services(next_check_time);

-- ============================================================================
-- METRICS
-- ============================================================================

-- Metrics table (will be converted to TimescaleDB hypertable)
CREATE TABLE metrics (
    time TIMESTAMPTZ NOT NULL,
    host_id BIGINT REFERENCES hosts(id) ON DELETE CASCADE,
    service_id BIGINT REFERENCES services(id) ON DELETE CASCADE,
    metric_name VARCHAR(100) NOT NULL,
    value DOUBLE PRECISION,
    unit VARCHAR(50),
    tags JSONB,
    PRIMARY KEY (time, host_id, metric_name)
);

-- Note: In production with TimescaleDB, run:
-- SELECT create_hypertable('metrics', 'time');

CREATE INDEX idx_metrics_host_time ON metrics (host_id, time DESC);
CREATE INDEX idx_metrics_service_time ON metrics (service_id, time DESC);
CREATE INDEX idx_metrics_name ON metrics (metric_name);

-- ============================================================================
-- ALERT RULES
-- ============================================================================

-- Alert rules table
CREATE TABLE alert_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    host_id BIGINT REFERENCES hosts(id) ON DELETE CASCADE,
    service_id BIGINT REFERENCES services(id) ON DELETE CASCADE,
    condition VARCHAR(500) NOT NULL,
    threshold DOUBLE PRECISION,
    duration INTEGER DEFAULT 60,
    severity VARCHAR(50) DEFAULT 'WARNING' CHECK (severity IN ('INFO', 'WARNING', 'CRITICAL')),
    enabled BOOLEAN DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_alert_rules_host_id ON alert_rules(host_id);
CREATE INDEX idx_alert_rules_service_id ON alert_rules(service_id);
CREATE INDEX idx_alert_rules_enabled ON alert_rules(enabled);

-- ============================================================================
-- ALERTS
-- ============================================================================

-- Alerts table
CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL REFERENCES alert_rules(id) ON DELETE CASCADE,
    host_id BIGINT REFERENCES hosts(id) ON DELETE CASCADE,
    service_id BIGINT REFERENCES services(id) ON DELETE CASCADE,
    status VARCHAR(50) DEFAULT 'FIRING' CHECK (status IN ('FIRING', 'RESOLVED', 'ACKNOWLEDGED')),
    severity VARCHAR(50) CHECK (severity IN ('INFO', 'WARNING', 'CRITICAL')),
    message TEXT NOT NULL,
    value DOUBLE PRECISION,
    triggered_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    acknowledged_at TIMESTAMP,
    acknowledged_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_alerts_rule_id ON alerts(rule_id);
CREATE INDEX idx_alerts_status ON alerts(status);
CREATE INDEX idx_alerts_severity ON alerts(severity);
CREATE INDEX idx_alerts_triggered_at ON alerts(triggered_at DESC);

-- ============================================================================
-- NOTIFICATION CHANNELS
-- ============================================================================

-- Notification channels table
CREATE TABLE notification_channels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL CHECK (type IN ('EMAIL', 'WEBHOOK', 'SLACK', 'PAGERDUTY', 'SMS')),
    config JSONB NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_channels_enabled ON notification_channels(enabled);

-- Alert rule to notification channel mapping (many-to-many)
CREATE TABLE alert_rule_notification_channels (
    alert_rule_id BIGINT NOT NULL REFERENCES alert_rules(id) ON DELETE CASCADE,
    notification_channel_id BIGINT NOT NULL REFERENCES notification_channels(id) ON DELETE CASCADE,
    PRIMARY KEY (alert_rule_id, notification_channel_id)
);

-- ============================================================================
-- NOTIFICATION LOG
-- ============================================================================

-- Notification log table
CREATE TABLE notification_log (
    id BIGSERIAL PRIMARY KEY,
    alert_id BIGINT NOT NULL REFERENCES alerts(id) ON DELETE CASCADE,
    channel_id BIGINT NOT NULL REFERENCES notification_channels(id) ON DELETE CASCADE,
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    attempts INTEGER DEFAULT 0,
    error_message TEXT,
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_log_alert_id ON notification_log(alert_id);
CREATE INDEX idx_notification_log_status ON notification_log(status);

-- ============================================================================
-- AGENTS
-- ============================================================================

-- Monitoring agents table
CREATE TABLE agents (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    host_id BIGINT REFERENCES hosts(id) ON DELETE SET NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    version VARCHAR(50),
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'ERROR')),
    last_seen_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agents_status ON agents(status);
CREATE INDEX idx_agents_api_key ON agents(api_key);

-- ============================================================================
-- AUDIT LOG
-- ============================================================================

-- Audit log table (for tracking user actions)
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    username VARCHAR(100),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    details JSONB,
    ip_address INET,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at DESC);

-- ============================================================================
-- INITIAL DATA
-- ============================================================================

-- Insert default admin user (password: admin123)
-- Password hash for 'admin123' with BCrypt cost 12
INSERT INTO users (username, email, password_hash, role) VALUES
('admin', 'admin@alertix.local', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKyKq4j9Gy', 'ADMIN');

-- Insert default notification channels
INSERT INTO notification_channels (name, type, config, enabled) VALUES
('Default Email', 'EMAIL', '{"to": "alerts@alertix.local", "from": "noreply@alertix.local"}', true),
('Webhook Example', 'WEBHOOK', '{"url": "http://localhost:3000/webhook", "method": "POST"}', false);

-- ============================================================================
-- FUNCTIONS AND TRIGGERS
-- ============================================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply triggers to all tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_hosts_updated_at BEFORE UPDATE ON hosts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_services_updated_at BEFORE UPDATE ON services
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_alert_rules_updated_at BEFORE UPDATE ON alert_rules
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notification_channels_updated_at BEFORE UPDATE ON notification_channels
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_agents_updated_at BEFORE UPDATE ON agents
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- VIEWS
-- ============================================================================

-- View: Service health summary
CREATE VIEW service_health_summary AS
SELECT 
    h.id as host_id,
    h.name as host_name,
    COUNT(s.id) as total_services,
    COUNT(CASE WHEN s.status = 'OK' THEN 1 END) as ok_services,
    COUNT(CASE WHEN s.status = 'WARNING' THEN 1 END) as warning_services,
    COUNT(CASE WHEN s.status = 'CRITICAL' THEN 1 END) as critical_services,
    COUNT(CASE WHEN s.status = 'UNKNOWN' THEN 1 END) as unknown_services
FROM hosts h
LEFT JOIN services s ON h.id = s.host_id
GROUP BY h.id, h.name;

-- View: Active alerts summary
CREATE VIEW active_alerts_summary AS
SELECT 
    severity,
    COUNT(*) as count,
    MIN(triggered_at) as oldest_alert,
    MAX(triggered_at) as newest_alert
FROM alerts
WHERE status = 'FIRING'
GROUP BY severity;

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE users IS 'User accounts for authentication and authorization';
COMMENT ON TABLE hosts IS 'Monitored hosts/servers';
COMMENT ON TABLE services IS 'Services running on hosts that are being monitored';
COMMENT ON TABLE metrics IS 'Time-series metrics data (CPU, memory, etc.)';
COMMENT ON TABLE alert_rules IS 'Alert rule definitions';
COMMENT ON TABLE alerts IS 'Alert instances when rules are triggered';
COMMENT ON TABLE notification_channels IS 'Notification channel configurations';
COMMENT ON TABLE notification_log IS 'Log of sent notifications';
COMMENT ON TABLE agents IS 'Monitoring agents installed on hosts';
