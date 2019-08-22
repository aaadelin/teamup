package com.team.TeamUp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.TeamUp.domain.enums.UserEventType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserEvent {

    @Id
    @GeneratedValue
    private int id;

    private UserEventType eventType;

    private String description;

    private LocalDateTime time;

    @JsonBackReference(value = "userHistory")
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User creator;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEventType getEventType() {
        return eventType;
    }

    public void setEventType(UserEventType eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "id=" + id +
                ", eventType=" + eventType +
                ", description='" + description + '\'' +
                ", time=" + time +
                '}';
    }
}
