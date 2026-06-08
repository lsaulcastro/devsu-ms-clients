-- ============================================================================
-- Customer Microservice - Seed Data
-- ============================================================================
-- Inserts the 3 customers described in the test specification use case:
--   - Jose Lema      (password: 1234)
--   - Marianela Montalvo (password: 5678)
--   - Juan Osorio    (password: 1245)
--
-- Passwords are stored as BCrypt hashes (strength 10).
-- ============================================================================

-- Person 1: Jose Lema
INSERT INTO persons (name, gender, age, identification, address, phone)
VALUES ('Jose Lema', 'MALE', 35, 'JL-001', 'Otavalo sn y principal', '098254785');

INSERT INTO customers (id, customer_id, password, active)
VALUES (
           (SELECT id FROM persons WHERE identification = 'JL-001'),
           'JLEMA',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           TRUE
       );

-- Person 2: Marianela Montalvo
INSERT INTO persons (name, gender, age, identification, address, phone)
VALUES ('Marianela Montalvo', 'FEMALE', 32, 'MM-002', 'Amazonas y NNUU', '097548965');

INSERT INTO customers (id, customer_id, password, active)
VALUES (
           (SELECT id FROM persons WHERE identification = 'MM-002'),
           'MMONTALVO',
           '$2a$10$qWnODfm/TVCwQuRZD3oxJupBQyrhpDvksUOXDD3HhxIPHzlnAm9rO',
           TRUE
       );

-- Person 3: Juan Osorio
INSERT INTO persons (name, gender, age, identification, address, phone)
VALUES ('Juan Osorio', 'MALE', 28, 'JO-003', '13 junio y Equinoccial', '098874587');

INSERT INTO customers (id, customer_id, password, active)
VALUES (
           (SELECT id FROM persons WHERE identification = 'JO-003'),
           'JOSORIO',
           '$2a$10$F4FZh.tQpQDmKwI8mtVB6OPwR3LjEPCgIxYAyIcyf5fT5VL6jjuPq',
           TRUE
       );