package com.lxp.recommend.domain.model.ids;

/**
 * 강좌 난이도 레벨
 *
 * 각 BC(Bounded Context)는 자체 도메인 모델을 가지므로
 * recommend 서비스에서도 독립적으로 정의함.
 */
public enum Level {
    JUNIOR,   // 초급
    MIDDLE,   // 중급
    SENIOR,   // 고급
    EXPERT;   // 전문가

    /**
     * 문자열 → Enum 변환 (대소문자 무시)
     *
     * @param value 문자열 값 (예: "junior", "MIDDLE")
     * @return Level Enum
     * @throws IllegalArgumentException 유효하지 않은 값인 경우
     */
    public static Level fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Level 값은 비어있을 수 없습니다.");
        }

        try {
            return Level.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "유효하지 않은 Level 값: " + value +
                            ". 허용된 값: JUNIOR, MIDDLE, SENIOR, EXPERT"
            );
        }
    }
}
