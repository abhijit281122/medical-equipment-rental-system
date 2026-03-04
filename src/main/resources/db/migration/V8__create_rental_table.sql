CREATE TABLE rental (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    machine_unit_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,

    created_by INTEGER,
    delivery_person_id INTEGER,
    rent_collector_id INTEGER,

    rent_start_date DATE NOT NULL,
    expected_return_date DATE,
    actual_return_date DATE,

    rental_price_per_day REAL,
    total_amount REAL DEFAULT 0,
    paid_amount REAL DEFAULT 0,
    due_amount REAL DEFAULT 0,

    status TEXT DEFAULT 'Active', -- Active, Returned, Overdue, Cancelled

    remarks TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (machine_unit_id) REFERENCES machine_unit(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (created_by) REFERENCES employee(id),
    FOREIGN KEY (delivery_person_id) REFERENCES employee(id),
    FOREIGN KEY (rent_collector_id) REFERENCES employee(id)
);

CREATE INDEX idx_rental_machine_unit ON rental(machine_unit_id);
CREATE INDEX idx_rental_customer ON rental(customer_id);
CREATE INDEX idx_rental_status ON rental(status);
CREATE INDEX idx_rental_start_date ON rental(rent_start_date);