-- ==========================================
-- V1: 추천 서비스 테이블 생성 (MySQL)
-- ==========================================

-- recommend_user 테이블
CREATE TABLE IF NOT EXISTS recommend_user (
    id VARCHAR(36) PRIMARY KEY,
    interest_tags JSON NOT NULL,
    level VARCHAR(20) NOT NULL,
    enrolled_course_ids JSON NOT NULL,
    created_course_ids JSON NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_recommend_user_status ON recommend_user(status);

-- recommend_course 테이블
CREATE TABLE IF NOT EXISTS recommend_course (
    id VARCHAR(36) PRIMARY KEY,
    tags JSON NOT NULL,
    level VARCHAR(20) NOT NULL,
    instructor_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_recommend_course_status ON recommend_course(status);
CREATE INDEX idx_recommend_course_instructor ON recommend_course(instructor_id);

-- recommend_result 테이블
CREATE TABLE IF NOT EXISTS recommend_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    course_ids JSON NOT NULL,
    batch_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_recommend_result_user_id ON recommend_result(user_id);
