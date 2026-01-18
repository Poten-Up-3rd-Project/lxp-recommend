package com.lxp.recommend.infrastructure.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 페이징 응답 구조 (Course, Enrollment API용)
 */
@Getter
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean empty;
    private int numberOfElements;

    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements,
                        int totalPages, boolean first, boolean last, boolean hasNext,
                        boolean hasPrevious, boolean empty, int numberOfElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.empty = empty;
        this.numberOfElements = numberOfElements;
    }
}
