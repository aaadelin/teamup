package com.team.teamup.domain;

import com.team.teamup.domain.enums.TimelineVisibility;
import com.team.teamup.domain.enums.UserTheme;
import com.team.teamup.utils.Pair;
import lombok.*;
import org.hibernate.annotations.LazyCollection;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private UserTheme theme;

    private String primaryColor;

    private String secondaryColor;

    private TimelineVisibility timelineVisibility;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "STATUS_VISIBILITY_ID")
    private List<TaskStatusVisibility> taskStatusVisibility;

    // todo quick create


}
