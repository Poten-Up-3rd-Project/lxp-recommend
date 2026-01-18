package com.lxp.recommend.domain.model;

import java.util.Set;

/**
 * 추천 점수 계산에 사용되는 태그 컨텍스트
 *
 * - Explicit Tags: 사용자가 프로필에서 선택한 관심 태그
 * - Implicit Tags: 수강 중인 강좌에서 추출한 태그
 */
public record TagContext(
        Set<String> explicitTags,
        Set<String> implicitTags
) {
    /**
     * Compact Constructor: 불변성 보장 및 null 방어
     */
    public TagContext {
        explicitTags = explicitTags != null ? Set.copyOf(explicitTags) : Set.of();
        implicitTags = implicitTags != null ? Set.copyOf(implicitTags) : Set.of();
    }

    /**
     * 태그가 하나라도 있는지 확인
     */
    public boolean hasAnyTag() {
        return !explicitTags.isEmpty() || !implicitTags.isEmpty();
    }

    /**
     * Explicit 태그인지 확인
     */
    public boolean isExplicit(String tag) {
        return explicitTags.contains(tag);
    }

    /**
     * Implicit 태그인지 확인
     */
    public boolean isImplicit(String tag) {
        return implicitTags.contains(tag);
    }

    /**
     * 태그가 Explicit 또는 Implicit 중 하나라도 포함되는지 확인
     */
    public boolean contains(String tag) {
        return isExplicit(tag) || isImplicit(tag);
    }

    /**
     * 전체 태그 수
     */
    public int totalTagCount() {
        return explicitTags.size() + implicitTags.size();
    }

    @Override
    public String toString() {
        return String.format("TagContext(explicit=%d, implicit=%d)",
                explicitTags.size(), implicitTags.size());
    }


    /**
     * Explicit 태그만으로 TagContext 생성
     * (Implicit 태그는 빈 Set)
     *
     * @param explicitTags 사용자가 선택한 관심 태그
     * @return TagContext
     */
    public static TagContext create(Set<String> explicitTags) {
        return new TagContext(explicitTags, Set.of());
    }

    /**
     * Explicit + Implicit 태그로 TagContext 생성
     *
     * @param explicitTags 사용자가 선택한 관심 태그
     * @param implicitTags 학습 이력에서 추출한 태그
     * @return TagContext
     */
    public static TagContext of(Set<String> explicitTags, Set<String> implicitTags) {
        return new TagContext(explicitTags, implicitTags);
    }

}
