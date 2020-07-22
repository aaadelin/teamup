package com.team.teamup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.teamup.utils.query.annotations.SearchField;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, length = 100)
    @SearchField
    private String username;

    @JsonIgnore
    private String password;

    private LocalDateTime lastActive;

    private boolean isActive;

    private String hashKey;

    @Column(columnDefinition = "integer default 60")
    private int minutesUntilLogout;

    private boolean locked;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAuthentication)) return false;
        UserAuthentication that = (UserAuthentication) o;
        return id == that.id &&
                isActive == that.isActive &&
                minutesUntilLogout == that.minutesUntilLogout &&
                locked == that.locked &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(lastActive, that.lastActive) &&
                Objects.equals(hashKey, that.hashKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, lastActive, isActive, hashKey, minutesUntilLogout, locked);
    }
}
