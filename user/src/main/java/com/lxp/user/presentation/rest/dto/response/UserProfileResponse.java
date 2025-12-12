package com.lxp.user.presentation.rest.dto.response;

import com.lxp.user.application.port.required.dto.UserInfoDto;

import java.util.List;

public record UserProfileResponse(
    String userId,
    String email,
    String name,
    List<Long> tags, // redis 연결 후 tagDto로 변경할 것
    String level
) {

    public static UserProfileResponse to(UserInfoDto userInfoDto) {
        return new UserProfileResponse(
            userInfoDto.id(),
            userInfoDto.email(),
            userInfoDto.name(),
            userInfoDto.tags(),
            userInfoDto.level()
        );
    }

    //만약 tag 이름을 넣어줘야한다면 이 엔티티로 변경
    public record TagDto(Long id, String name) {
    }
}
