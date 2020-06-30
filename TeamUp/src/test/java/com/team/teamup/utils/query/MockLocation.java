package com.team.teamup.utils.query;

import com.team.teamup.utils.query.annotations.SearchEntity;
import com.team.teamup.utils.query.annotations.SearchField;
import lombok.Builder;
import lombok.Getter;

@Getter
@SearchEntity
@Builder
public class MockLocation {
    @SearchField
    private String address;
    @SearchField
    private String country;
}
