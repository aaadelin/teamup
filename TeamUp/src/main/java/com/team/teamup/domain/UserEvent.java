package com.team.teamup.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.teamup.domain.enums.UserEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private UserEventType eventType;

    private String description;

    private LocalDateTime time;

    @JsonBackReference(value = "userHistory")
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getTime() {
        return time;
    }
}
