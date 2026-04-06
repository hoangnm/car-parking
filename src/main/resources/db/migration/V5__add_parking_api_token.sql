CREATE TABLE parking_api_token (
    id         SERIAL PRIMARY KEY,
    parking_id INTEGER      NOT NULL UNIQUE REFERENCES parking (id),
    token      VARCHAR(255) NOT NULL UNIQUE
);
