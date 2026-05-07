-- Backfill script for availability table (MySQL 8+).
-- Run once after `homestay_availability` is created, especially when old reservations already exist.

-- 1) Remove stale availability rows: reservation deleted.
DELETE ha
FROM homestay_availability ha
LEFT JOIN reservation r ON r.id = ha.reservation_id
WHERE r.id IS NULL;

-- 2) Rebuild missing day-by-day occupancy rows from reservation date ranges.
INSERT IGNORE INTO homestay_availability (room_id, reservation_id, stay_date)
WITH RECURSIVE stay_days AS (
    SELECT
        r.id AS reservation_id,
        r.room_id AS room_id,
        DATE(r.date) AS stay_date,
        DATE(r.check_out_date) AS check_out_date
    FROM reservation r
    WHERE r.room_id IS NOT NULL
      AND r.date IS NOT NULL
      AND r.check_out_date IS NOT NULL
      AND DATE(r.date) < DATE(r.check_out_date)

    UNION ALL

    SELECT
        reservation_id,
        room_id,
        DATE_ADD(stay_date, INTERVAL 1 DAY),
        check_out_date
    FROM stay_days
    WHERE DATE_ADD(stay_date, INTERVAL 1 DAY) < check_out_date
)
SELECT room_id, reservation_id, stay_date
FROM stay_days;
