package com.kalamin.CloudStorage.controller;


import com.kalamin.CloudStorage.dto.CombinedFilesDto;
import com.kalamin.CloudStorage.dto.ShareDto;
import com.kalamin.CloudStorage.dto.ShareUsersDto;
import com.kalamin.CloudStorage.service.IShareService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("share")
public class ShareController {

    @Autowired
    private IShareService shareService;

    @PostMapping(value = "content", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    ResponseEntity<String> share(@RequestBody @Valid ShareDto shareDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    shareService.createSharableLink(shareDto.getUserId(), shareDto.getFileId(), shareDto.isFolder()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "content/users", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    ResponseEntity<String> shareWithUsers(@RequestBody @Valid ShareUsersDto shareUsersDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    shareService.createShareWithUsersLink(shareUsersDto.getUserId(), shareUsersDto.getFileId(), shareUsersDto.isFolder(), shareUsersDto.getEmails()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{link}")
    public @ResponseBody
    ResponseEntity<CombinedFilesDto> getSharedContent(@PathVariable("link") String link) {
        try {
            System.out.println(link);
            return ResponseEntity.status(HttpStatus.OK).body(
                    shareService.getSharedContent(link));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("shared-with-me")
    public @ResponseBody
    ResponseEntity<List<CombinedFilesDto>> getSharedWithMe(@RequestBody String userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    shareService.getAllSharedContent(userId));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
