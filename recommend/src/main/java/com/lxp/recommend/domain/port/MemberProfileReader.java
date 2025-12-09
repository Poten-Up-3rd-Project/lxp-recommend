package com.lxp.recommend.domain.port;

import com.lxp.recommend.domain.dto.LearnerProfileView;

public interface MemberProfileReader {
    // 외부 컨텍스트 통신이므로 Long 사용 (또는 MemberId VO 사용해도 무방)
    LearnerProfileView getProfile(String memberId);
}
