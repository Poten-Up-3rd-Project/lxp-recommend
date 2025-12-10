package com.lxp.user.presentation.rest.dto.response;

import com.lxp.user.application.port.required.dto.UserInfoDto;

import java.util.List;

public record UserInfoResponse(
    String userId,
    String email,
    String name,
    List<Long> tags, //tag를 아이디 값만 넘기는게 맞는지..
    String learnerLevel,
    String job
) {

    public static UserInfoResponse to(UserInfoDto userInfoDto) {
        return new UserInfoResponse(
            userInfoDto.id(),
            userInfoDto.email(),
            userInfoDto.name(),
            userInfoDto.tags(),
            userInfoDto.learnerLevel(),
            userInfoDto.job()
        );
    }

    //만약 tag 이름을 넣어줘야한다면 이 엔티티로 변경
    public record TagDto(Long id, String name) {
    }
}
