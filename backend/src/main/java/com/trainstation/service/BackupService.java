package com.trainstation.service;

import com.trainstation.entity.BackupRecord;
import java.util.List;

public interface BackupService {
    BackupRecord createBackup();
    List<BackupRecord> getAllBackups();
    boolean restoreBackup(Long backupId);
    boolean deleteBackup(Long backupId);
}
