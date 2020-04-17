package com.team.teamup.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class ResetRequest {

    public static final int MAX_MINUTES = 60 * 24; //24 hours

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    private LocalDateTime createdAt;
}

