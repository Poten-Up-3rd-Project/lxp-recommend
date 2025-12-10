package com.lxp.recommend.infrastructure.adapter;

// Member BC의 서비스나 Feign Client import (실제 연동 방식에 따라)
// import com.lxp.member.application.MemberQueryService;


/**
 *
 @Component
 @RequiredArgsConstructor
 public class MemberProfileReaderAdapter implements MemberProfileReader {

 // 실제 Member BC를 호출하는 방법 (예시)
 // private final MemberQueryService memberQueryService; // 같은 JVM (Modulith)
 // 또는
 // private final MemberFeignClient memberClient; // 외부 HTTP 호출 (MSA)

 @Override
 public LearnerProfileView getProfile(String memberId) {
 // 실제 Member BC 호출 예시 (현재는 하드코딩)
 // var member = memberQueryService.findById(memberId);

 // 임시 더미 데이터 반환 (Member BC 준비 전까지)
 return new LearnerProfileView(
 memberId,
 CareerType.FRESHMAN,
 Set.of("Java", "Spring", "JPA")
 );
 }
 }

 *
 *
 */