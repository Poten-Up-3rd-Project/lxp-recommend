package com.lxp.recommend.infrastructure.batch.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

/**
 * 추천 대상 학습자 ID 조회 Reader
 * 전체 학습자 목록을 조회하여 순차 반환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LearnerIdReader implements ItemReader<String> {

    private final JdbcTemplate jdbcTemplate;
    private Iterator<String> learnerIdIterator;

    /**
     * 학습자 ID를 하나씩 반환
     * 첫 호출 시 전체 학습자 조회 후 Iterator로 순회
     *
     * @return 다음 학습자 ID, 없으면 null (배치 종료 신호)
     */
    @Override
    public String read() {
        if (learnerIdIterator == null) {
            List<String> learnerIds = fetchAllLearnerIds();
            log.info("[Batch Reader] Loaded {} learners for recommendation batch", learnerIds.size());
            learnerIdIterator = learnerIds.iterator();
        }

        return learnerIdIterator.hasNext() ? learnerIdIterator.next() : null;
    }

    /**
     * DB에서 전체 학습자 ID 조회
     * 기존 추천 테이블에서 DISTINCT member_id 추출
     *
     * TODO: Member BC API 호출로 변경 가능
     */
    private List<String> fetchAllLearnerIds() {
        String sql = "SELECT DISTINCT member_id FROM member_recommendations ORDER BY member_id";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}
