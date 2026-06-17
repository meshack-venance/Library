package com.shacky.library.mappers;

import com.shacky.library.dtos.UserDto;
import com.shacky.library.entities.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userType(user.getUserType())
                .clsRoom(user.getClsRoom())
                .build();
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .userType(dto.getUserType())
                .clsRoom(dto.getClsRoom())
                .build();
    }
}
