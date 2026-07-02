package com.trainstation.controller;

import com.trainstation.dto.TrainStationPriceDto;
import com.trainstation.entity.TrainStation;
import com.trainstation.service.TrainStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trains/{trainId}/stations")
public class TrainStationController {

    @Autowired
    private TrainStationService trainStationService;

    @GetMapping
    public Map<String, Object> getStationsByTrainId(@PathVariable Long trainId) {
        Map<String, Object> result = new HashMap<>();
        List<TrainStationPriceDto> stations = trainStationService.getStationsByTrainId(trainId);
        result.put("success", true);
        result.put("data", stations);
        return result;
    }

    @PutMapping("/{stationId}/price")
    public Map<String, Object> updateStationPrice(
            @PathVariable Long trainId,
            @PathVariable Long stationId,
            @RequestParam BigDecimal price) {
        Map<String, Object> result = new HashMap<>();
        boolean success = trainStationService.updateStationPrice(trainId, stationId, price);
        result.put("success", success);
        result.put("message", success ? "价格更新成功" : "价格更新失败");
        return result;
    }

    @PostMapping
    public Map<String, Object> addTrainStation(
            @PathVariable Long trainId,
            @RequestBody TrainStation trainStation) {
        Map<String, Object> result = new HashMap<>();
        trainStation.setTrainId(trainId);
        boolean success = trainStationService.saveTrainStation(trainStation);
        result.put("success", success);
        result.put("message", success ? "添加成功" : "添加失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTrainStation(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = trainStationService.deleteTrainStation(id);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}
