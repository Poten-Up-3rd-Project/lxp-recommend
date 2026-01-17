package com.lxp.recommend.infrastructure.web.internal.client;

import com.lxp.recommend.infrastructure.external.common.InternalApiResponse;
import com.lxp.recommend.infrastructure.external.member.dto.MemberProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Member BC Internal API 호출용 FeignClient
 */
@FeignClient(
        name = "member-service",
        url = "${external.member.base-url}"
)
public interface MemberServiceFeignClient {

    @GetMapping("/internal/api-v1/users/{userId}/profile")
    ResponseEntity<MemberProfileResponse> getMemberProfile(@PathVariable("userId") String userId);
}
