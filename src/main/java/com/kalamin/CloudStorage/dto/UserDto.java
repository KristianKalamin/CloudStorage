package com.kalamin.CloudStorage.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDto {
    private long id;
    private String name;
    private String lastname;
    private String email;
    private String password;
}
