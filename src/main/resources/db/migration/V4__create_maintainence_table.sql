-- maintenance table
CREATE TABLE maintenance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    equipment_id TEXT,
    issue TEXT,
    last_service_date DATE,
    next_service_due DATE,
    technician TEXT,
    notes TEXT
);