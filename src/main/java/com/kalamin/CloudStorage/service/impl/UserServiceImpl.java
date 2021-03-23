package com.kalamin.CloudStorage.service.impl;

import com.kalamin.CloudStorage.dto.UserDto;
import com.kalamin.CloudStorage.exception.UserException;
import com.kalamin.CloudStorage.model.Drive;
import com.kalamin.CloudStorage.model.Folder;
import com.kalamin.CloudStorage.model.User;
import com.kalamin.CloudStorage.repository.DriveRepository;
import com.kalamin.CloudStorage.repository.FolderRepository;
import com.kalamin.CloudStorage.repository.UserRepository;
import com.kalamin.CloudStorage.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.Timestamp;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriveRepository driveRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Override
    public UserDto login(UserDto userDto) throws UserException {
        User user = userRepository.login(userDto.getEmail().trim(), userDto.getPassword().trim());
        if (user == null) {
            throw new UserException("Invalid user data");
        }
        return new UserDto(user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                null);
    }

    @Transactional
    @Override
    public UserDto register(UserDto userDto) {
        // creates new user with empty storage
        User newUser = new User(userDto.getName().trim(), userDto.getLastname().trim(), userDto.getEmail().trim(), userDto.getPassword().trim());
        newUser = userRepository.save(newUser);
        String path = createFolderInUploads(newUser.getId() + "/root");

        java.io.File fRoot = new File(path);
        fRoot.mkdirs();

        Drive userDrive = new Drive(newUser);
        driveRepository.save(userDrive);

        Folder rootFolder = new Folder("root", path, new Timestamp(System.currentTimeMillis()), null, userDrive);
        folderRepository.save(rootFolder);

        rootFolder.setDrive(userDrive);
        rootFolder.setParentFolder(rootFolder);

        return new UserDto(newUser.getId(),
                newUser.getName(),
                newUser.getLastname(),
                newUser.getEmail(),
                null);
    }

}
