package com.lxp.recommend.dto.parquet;

import java.util.List;

public record ParquetUserRow(
        String id,
        List<Long> interestTags,
        int level,
        List<String> purchasedCourseIds,
        List<String> createdCourseIds
) {
}
