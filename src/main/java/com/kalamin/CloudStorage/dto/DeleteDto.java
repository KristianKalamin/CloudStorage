package com.kalamin.CloudStorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteDto {
    private long id;

    @JsonProperty
    private boolean isFolder;
}
