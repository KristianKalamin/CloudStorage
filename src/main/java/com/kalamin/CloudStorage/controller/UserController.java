package com.kalamin.CloudStorage.controller;

import com.kalamin.CloudStorage.dto.UserDto;
import com.kalamin.CloudStorage.exception.UserException;
import com.kalamin.CloudStorage.service.IUserService;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
    @Autowired
    private IUserService userService;

    @NotNull
    @PostMapping("login")
    private @ResponseBody
    ResponseEntity<UserDto> login(@RequestBody @Valid UserDto user) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.login(user));
        } catch (UserException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @NotNull
    @PostMapping("register")
    private @ResponseBody
    ResponseEntity<UserDto> register(@RequestBody @Valid UserDto user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.register(user));
    }
}
