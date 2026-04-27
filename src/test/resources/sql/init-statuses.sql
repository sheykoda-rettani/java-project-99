DELETE FROM task_statuses;

INSERT INTO task_statuses (name, slug, created_at) VALUES
('Черновик', 'draft', NOW()),
('На ревью', 'to_review', NOW()),
('На исправление', 'to_be_fixed', NOW()),
('К публикации', 'to_publish', NOW()),
('Опубликовано', 'published', NOW());