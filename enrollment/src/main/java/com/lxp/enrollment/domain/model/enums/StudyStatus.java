package com.lxp.enrollment.domain.model.enums;

/**
 * 진행률 상태 정의 Enum
 */
public enum StudyStatus {

    IN_PROGRESS("진행 중"),
    COMPLETED("완료");

    private final String description;

    StudyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
