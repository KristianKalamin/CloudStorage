package com.kalamin.CloudStorage.service;

import com.kalamin.CloudStorage.dto.CombinedFilesDto;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface IShareService extends IService {

    String createSharableLink(long userId, long fileId, boolean isFolder) throws NoSuchAlgorithmException, UnsupportedEncodingException;

    CombinedFilesDto getSharedContent(String link);
}
