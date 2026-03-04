CREATE TABLE machine (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    machine_code TEXT UNIQUE NOT NULL,
    machine_name TEXT NOT NULL,
    category TEXT,
    manufacturer TEXT,
    rental_price_per_day REAL DEFAULT 0,
    description TEXT,
    image TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_machine_name ON machine(machine_name);