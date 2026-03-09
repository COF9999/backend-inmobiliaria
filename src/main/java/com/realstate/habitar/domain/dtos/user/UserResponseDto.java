package com.realstate.habitar.domain.dtos.user;

import com.realstate.habitar.domain.dtos.token.TokenDto;

public record UserResponseDto(UserRequestDto userRequestDto,
                              TokenDto tokenDto) {
}
