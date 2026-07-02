package com.trainstation.controller;

import com.trainstation.entity.BackupRecord;
import com.trainstation.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @PostMapping
    public Map<String, Object> createBackup() {
        Map<String, Object> result = new HashMap<>();
        BackupRecord record = backupService.createBackup();
        result.put("success", "SUCCESS".equals(record.getStatus()));
        result.put("data", record);
        result.put("message", "SUCCESS".equals(record.getStatus()) ? "备份成功" : "备份失败");
        return result;
    }

    @GetMapping
    public Map<String, Object> getAllBackups() {
        Map<String, Object> result = new HashMap<>();
        List<BackupRecord> backups = backupService.getAllBackups();
        result.put("success", true);
        result.put("data", backups);
        return result;
    }

    @PostMapping("/{id}/restore")
    public Map<String, Object> restoreBackup(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = backupService.restoreBackup(id);
        result.put("success", success);
        result.put("message", success ? "恢复成功" : "恢复失败");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBackup(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = backupService.deleteBackup(id);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}
