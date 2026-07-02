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

    // ========== 仪表盘新增接口 ==========

    @GetMapping("/kpi")
    public Map<String, Object> getKpi() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = statisticsService.getKpi();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/trend")
    public Map<String, Object> getTrend() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = statisticsService.get7DayTrend();
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/train-top")
    public Map<String, Object> getTrainTop(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = statisticsService.getTrainTopRevenue(limit);
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/station-popular")
    public Map<String, Object> getStationPopular(
            @RequestParam(defaultValue = "departure") String type,
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = statisticsService.getStationPopularity(type, limit);
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
