DELETE FROM users;

INSERT INTO users (first_name, last_name, email, password, created_at, updated_at)
VALUES ('Hexlet', 'Admin', 'hexlet@example.com', 'secret', NOW(), NOW());