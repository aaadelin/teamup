package com.team.teamup.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.teamup.domain.enums.Department;
import com.team.teamup.domain.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
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

    private Date joinedAt;

    private String mail;

    private boolean locked;

    private boolean hasUnfinishedTasks;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getLastActive() {
        return lastActive;
    }
}
