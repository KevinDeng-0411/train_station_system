package com.trainstation.controller;

import com.trainstation.entity.Station;
import com.trainstation.service.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping
    public Map<String, Object> getAllStations() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stationService.getAllStations());
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getStationById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Station station = stationService.getStationById(id);
        result.put("success", station != null);
        result.put("data", station);
        return result;
    }

    @PostMapping
    public Map<String, Object> createStation(@Valid @RequestBody Station station) {
        Map<String, Object> result = new HashMap<>();
        boolean success = stationService.saveStation(station);
        result.put("success", success);
        result.put("message", success ? "创建成功" : "创建失败");
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateStation(@PathVariable Long id, @Valid @RequestBody Station station) {
        Map<String, Object> result = new HashMap<>();
        station.setId(id);
        boolean success = stationService.updateStation(station);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteStation(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = stationService.deleteStation(id);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}
