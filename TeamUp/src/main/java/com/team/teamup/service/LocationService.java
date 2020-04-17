package com.team.teamup.service;

import com.team.teamup.domain.Location;
import com.team.teamup.persistence.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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

    public Location save(Location location){
        return locationRepository.save(location);
    }

    public Optional<Location> findById(int id) {
        return locationRepository.findById(id);
    }

    public void delete(int id) {
        locationRepository.deleteById(id);
    }
}
