package com.realstate.habitar.infraestructure.rest.model;

import com.realstate.habitar.application.usecases.user.UserService;
import com.realstate.habitar.domain.dtos.user.UserRequestDto;
import com.realstate.habitar.domain.dtos.user.UserResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/user")
public class UserRest {
    private final UserService userService;

    public UserRest(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody List<UserRequestDto> listUserRequestDto){
        for(UserRequestDto item:listUserRequestDto){
            userService.create(item);
        }
        return "OK";
    }
    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id){
        return userService.search(id);
    }

    @PutMapping("/")
    public UserResponseDto updateUser(@RequestBody UserRequestDto userRequestDto){
        return userService.update(userRequestDto);
    }

    @DeleteMapping("/{id}")
    public UserResponseDto deleteUser(@PathVariable Long id){
        return null;
    }

}
