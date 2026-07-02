package com.trainstation.controller;

import com.trainstation.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/trains/{trainNumber}/date/{date}")
    public Map<String, Object> getTrainSalesStatistics(
            @PathVariable String trainNumber,
            @PathVariable String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = statisticsService.getTrainSalesStatistics(trainNumber, date);
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/salespeople/date/{date}")
    public Map<String, Object> getSalespersonRevenue(@PathVariable String date) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = statisticsService.getSalespersonRevenue(date);
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
