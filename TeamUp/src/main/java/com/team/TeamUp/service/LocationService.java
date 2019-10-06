package com.team.TeamUp.service;

import com.team.TeamUp.domain.Location;
import com.team.TeamUp.persistence.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationService {

    private LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository){
        this.locationRepository = locationRepository;
    }


    public List<Location> getAll() {
        return locationRepository.findAll();
    }
}
