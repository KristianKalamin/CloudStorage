package com.kalamin.CloudStorage.service.impl;

import com.kalamin.CloudStorage.dto.CombinedFilesDto;
import com.kalamin.CloudStorage.model.Shared;
import com.kalamin.CloudStorage.repository.FileRepository;
import com.kalamin.CloudStorage.repository.FolderRepository;
import com.kalamin.CloudStorage.repository.ShareRepository;
import com.kalamin.CloudStorage.repository.UserRepository;
import com.kalamin.CloudStorage.service.IShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;

@Service
public class ShareServiceImpl implements IShareService {
    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String createSharableLink(long userId, long fileId, boolean isFolder) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String filePath;
        if (isFolder) {
            filePath = folderRepository.getOne(fileId).getPath();
        } else {
            filePath = fileRepository.getOne(fileId).getPath();
        }

        var shared = shareRepository.findSharedByFilePathEquals(filePath);
        if (shared != null)
            return shared.getSharedUri();

        System.out.println(filePath);
        String link = generateRandomString();

        final var sharingUser = userRepository.getOne(userId);

        shared = new Shared(filePath, link, Collections.singletonList(sharingUser));
        shared = shareRepository.save(shared);

        final var user = userRepository.getOne(userId);
        user.getShareds().add(shared);
        userRepository.save(user);

        return URLEncoder.encode(link, StandardCharsets.UTF_8.toString());
    }


    @Override
    public CombinedFilesDto getSharedContent(String link) {
        final var shared = shareRepository.findBySharedUriEquals(link);

        final var file = fileRepository.getFileByPathEquals(shared.getFilePath());
        if (file != null) {
            return new CombinedFilesDto(file);
        } else {
            var folder = folderRepository.getFolderByPathEquals(shared.getFilePath());
            return new CombinedFilesDto(folder);
        }
    }

    private String generateRandomString() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}
