package com.realstate.habitar.application.usecases.user;

import com.realstate.habitar.domain.dtos.user.RoleChangeDto;
import com.realstate.habitar.domain.dtos.user.UserRequestDto;
import com.realstate.habitar.domain.dtos.user.UserResponseDto;
import com.realstate.habitar.infraestructure.classes.model.User;

import java.util.List;

public interface UserService {
    UserResponseDto search(Long id);
    UserResponseDto create(UserRequestDto object);
    UserResponseDto update(UserRequestDto object);
    void activeDelete(Long id);
    UserResponseDto convertToDto(User user);
    List<UserResponseDto> getUserList();
    UserResponseDto changeRole(RoleChangeDto roleChangeDto);
}
