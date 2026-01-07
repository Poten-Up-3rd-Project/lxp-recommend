-- ==========================================
-- V1: 추천 테이블 생성
-- ==========================================

CREATE TABLE IF NOT EXISTS member_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(50) NOT NULL UNIQUE COMMENT '학습자 ID',
    calculated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '추천 계산 시각',
    INDEX idx_member_id (member_id),
    INDEX idx_calculated_at (calculated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='학습자별 추천 Aggregate';

CREATE TABLE IF NOT EXISTS recommended_course_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recommendation_id BIGINT NOT NULL COMMENT '추천 Aggregate FK',
    course_id VARCHAR(50) NOT NULL COMMENT '강좌 ID',
    score DOUBLE NOT NULL COMMENT '추천 점수',
    rank_val INT NOT NULL COMMENT '추천 순위',
    item_index INT NOT NULL COMMENT '목록 순서 (@OrderColumn)',
    FOREIGN KEY (recommendation_id) REFERENCES member_recommendations(id) ON DELETE CASCADE,
    INDEX idx_recommendation_id (recommendation_id),
    INDEX idx_course_id (course_id),
    INDEX idx_score (score DESC),
    INDEX idx_rank (rank_val ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='추천 강좌 목록';
