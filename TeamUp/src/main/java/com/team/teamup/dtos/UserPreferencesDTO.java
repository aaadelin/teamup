package com.team.teamup.dtos;


import com.team.teamup.domain.TaskStatusVisibility;
import com.team.teamup.domain.enums.TimelineVisibility;
import com.team.teamup.domain.enums.UserTheme;
import com.team.teamup.utils.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@ToString
public class UserPreferencesDTO {

    private int id;

    private UserTheme theme;

    private String primaryColor;

    private String secondaryColor;

    private TimelineVisibility timelineVisibility;
    
    private List<Pair<String, Boolean>> taskStatusVisibility;
}
