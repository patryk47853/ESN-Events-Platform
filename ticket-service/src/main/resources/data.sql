CREATE UNIQUE INDEX IF NOT EXISTS unique_active_ticket_per_user_event
ON ticket (event_id, user_id)
WHERE status IN ('PENDING', 'CONFIRMED');