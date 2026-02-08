CREATE TABLE rental (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    equipment_id TEXT NOT NULL,
    patient_name TEXT NOT NULL,
    challan_no TEXT,
    staff_separation TEXT,
    rent_received_date DATE,
    rent_collection_date DATE,
    rent_end_date DATE,
    status TEXT DEFAULT 'Active',  -- Active, Due, Closed
    collected_by TEXT,

    FOREIGN KEY (equipment_id)
        REFERENCES equipment (equipment_id)
        ON DELETE CASCADE
);
