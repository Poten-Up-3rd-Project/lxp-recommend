package com.lxp.enrollment.application.port.provided;

import com.lxp.common.domain.pagination.Page;
import com.lxp.enrollment.application.port.provided.dto.EnrollmentHistoryItem;
import com.lxp.enrollment.application.port.provided.query.GetEnrollmentHistoryQuery;

public interface GetEnrollmentHistoryUseCase {
    Page<EnrollmentHistoryItem> getEnrollmentHistory(GetEnrollmentHistoryQuery query);
}
