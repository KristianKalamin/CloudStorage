package com.kalamin.CloudStorage.service.impl;

import com.kalamin.CloudStorage.dto.CombinedFilesDto;
import com.kalamin.CloudStorage.model.Shared;
import com.kalamin.CloudStorage.model.User;
import com.kalamin.CloudStorage.repository.FileRepository;
import com.kalamin.CloudStorage.repository.FolderRepository;
import com.kalamin.CloudStorage.repository.ShareRepository;
import com.kalamin.CloudStorage.repository.UserRepository;
import com.kalamin.CloudStorage.service.IShareService;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private EmailService emailService;

    @SneakyThrows
    private void sendMail(String shareUri, String userEmail, String userName) {
        final Email email = DefaultEmail.builder()
                .from(new InternetAddress("kalamin.michal@gmail.com", "Cloud Storage"))
                .to(Collections.singletonList(new InternetAddress(userEmail, "Cloud Storage")))
                .subject((userName.equals("")) ? userEmail : userName + " shared content")
                .body("User " + ((userName.equals("")) ? userEmail : userName) + " shared content with you.\nPlease use link below to access shared content:\n\n" + "localhost:4200/share/users/" + shareUri)
                .encoding("UTF-8").build();

        emailService.send(email);
    }

    @Override
    public String createSharableLink(String userId, long fileId, boolean isFolder) throws UnsupportedEncodingException {
        String filePath;
        if (isFolder) {
            filePath = folderRepository.getOne(fileId).getPath();
        } else {
            filePath = fileRepository.getOne(fileId).getPath();
        }

        var shared = shareRepository.findSharedByFilePathEquals(filePath);
        if (shared != null)
            return shared.getSharedUri();

        String link = generateRandomString();
        System.out.println("Share link " + link);

        final var sharingUser = userRepository.getOne(userId);

        shared = new Shared(filePath, link);
        shared
                .getUsers()
                .addAll(Stream.of(sharingUser)
                        .peek(u -> u.getShareds()
                                .add(null))
                        .collect(Collectors.toList()));
        shareRepository.save(shared);


        return URLEncoder.encode(link, StandardCharsets.UTF_8.toString());
    }

    @Override
    public String createShareWithUsersLink(String userId, long fileId, boolean isFolder, List<String> emails) throws UnsupportedEncodingException {
        String filePath;
        if (isFolder) {
            filePath = folderRepository.getOne(fileId).getPath();
        } else {
            filePath = fileRepository.getOne(fileId).getPath();
        }

        String link = generateRandomString();

        final var shared = new Shared(filePath, link);
        List<User> users = new ArrayList<>();

        for (String email : emails) {
            var user = userRepository.findUserByEmailEquals(email);
            if (user != null) {
                users.add(user);
                sendMail(link, user.getEmail(), user.getName());
            } else {
                sendMail(link, email, "");
            }
        }

        shared
                .getUsers()
                .addAll(users
                        .stream()
                        .peek(u -> u.getShareds().add(shared))
                        .collect(Collectors.toList()));

        shareRepository.save(shared);

        return URLEncoder.encode(link, StandardCharsets.UTF_8.toString());
    }

    @Override
    public List<CombinedFilesDto> getAllSharedContent(String userId) {
        var shared = shareRepository.findAllByUsersEquals(new User(userId));

        final List<CombinedFilesDto> all = new ArrayList<>(shared.size());
        for (Shared content : shared) {
            System.out.println(content.toString());
            final var file = fileRepository.getFileByPathEquals(content.getFilePath());
            if (file != null) {
                all.add(new CombinedFilesDto(file));
            } else {
                var folder = folderRepository.getFolderByPathEquals(content.getFilePath());
                all.add(new CombinedFilesDto(folder));
            }
        }
        return all;
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
