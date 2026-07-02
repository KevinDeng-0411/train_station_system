package com.trainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trainstation.entity.BackupRecord;
import com.trainstation.mapper.BackupRecordMapper;
import com.trainstation.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class BackupServiceImpl implements BackupService {

    @Autowired
    private BackupRecordMapper backupRecordMapper;

    @Value("${app.backup.path:/backups}")
    private String backupPath;

    @Override
    public BackupRecord createBackup() {
        BackupRecord record = new BackupRecord();
        String fileName = "backup_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".sql";
        String filePath = backupPath + "/" + fileName;

        try {
            // 确保备份目录存在
            File backupDir = new File(backupPath);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            // 执行mysqldump备份
            String[] command = {
                "mysqldump",
                "-h" + "localhost",
                "-uroot",
                "-proot123456",
                "--single-transaction",
                "--quick",
                "--lock-tables=false",
                "train_station_db"
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(new File(filePath));

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                File backupFile = new File(filePath);
                record.setBackupFile(fileName);
                record.setBackupPath(filePath);
                record.setBackupSize(backupFile.length());
                record.setBackupType("FULL");
                record.setStatus("SUCCESS");
                backupRecordMapper.insert(record);
            } else {
                record.setBackupFile(fileName);
                record.setBackupPath(filePath);
                record.setStatus("FAILED");
                record.setErrorMessage("备份失败，退出码: " + exitCode);
                backupRecordMapper.insert(record);
            }
        } catch (Exception e) {
            record.setBackupFile(fileName);
            record.setBackupPath(filePath);
            record.setStatus("FAILED");
            record.setErrorMessage(e.getMessage());
            backupRecordMapper.insert(record);
        }

        return record;
    }

    @Override
    public List<BackupRecord> getAllBackups() {
        LambdaQueryWrapper<BackupRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BackupRecord::getCreatedAt);
        return backupRecordMapper.selectList(wrapper);
    }

    @Override
    public boolean restoreBackup(Long backupId) {
        BackupRecord backup = backupRecordMapper.selectById(backupId);
        if (backup == null || !"SUCCESS".equals(backup.getStatus())) {
            return false;
        }

        try {
            String[] command = {
                "mysql",
                "-h" + "localhost",
                "-uroot",
                "-proot123456",
                "train_station_db"
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectInput(new File(backup.getBackupPath()));
            Process process = pb.start();
            int exitCode = process.waitFor();

            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteBackup(Long backupId) {
        BackupRecord backup = backupRecordMapper.selectById(backupId);
        if (backup == null) {
            return false;
        }

        // 删除物理文件
        File file = new File(backup.getBackupPath());
        if (file.exists()) {
            file.delete();
        }

        // 删除数据库记录
        return backupRecordMapper.deleteById(backupId) > 0;
    }
}
