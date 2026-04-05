ALTER TABLE parking_slot RENAME TO parking_session;
ALTER INDEX idx_parking_slot_car RENAME TO idx_parking_session_car;
ALTER INDEX idx_parking_slot_parking RENAME TO idx_parking_session_parking;
