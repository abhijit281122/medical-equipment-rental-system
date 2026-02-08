INSERT INTO rental (
    equipment_id, patient_name, challan_no,
    staff_separation, rent_received_date,
    rent_collection_date, rent_end_date,
    status, collected_by
)
VALUES
    ('EQ001', 'Rohan Patel', 'DC-001',
     'Nurse Asha', '2025-11-20',
     '2025-11-25', '2025-12-20',
     'Active', 'Mr. Kumar'),

    ('EQ002', 'Meera Shah', 'DC-002',
     'Nurse John', '2025-10-10',
     '2025-10-15', '2025-12-10',
     'Due', 'Mrs. Anita'),

    ('EQ003', 'Arjun Verma', 'DC-003',
     'Nurse Priya', '2025-09-05',
     '2025-09-10', '2025-11-05',
     'Closed', 'Mr. Roy');
