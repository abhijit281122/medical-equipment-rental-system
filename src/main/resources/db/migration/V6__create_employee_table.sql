CREATE TABLE employee (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_code TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    phone TEXT,
    email TEXT,
    address TEXT,
    joining_date DATE,
    salary REAL DEFAULT 0,
    role_id INTEGER NOT NULL,
    status TEXT DEFAULT 'Active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (role_id)
        REFERENCES employee_role(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_employee_role ON employee(role_id);
CREATE INDEX idx_employee_status ON employee(status);