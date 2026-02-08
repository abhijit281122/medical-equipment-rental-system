CREATE TABLE IF NOT EXISTS employee (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_code TEXT NOT NULL UNIQUE,     -- EMP001, EMP002
    full_name TEXT NOT NULL,
    role TEXT NOT NULL,                     -- Admin, Technician, Delivery, Accountant
    phone TEXT,
    email TEXT,
    address TEXT,
    is_active INTEGER DEFAULT 1,           -- 1 = Active, 0 = Inactive

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_at DATETIME,
    updated_by TEXT
);

