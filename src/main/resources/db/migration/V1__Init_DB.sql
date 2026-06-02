CREATE TABLE access_zone (
    id BIGSERIAL PRIMARY KEY,
    zone_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE client_zone (
    client_id BIGINT NOT NULL,
    zone_id BIGINT NOT NULL,
    PRIMARY KEY (client_id, zone_id),
    CONSTRAINT fk_client_zone_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    CONSTRAINT fk_client_zone_zone FOREIGN KEY (zone_id) REFERENCES access_zone(id) ON DELETE CASCADE
);

CREATE TABLE access_cards (
    id BIGSERIAL PRIMARY KEY,
    rfid_token VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    client_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_access_cards_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

CREATE TABLE access_logs (
    id BIGSERIAL PRIMARY KEY,
    direction VARCHAR(255) NOT NULL,
    time_stamp TIMESTAMP NOT NULL,
    client_id BIGINT NOT NULL,
    zone_id BIGINT NOT NULL,
    CONSTRAINT fk_access_logs_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_access_logs_zone FOREIGN KEY (zone_id) REFERENCES access_zone(id)
);

