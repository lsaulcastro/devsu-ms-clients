-- ============================================================================
-- Customer Microservice - Initial Schema
-- ============================================================================
-- Generated for: ms-clients
-- Strategy: JOINED inheritance (Person <- Customer)
-- ============================================================================

CREATE TABLE persons (
                         id              BIGSERIAL    PRIMARY KEY,
                         name            VARCHAR(100) NOT NULL,
                         gender          VARCHAR(20)  NOT NULL,
                         age             INTEGER      NOT NULL CHECK (age >= 0 AND age <= 150),
                         identification  VARCHAR(20)  NOT NULL UNIQUE,
                         address         VARCHAR(200) NOT NULL,
                         phone           VARCHAR(20)  NOT NULL
);

CREATE INDEX idx_persons_identification ON persons (identification);

CREATE TABLE customers (
                           id           BIGINT       PRIMARY KEY REFERENCES persons (id) ON DELETE CASCADE,
                           customer_id  VARCHAR(30)  NOT NULL UNIQUE,
                           password     VARCHAR(100) NOT NULL,
                           active       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_customers_customer_id ON customers (customer_id);
CREATE INDEX idx_customers_active      ON customers (active);

CREATE TABLE outbox_events (
                               id              BIGSERIAL    PRIMARY KEY,
                               aggregate_type  VARCHAR(50)  NOT NULL,
                               aggregate_id    VARCHAR(50)  NOT NULL,
                               event_type      VARCHAR(50)  NOT NULL,
                               payload         TEXT         NOT NULL,
                               status          VARCHAR(20)  NOT NULL,
                               created_at      TIMESTAMP    NOT NULL,
                               published_at    TIMESTAMP,
                               retry_count     INTEGER      NOT NULL DEFAULT 0,
                               last_error      TEXT
);

CREATE INDEX idx_outbox_status_created ON outbox_events (status, created_at);
CREATE INDEX idx_outbox_aggregate      ON outbox_events (aggregate_type, aggregate_id);