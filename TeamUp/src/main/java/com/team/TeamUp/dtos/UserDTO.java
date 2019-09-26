package com.team.TeamUp.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.TeamUp.domain.enums.Department;
import com.team.TeamUp.domain.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserDTO {

    private int id;

    private String username;

    private String password;

    private String photo;

    private String firstName;

    private String lastName;

    private LocalDateTime lastActive;

    private boolean isActive;

    private int teamID;

    private Department department;

    private UserStatus status;

    private LocalDate joinedAt;

    private String mail;

    private boolean locked;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getLastActive() {
        return lastActive;
    }
}
