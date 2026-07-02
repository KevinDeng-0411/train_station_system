-- MySQL dump 10.13  Distrib 8.4.10, for Linux (aarch64)
--
-- Host: mysql    Database: train_station_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `backup_records`
--

DROP TABLE IF EXISTS `backup_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `backup_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '备份记录ID',
  `backup_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备份文件名',
  `backup_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '备份文件路径',
  `backup_size` bigint DEFAULT NULL COMMENT '备份文件大小(字节)',
  `backup_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'FULL' COMMENT '备份类型: FULL完全',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'SUCCESS' COMMENT '状态: SUCCESS/FAILED',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_created` (`created_at`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据备份记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `backup_records`
--

LOCK TABLES `backup_records` WRITE;
/*!40000 ALTER TABLE `backup_records` DISABLE KEYS */;
INSERT INTO `backup_records` VALUES (2,'backup_20260702_063509.sql','/backups/backup_20260702_063509.sql',NULL,'FULL','FAILED','Cannot run program \"mysqldump\": Exec failed, error: 2 (No such file or directory) ',NULL);
/*!40000 ALTER TABLE `backup_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refund_records`
--

DROP TABLE IF EXISTS `refund_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refund_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '退票记录ID',
  `ticket_id` bigint NOT NULL COMMENT '原车票ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '退票时间',
  `operator_id` bigint NOT NULL COMMENT '操作员ID',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退票原因',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `operator_id` (`operator_id`),
  KEY `idx_ticket` (`ticket_id`),
  KEY `idx_refund_time` (`refund_time`),
  CONSTRAINT `refund_records_ibfk_1` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`),
  CONSTRAINT `refund_records_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `salespeople` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退票记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refund_records`
--

LOCK TABLES `refund_records` WRITE;
/*!40000 ALTER TABLE `refund_records` DISABLE KEYS */;
INSERT INTO `refund_records` VALUES (1,9,88.00,'2026-07-02 06:26:35',3,'乘客行程变更','2026-07-02 06:26:35'),(2,16,184.00,'2026-07-01 22:34:58',1,'test',NULL),(3,2,309.00,'2026-07-01 23:40:08',2,'取消','2026-07-01 23:40:08');
/*!40000 ALTER TABLE `refund_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salespeople`
--

DROP TABLE IF EXISTS `salespeople`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salespeople` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '业务员ID',
  `employee_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工工号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号',
  `status` tinyint DEFAULT '1' COMMENT '状态: 0离职 1在职',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_code` (`employee_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salespeople`
--

LOCK TABLES `salespeople` WRITE;
/*!40000 ALTER TABLE `salespeople` DISABLE KEYS */;
INSERT INTO `salespeople` VALUES (1,'EMP001','张三','13800138001','110101198001011234',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(2,'EMP002','李四','13800138002','110101198002021234',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(3,'EMP003','王五','13800138003','110101198003031234',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(4,'EMP004','赵六','13800138004','110101198004041234',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(5,'EMP005','钱七','13800138005','110101198005051234',0,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(8,'EMP999','测试员','13800000000','110101199901011234',1,NULL,NULL),(10,'123','哈哈','123123123','44123',1,'2026-07-01 22:45:23','2026-07-01 22:45:23');
/*!40000 ALTER TABLE `salespeople` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stations`
--

DROP TABLE IF EXISTS `stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '站点ID',
  `station_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '站点名称',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所在城市',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_station_name` (`station_name`),
  KEY `idx_city` (`city`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stations`
--

LOCK TABLES `stations` WRITE;
/*!40000 ALTER TABLE `stations` DISABLE KEYS */;
INSERT INTO `stations` VALUES (1,'北京南站','北京','2026-07-02 06:26:35'),(2,'天津南站','天津','2026-07-02 06:26:35'),(3,'济南西站','济南','2026-07-02 06:26:35'),(4,'南京南站','南京','2026-07-02 06:26:35'),(5,'无锡东站','无锡','2026-07-02 06:26:35'),(6,'上海虹桥站','上海','2026-07-02 06:26:35'),(9,'测试站1','测试城市1','2026-07-01 22:42:52'),(10,'深圳北站','深圳','2026-07-01 23:38:13'),(11,'宣城站','宣城','2026-07-01 23:38:59'),(12,'广州南站','广州','2026-07-01 23:39:21');
/*!40000 ALTER TABLE `stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '车票ID',
  `train_id` bigint NOT NULL COMMENT '车次ID',
  `passenger_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '乘客姓名',
  `passenger_id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '乘客身份证号',
  `departure_station_id` bigint NOT NULL COMMENT '出发站点ID',
  `arrival_station_id` bigint NOT NULL COMMENT '到达站点ID',
  `seat_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '座位号',
  `price` decimal(10,2) NOT NULL COMMENT '票价',
  `sale_date` date NOT NULL COMMENT '乘车日期',
  `sale_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '售票时间',
  `salesperson_id` bigint NOT NULL COMMENT '售票员ID',
  `status` tinyint DEFAULT '1' COMMENT '状态: 0已退票 1有效',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_train_date_seat` (`train_id`,`sale_date`,`seat_number`),
  KEY `departure_station_id` (`departure_station_id`),
  KEY `arrival_station_id` (`arrival_station_id`),
  KEY `idx_train_date` (`train_id`,`sale_date`),
  KEY `idx_salesperson` (`salesperson_id`),
  KEY `idx_sale_date` (`sale_date`),
  KEY `idx_status` (`status`),
  CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`train_id`) REFERENCES `trains` (`id`),
  CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`departure_station_id`) REFERENCES `stations` (`id`),
  CONSTRAINT `tickets_ibfk_3` FOREIGN KEY (`arrival_station_id`) REFERENCES `stations` (`id`),
  CONSTRAINT `tickets_ibfk_4` FOREIGN KEY (`salesperson_id`) REFERENCES `salespeople` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车票销售记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
INSERT INTO `tickets` VALUES (1,1,'乘客A','310101199001011234',1,2,'1A',88.00,'2026-07-02','2026-07-02 06:26:35',1,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(2,1,'乘客B','310101199002021234',1,4,'1B',309.00,'2026-07-02','2026-07-02 06:26:35',1,0,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(3,1,'乘客C','310101199003031234',2,4,'2A',221.00,'2026-07-02','2026-07-02 06:26:35',2,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(4,1,'乘客D','310101199004041234',3,6,'3A',259.00,'2026-07-02','2026-07-02 06:26:35',2,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(5,1,'乘客E','310101199005051234',4,6,'4A',134.00,'2026-07-02','2026-07-02 06:26:35',3,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(6,1,'乘客F','310101199006061234',1,6,'5A',443.00,'2026-07-02','2026-07-02 06:26:35',3,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(7,1,'乘客G','310101199007071234',5,6,'5B',64.00,'2026-07-02','2026-07-02 06:26:35',4,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(8,1,'乘客H','310101199008081234',1,3,'6A',184.00,'2026-07-02','2026-07-02 06:26:35',4,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(9,2,'乘客I','310101199009091234',1,6,'1A',443.00,'2026-07-02','2026-07-02 06:26:35',1,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(10,2,'乘客J','310101199010101234',2,6,'1B',355.00,'2026-07-02','2026-07-02 06:26:35',2,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(11,2,'乘客K','310101199011111234',3,5,'2A',195.00,'2026-07-02','2026-07-02 06:26:35',3,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(12,4,'乘客L','310101199012121234',1,6,'1A',333.00,'2026-07-02','2026-07-02 06:26:35',4,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(13,4,'乘客M','310101199101011234',2,5,'1B',219.00,'2026-07-02','2026-07-02 06:26:35',1,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(14,4,'乘客N','310101199102021234',4,6,'2A',101.00,'2026-07-02','2026-07-02 06:26:35',2,1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(15,1,'乘客O','310101199103031234',1,2,'7A',88.00,'2026-07-02','2026-07-02 06:26:35',3,0,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(16,3,'测试','110101199001011234',1,3,'99X',184.00,'2026-07-15','2026-07-01 22:34:36',1,0,NULL,NULL),(17,7,'小四','44119293019293',11,12,'1D',50.00,'2026-07-10','2026-07-01 23:39:50',1,1,'2026-07-01 23:39:50','2026-07-01 23:39:50'),(18,7,'小王','444112121',11,12,'1A',53.00,'2026-07-10','2026-07-02 00:25:51',4,1,'2026-07-02 00:25:51','2026-07-02 00:25:51');
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_before_ticket_sale` BEFORE INSERT ON `tickets` FOR EACH ROW BEGIN
    DECLARE v_remaining INT;
    DECLARE v_total INT;
    DECLARE v_seats_sold INT;

    -- 检查车次是否存在
    SELECT total_seats, remaining_seats INTO v_total, v_remaining
    FROM trains
    WHERE id = NEW.train_id;

    -- 如果车次不存在，抛出错误
    IF v_total IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '车次不存在';
    END IF;

    -- 检查是否还有余票
    IF v_remaining <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '余票不足，无法售票';
    END IF;

    -- 检查座位是否已被占用（同一天同一车次同一座位）
    SELECT COUNT(*) INTO v_seats_sold
    FROM tickets
    WHERE train_id = NEW.train_id
      AND sale_date = NEW.sale_date
      AND seat_number = NEW.seat_number
      AND status = 1;

    IF v_seats_sold > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '该座位已被售出';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_after_refund` AFTER UPDATE ON `tickets` FOR EACH ROW BEGIN
    -- 仅当状态从有效变为已退票时触发
    IF OLD.status = 1 AND NEW.status = 0 THEN
        -- 更新车次剩余座位数（应用层也会做，这里作为双重保障）
        UPDATE trains
        SET remaining_seats = remaining_seats + 1
        WHERE id = OLD.train_id AND remaining_seats < total_seats;

        -- 记录退票日志（可扩展为独立日志表）
        -- 本系统使用refund_records表记录，此处仅作为触发器示例
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `train_stations`
--

DROP TABLE IF EXISTS `train_stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `train_stations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `train_id` bigint NOT NULL COMMENT '车次ID',
  `station_id` bigint NOT NULL COMMENT '站点ID',
  `stop_order` int NOT NULL COMMENT '站点顺序',
  `arrival_time` time DEFAULT NULL COMMENT '到达时间',
  `departure_time` time DEFAULT NULL COMMENT '出发时间',
  `price` decimal(10,2) NOT NULL COMMENT '从起点到该站的价格',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_train_station` (`train_id`,`station_id`),
  KEY `idx_train` (`train_id`),
  KEY `idx_station` (`station_id`),
  CONSTRAINT `train_stations_ibfk_1` FOREIGN KEY (`train_id`) REFERENCES `trains` (`id`) ON DELETE CASCADE,
  CONSTRAINT `train_stations_ibfk_2` FOREIGN KEY (`station_id`) REFERENCES `stations` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车次经停站点及价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `train_stations`
--

LOCK TABLES `train_stations` WRITE;
/*!40000 ALTER TABLE `train_stations` DISABLE KEYS */;
INSERT INTO `train_stations` VALUES (1,1,1,1,NULL,'08:00:00',0.00,'2026-07-02 06:26:35'),(2,1,2,2,'08:30:00','08:32:00',88.00,'2026-07-02 06:26:35'),(3,1,3,3,'09:15:00','09:17:00',184.00,'2026-07-02 06:26:35'),(4,1,4,4,'10:45:00','10:47:00',309.00,'2026-07-02 06:26:35'),(5,1,5,5,'11:20:00','11:22:00',379.00,'2026-07-02 06:26:35'),(6,1,6,6,'12:00:00',NULL,443.00,'2026-07-02 06:26:35'),(7,2,1,1,NULL,'09:00:00',0.00,'2026-07-02 06:26:35'),(8,2,2,2,'09:30:00','09:32:00',88.00,'2026-07-02 06:26:35'),(9,2,3,3,'10:15:00','10:17:00',184.00,'2026-07-02 06:26:35'),(10,2,4,4,'11:45:00','11:47:00',309.00,'2026-07-02 06:26:35'),(11,2,5,5,'12:20:00','12:22:00',379.00,'2026-07-02 06:26:35'),(12,2,6,6,'13:00:00',NULL,443.00,'2026-07-02 06:26:35'),(13,3,1,1,NULL,'10:00:00',0.00,'2026-07-02 06:26:35'),(14,3,3,2,'11:00:00','11:02:00',184.00,'2026-07-02 06:26:35'),(15,3,4,3,'12:30:00','12:32:00',309.00,'2026-07-02 06:26:35'),(16,3,6,4,'13:30:00',NULL,443.00,'2026-07-02 06:26:35'),(17,4,1,1,NULL,'14:00:00',0.00,'2026-07-02 06:26:35'),(18,4,2,2,'14:40:00','14:42:00',66.00,'2026-07-02 06:26:35'),(19,4,3,3,'15:35:00','15:37:00',138.00,'2026-07-02 06:26:35'),(20,4,4,4,'17:05:00','17:07:00',232.00,'2026-07-02 06:26:35'),(21,4,5,5,'17:40:00','17:42:00',285.00,'2026-07-02 06:26:35'),(22,4,6,6,'18:20:00',NULL,333.00,'2026-07-02 06:26:35'),(23,5,6,1,NULL,'08:30:00',0.00,'2026-07-02 06:26:35'),(24,5,5,2,'09:08:00','09:10:00',64.00,'2026-07-02 06:26:35'),(25,5,4,3,'09:43:00','09:45:00',134.00,'2026-07-02 06:26:35'),(26,5,3,4,'11:13:00','11:15:00',259.00,'2026-07-02 06:26:35'),(27,5,2,5,'11:58:00','12:00:00',355.00,'2026-07-02 06:26:35'),(28,5,1,6,'12:30:00',NULL,443.00,'2026-07-02 06:26:35'),(29,6,6,1,NULL,'09:30:00',0.00,'2026-07-02 06:26:35'),(30,6,4,2,'10:30:00','10:32:00',134.00,'2026-07-02 06:26:35'),(31,6,3,3,'12:00:00','12:02:00',259.00,'2026-07-02 06:26:35'),(32,6,1,4,'13:30:00',NULL,443.00,'2026-07-02 06:26:35');
/*!40000 ALTER TABLE `train_stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trains`
--

DROP TABLE IF EXISTS `trains`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trains` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '车次ID',
  `train_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '车次号',
  `departure_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '出发城市',
  `arrival_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '到达城市',
  `total_seats` int NOT NULL COMMENT '总座位数',
  `remaining_seats` int NOT NULL COMMENT '剩余座位数',
  `departure_time` time NOT NULL COMMENT '每日发车时间',
  `status` tinyint DEFAULT '1' COMMENT '状态: 0停运 1正常',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_train_number` (`train_number`),
  KEY `idx_departure` (`departure_city`),
  KEY `idx_arrival` (`arrival_city`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车次信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trains`
--

LOCK TABLES `trains` WRITE;
/*!40000 ALTER TABLE `trains` DISABLE KEYS */;
INSERT INTO `trains` VALUES (1,'G101','北京','上海',500,500,'08:00:00',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(2,'G103','北京','上海',500,500,'09:00:00',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(3,'G105','北京','上海',400,400,'10:00:00',1,'2026-07-02 06:26:35','2026-07-02 06:34:57'),(4,'D301','北京','上海',300,300,'14:00:00',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(5,'G201','上海','北京',500,500,'08:30:00',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(6,'G203','上海','北京',500,500,'09:30:00',1,'2026-07-02 06:26:35','2026-07-02 06:26:35'),(7,'G1101','宣城','广州',100,98,'08:00:00',1,NULL,'2026-07-01 23:39:50'),(8,'G999','北京','上海',500,500,'20:00:00',1,NULL,NULL),(9,'G2233','广州','深圳',100,100,'12:00:00',1,'2026-07-01 23:37:59','2026-07-01 23:37:59');
/*!40000 ALTER TABLE `trains` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-02 13:43:08
