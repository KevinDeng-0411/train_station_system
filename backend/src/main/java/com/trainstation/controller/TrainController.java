package com.trainstation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.entity.Train;
import com.trainstation.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    @Autowired
    private TrainService trainService;

    @GetMapping
    public Map<String, Object> getAllTrains() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", trainService.getAllTrains());
        return result;
    }

    @GetMapping("/page")
    public Map<String, Object> getTrainPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        Page<Train> page = trainService.getTrainPage(pageNum, pageSize, keyword);
        result.put("success", true);
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getTrainById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Train train = trainService.getTrainById(id);
        result.put("success", train != null);
        result.put("data", train);
        return result;
    }

    @PostMapping
    public Map<String, Object> createTrain(@RequestBody Train train) {
        Map<String, Object> result = new HashMap<>();
        boolean success = trainService.saveTrain(train);
        result.put("success", success);
        result.put("message", success ? "创建成功" : "创建失败");
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateTrain(@PathVariable Long id, @RequestBody Train train) {
        Map<String, Object> result = new HashMap<>();
        train.setId(id);
        boolean success = trainService.updateTrain(train);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTrain(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = trainService.deleteTrain(id);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}
