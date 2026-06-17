package com.shacky.library.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Long totalBorrowed; // from query
    private String userType;
    private String clsRoom;

    // Constructor for JPQL query
    public UserDto(Long id, String firstName,String middleName, String lastName, String email, Long totalBorrowed, String userType, String clsRoom) {
        this.id = id;
        this.firstName = firstName;
        this.middleName=middleName;
        this.lastName = lastName;
        this.email = email;
        this.totalBorrowed = totalBorrowed;
        this.userType = userType;
        this.clsRoom = clsRoom;
    }

    public UserDto(Long id, String firstName, String lastName,String email, Long totalBorrowed, String userType, String clsRoom) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email= email;
        this.totalBorrowed = totalBorrowed;
        this.userType = userType;
        this.clsRoom = clsRoom;
    }

    public String getFullName() {
        if (firstName == null&& middleName==null && lastName == null) return "";
        if (firstName == null) return middleName +" " + lastName;
        if (middleName==null) return firstName+" " + lastName;
        if (lastName == null) return firstName + " " + middleName;
        return firstName + " " + middleName+" "+lastName;
    }
}
