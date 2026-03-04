CREATE TABLE customer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    phone TEXT,
    address TEXT,
    location TEXT,
    reference_by INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (reference_by)
        REFERENCES employee(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_customer_reference ON customer(reference_by);