package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("backup_records")
public class BackupRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String backupFile;

    private String backupPath;

    private Long backupSize;

    private String backupType;

    private String status;

    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
