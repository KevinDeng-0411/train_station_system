package com.trainstation.service;

import com.trainstation.entity.Station;
import java.util.List;

public interface StationService {
    List<Station> getAllStations();
    Station getStationById(Long id);
    Station getStationByName(String name);
    boolean saveStation(Station station);
    boolean updateStation(Station station);
    boolean deleteStation(Long id);
}
