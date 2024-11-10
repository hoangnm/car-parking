DROP TABLE IF EXISTS parking_slot;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS parking;

CREATE TABLE IF NOT EXISTS car (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    color VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS parking (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    location VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS parking_slot (
    id SERIAL PRIMARY KEY,
    car_id INTEGER REFERENCES car(id),
    parking_id INTEGER REFERENCES parking(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES car(id),
    CONSTRAINT fk_parking FOREIGN KEY (parking_id) REFERENCES parking(id)
);

CREATE INDEX IF NOT EXISTS idx_parking_slot_car ON parking_slot(car_id);
CREATE INDEX IF NOT EXISTS idx_parking_slot_parking ON parking_slot(parking_id);
CREATE INDEX IF NOT EXISTS idx_car_license_plate ON car(license_plate);
