package com.team.teamup.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class ResetRequestDTO {

    private int id;

    private int userId;

    //userd to recive input data
    private String username;

    //userd to recive input data
    private String newPassword;

    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
