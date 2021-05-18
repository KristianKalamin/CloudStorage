package com.kalamin.CloudStorage.service.impl;

import com.kalamin.CloudStorage.dto.UserDto;
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
    public UserDto login(UserDto userDto) {
        var user = userRepository.findUserByIdEquals(userDto.getSub().trim());
        if (user == null)
            register(userDto.getSub(), userDto.getNickname(), userDto.getEmail());

        return userDto;
    }

    @Transactional
    public void register(String userId, String name, String email) {
        // creates new user with empty storage
        try {
            User newUser = new User(userId, name, email);
            newUser = userRepository.save(newUser);
            String path = createFolderInUploads(userId + "/root");

            java.io.File fRoot = new File(path);
            fRoot.mkdirs();

            Drive userDrive = new Drive(newUser);
            driveRepository.save(userDrive);

            Folder rootFolder = new Folder("root", path, new Timestamp(System.currentTimeMillis()), null, userDrive);
            rootFolder.setDrive(userDrive);
            rootFolder.setParentFolder(rootFolder);
            folderRepository.saveAndFlush(rootFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
