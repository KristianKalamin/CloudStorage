package com.kalamin.CloudStorage.service;

import com.kalamin.CloudStorage.dto.UserDto;
import com.kalamin.CloudStorage.exception.UserException;
import com.kalamin.CloudStorage.model.User;

import javax.transaction.Transactional;

public interface IUserService extends IService {
    @Transactional
    UserDto register(UserDto userDto);

    UserDto login(UserDto userDto) throws UserException;
}
