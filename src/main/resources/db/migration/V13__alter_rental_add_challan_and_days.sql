-- ============================================
-- V13__alter_rental_add_challan_and_days.sql
-- Purpose: Support multi-machine rental using
--          single rental table with challan grouping
-- ============================================

-- 1️⃣ Add challan number (NOT UNIQUE)
ALTER TABLE rental ADD COLUMN challan_no TEXT;

-- 2️⃣ Add days column (store rental duration)
ALTER TABLE rental ADD COLUMN days INTEGER DEFAULT 1;

-- 3️⃣ Add soft delete column
ALTER TABLE rental ADD COLUMN is_deleted INTEGER DEFAULT 0;

-- 4️⃣ Create index for challan grouping
CREATE INDEX IF NOT EXISTS idx_rental_challan
ON rental(challan_no);

-- 5️⃣ Create index for soft delete filtering
CREATE INDEX IF NOT EXISTS idx_rental_deleted
ON rental(is_deleted);

-- ============================================
-- Notes:
-- Multiple rows can share same challan_no.
-- One challan = one rental transaction.
-- ============================================