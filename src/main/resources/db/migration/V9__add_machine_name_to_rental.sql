-- Add machine_name column to rental table for historical reporting
ALTER TABLE rental
ADD COLUMN machine_name TEXT;

-- Update existing rows by copying equipment_name from equipment table
UPDATE rental
SET machine_name = (
    SELECT equipment_name
    FROM equipment
    WHERE equipment.equipment_id = rental.equipment_id
);
