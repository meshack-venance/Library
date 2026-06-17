package com.shacky.library.services;

import com.shacky.library.dtos.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    UserDto getUserById(Long id);

    Page<UserDto> getAllUsers(Pageable pageable);

    void deleteUser(Long id);

    boolean existsByFullName(String firstName, String middleName, String lastName);

    void importUsersFromExcel(MultipartFile file) throws Exception;


}
