package com.kalamin.CloudStorage.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDto {
    private String sub;
    private String nickname;
    private String email;
}
