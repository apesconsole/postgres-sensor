-- Create the notify_event function
CREATE OR REPLACE FUNCTION notify_event() RETURNS trigger AS $$
BEGIN
    PERFORM pg_notify('events', row_to_json(NEW)::text);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the table if it doesn't exist
CREATE TABLE IF NOT EXISTS sensor_test_tbl (
    id SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TRIGGER event_trigger
AFTER INSERT OR UPDATE ON events.sensor_test_tbl
FOR EACH ROW EXECUTE PROCEDURE events.notify_event();

-- Check if the function exists
SELECT proname FROM pg_proc WHERE proname = 'notify_event';

-- Check if the trigger exists on the table
SELECT tgname FROM pg_trigger WHERE tgrelid = 'sensor_test_tbl'::regclass;

insert into events.sensor_test_tbl(id, name)values(1, 'data 1');
insert into events.sensor_test_tbl(id, name)values(2, 'data 2');
insert into events.sensor_test_tbl(id, name)values(3, 'data 3');

