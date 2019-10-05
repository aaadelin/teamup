package com.team.TeamUp.service;

import com.team.TeamUp.domain.Team;
import com.team.TeamUp.domain.User;
import com.team.TeamUp.dtos.TeamDTO;
import com.team.TeamUp.persistence.TeamRepository;
import com.team.TeamUp.utils.DTOsConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class TeamService {

    private TeamRepository teamRepository;
    private DTOsConverter dtOsConverter;

    public TeamService (TeamRepository teamRepository){
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }

    public List<TeamDTO> getAllTeamDTO(){
        return getAllTeams().stream().map(dtOsConverter::getDTOFromTeam).collect(Collectors.toList());
    }

    public Team getTeamByID(int id){
        return teamRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public TeamDTO getTeamDTOByID(int id){
        return dtOsConverter.getDTOFromTeam(getTeamByID(id));
    }

    public List<Team> getByLeader(User leader){
        return teamRepository.findAllByLeader(leader);
    }

    public Team save(Team newTeam) {
        return teamRepository.save(newTeam);
    }
}
