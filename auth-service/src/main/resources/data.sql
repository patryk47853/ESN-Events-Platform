INSERT INTO users (
    email,
    password,
    role
)
VALUES (
    'student@esn.test',
    '$2b$12$.CaSlchdR3r.ywIXUf4Z8eH3f.7azH.wTjtdg0hfl9/iMea64OIwq',
    'USER'
)
ON CONFLICT (email)
DO UPDATE SET
    password = EXCLUDED.password,
    role = EXCLUDED.role;

INSERT INTO users (
    email,
    password,
    role
)
VALUES (
    'organizer@esn.test',
    '$2b$12$.CaSlchdR3r.ywIXUf4Z8eH3f.7azH.wTjtdg0hfl9/iMea64OIwq',
    'ORGANIZER'
)
ON CONFLICT (email)
DO UPDATE SET
    password = EXCLUDED.password,
    role = EXCLUDED.role;