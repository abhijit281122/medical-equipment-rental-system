-- equipment table
CREATE TABLE equipment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    equipment_id TEXT UNIQUE NOT NULL,
    equipment_name TEXT NOT NULL,
    category TEXT,
    manufacturer TEXT,
    serial_number TEXT,
    rental_price REAL DEFAULT 0,
    condition TEXT DEFAULT 'Available',
    image TEXT,
    date_added DATE
);