package com.lxp.recommend.dto.parquet;

import java.util.List;

public record ParquetCourseRow(
        String id,
        List<Long> tags,
        int level
) {
}
