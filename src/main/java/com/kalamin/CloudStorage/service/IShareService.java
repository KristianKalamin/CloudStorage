package com.kalamin.CloudStorage.service;

import com.kalamin.CloudStorage.dto.CombinedFilesDto;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface IShareService extends IService {

    String createSharableLink(String userId, long fileId, boolean isFolder) throws NoSuchAlgorithmException, UnsupportedEncodingException;
    String createShareWithUsersLink(String userId, long fileId, boolean isFolder, List<String> emails) throws UnsupportedEncodingException;

    List<CombinedFilesDto> getAllSharedContent(String userId);

    CombinedFilesDto getSharedContent(String link);
}
