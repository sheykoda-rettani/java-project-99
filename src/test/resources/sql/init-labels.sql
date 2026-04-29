DELETE FROM labels;

INSERT INTO labels (name, created_at) VALUES
('feature', NOW()),
('bug', NOW());