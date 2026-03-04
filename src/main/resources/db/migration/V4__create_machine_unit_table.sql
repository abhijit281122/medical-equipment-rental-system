CREATE TABLE machine_unit (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    machine_id INTEGER NOT NULL,
    serial_number TEXT UNIQUE NOT NULL,
    lot_number TEXT,
    status TEXT DEFAULT 'Available', -- Available, Rented, Maintenance
    condition TEXT DEFAULT 'Good',
    purchase_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (machine_id)
        REFERENCES machine(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_machine_unit_machine_id ON machine_unit(machine_id);
CREATE INDEX idx_machine_unit_status ON machine_unit(status);