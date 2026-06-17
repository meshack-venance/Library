package com.shacky.library.services.impl;

import com.shacky.library.common.exception.DuplicateResourceException;
import com.shacky.library.common.exception.ResourceNotFoundException;
import com.shacky.library.dtos.UserDto;
import com.shacky.library.entities.User;
import com.shacky.library.repositories.UserRepository;
import com.shacky.library.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // Convert entity to DTO
    private UserDto toDto(User user) {
        if (user == null) return null;
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

    // Convert DTO to entity
    private User toEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUserType(dto.getUserType());
        user.setClsRoom(dto.getClsRoom());
        return user;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        // Check for duplicates by full name
        Optional<User> existingUser = userRepository.findByFirstNameAndMiddleNameAndLastName(
                userDto.getFirstName(),
                userDto.getMiddleName(),
                userDto.getLastName()
        );

        if (existingUser.isPresent()) {
            throw new DuplicateResourceException("User with the same full name already exists.");
        }

        User user = toEntity(userDto);
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(userDto.getFirstName());
                    existingUser.setMiddleName(userDto.getMiddleName());
                    existingUser.setLastName(userDto.getLastName());
                    existingUser.setEmail(userDto.getEmail());
                    existingUser.setUserType(userDto.getUserType());
                    existingUser.setClsRoom(userDto.getClsRoom());
                    User updated = userRepository.save(existingUser);
                    return toDto(updated);
                })
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toDto);
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByFullName(String firstName, String middleName, String lastName) {
        return userRepository.findByFirstNameAndMiddleNameAndLastName(firstName, middleName, lastName).isPresent();
    }

    @Override
    public void importUsersFromExcel(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) continue;

                String firstName = row.getCell(0).getStringCellValue();
                String middleName = row.getCell(1).getStringCellValue();
                String lastName = row.getCell(2).getStringCellValue();
                String email = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null;
                String userType = row.getCell(4).getStringCellValue();
                String clsRoom = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null;

                UserDto userDto = UserDto.builder()
                        .firstName(firstName)
                        .middleName(middleName)
                        .lastName(lastName)
                        .email(email)
                        .userType(userType)
                        .clsRoom(clsRoom)
                        .build();

                // Avoid duplicate entry
                if (!existsByFullName(firstName, middleName, lastName)) {
                    createUser(userDto);
                }
            }
        }
    }

}
