package com.trainstation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.entity.Salesperson;
import com.trainstation.service.SalespersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/salespeople")
public class SalespersonController {

    @Autowired
    private SalespersonService salespersonService;

    @GetMapping
    public Map<String, Object> getAllSalespeople() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", salespersonService.getAllSalespeople());
        return result;
    }

    @GetMapping("/page")
    public Map<String, Object> getSalespersonPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        Page<Salesperson> page = salespersonService.getSalespersonPage(pageNum, pageSize, keyword);
        result.put("success", true);
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getSalespersonById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Salesperson salesperson = salespersonService.getSalespersonById(id);
        result.put("success", salesperson != null);
        result.put("data", salesperson);
        return result;
    }

    @PostMapping
    public Map<String, Object> createSalesperson(@RequestBody Salesperson salesperson) {
        Map<String, Object> result = new HashMap<>();
        boolean success = salespersonService.saveSalesperson(salesperson);
        result.put("success", success);
        result.put("message", success ? "创建成功" : "创建失败");
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateSalesperson(@PathVariable Long id, @RequestBody Salesperson salesperson) {
        Map<String, Object> result = new HashMap<>();
        salesperson.setId(id);
        boolean success = salespersonService.updateSalesperson(salesperson);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteSalesperson(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = salespersonService.deleteSalesperson(id);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}
