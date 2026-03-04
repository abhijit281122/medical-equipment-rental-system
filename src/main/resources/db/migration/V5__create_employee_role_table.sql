CREATE TABLE employee_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_name TEXT UNIQUE NOT NULL
);

INSERT INTO employee_role (role_name) VALUES
('Data Entry Operator'),
('Medical Representative'),
('Delivery Person'),
('Rent Collector');