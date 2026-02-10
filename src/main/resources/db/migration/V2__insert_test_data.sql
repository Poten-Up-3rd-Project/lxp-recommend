-- Test Users
INSERT INTO recommend_user (id, interest_tags, level, enrolled_course_ids, created_course_ids, status, created_at, updated_at)
VALUES
    ('user-001', '[1, 2, 3]', 'JUNIOR', '[]', '[]', 'ACTIVE', NOW(), NOW()),
    ('user-002', '[2, 3, 4]', 'MIDDLE', '["course-001"]', '[]', 'ACTIVE', NOW(), NOW()),
    ('user-003', '[1, 4, 5]', 'SENIOR', '[]', '["course-003"]', 'ACTIVE', NOW(), NOW()),
    ('user-004', '[3, 5, 6]', 'JUNIOR', '["course-002"]', '[]', 'ACTIVE', NOW(), NOW()),
    ('user-005', '[1, 2, 6]', 'EXPERT', '[]', '["course-005"]', 'ACTIVE', NOW(), NOW()),
    ('instructor-001', '[1, 2, 3, 4, 5]', 'EXPERT', '[]', '["course-001", "course-002"]', 'ACTIVE', NOW(), NOW()),
    ('instructor-002', '[2, 3, 4, 5, 6]', 'EXPERT', '[]', '["course-003", "course-004", "course-005"]', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE id = id;

-- Test Courses
INSERT INTO recommend_course (id, tags, level, instructor_id, status, created_at, updated_at)
VALUES
    ('course-001', '[1, 2]', 'JUNIOR', 'instructor-001', 'ACTIVE', NOW(), NOW()),
    ('course-002', '[2, 3]', 'JUNIOR', 'instructor-001', 'ACTIVE', NOW(), NOW()),
    ('course-003', '[3, 4]', 'MIDDLE', 'instructor-002', 'ACTIVE', NOW(), NOW()),
    ('course-004', '[4, 5]', 'SENIOR', 'instructor-002', 'ACTIVE', NOW(), NOW()),
    ('course-005', '[5, 6]', 'EXPERT', 'instructor-002', 'ACTIVE', NOW(), NOW()),
    ('course-006', '[1, 3, 5]', 'JUNIOR', 'instructor-001', 'ACTIVE', NOW(), NOW()),
    ('course-007', '[2, 4, 6]', 'MIDDLE', 'instructor-002', 'ACTIVE', NOW(), NOW()),
    ('course-008', '[1, 2, 3]', 'SENIOR', 'instructor-001', 'ACTIVE', NOW(), NOW()),
    ('course-009', '[4, 5, 6]', 'EXPERT', 'instructor-002', 'ACTIVE', NOW(), NOW()),
    ('course-010', '[1, 4]', 'MIDDLE', 'instructor-001', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE id = id;
