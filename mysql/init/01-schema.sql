-- ============================================================================
-- 火车站票务管理系统 - 数据库表结构设计
-- ============================================================================
--
-- 【数据库设计说明】
-- 本数据库设计遵循第三范式(3NF)原则：
--   1NF: 所有属性原子性，不可再分
--   2NF: 非主键属性完全依赖于主键（消除部分依赖）
--   3NF: 非主键属性之间不存在传递依赖
--
-- 【函数依赖分析】
--
-- 候选键识别:
--   tickets表: (train_id, sale_date, seat_number) 三元组为主键
--   train_stations表: (train_id, station_id) 二元组为主键
--   salespeople表: employee_code 为候选键
--
-- 函数依赖:
--   tickets: (train_id, sale_date, seat_number) → 所有票务属性
--   train_stations: (train_id, station_id) → stop_order, arrival_time, departure_time, price
--   salespeople: employee_code → name, phone, id_card, status
--
-- 【范式符合性验证】
--   ✅ trains表: 所有非主键属性直接依赖于主键(id)
--   ✅ stations表: station_name → city，无传递依赖
--   ✅ train_stations表: price直接依赖于主键组合，无部分依赖无传递依赖
--   ✅ salespeople表: 所有属性直接依赖于主键employee_code
--   ✅ tickets表: 所有非主键属性完全依赖于主键，无传递依赖
--   ✅ refund_records表: 所有属性直接依赖于主键(id)
--   ✅ backup_records表: 所有属性直接依赖于主键(id)
--
-- ============================================================================

-- 设置字符集和排序规则统一
SET NAMES utf8mb4;
SET collation_server = utf8mb4_unicode_ci;
SET character_set_server = utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS train_station_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE train_station_db;

-- ----------------------------------------------------------------------------
-- 1. 站点表 (stations)
-- ----------------------------------------------------------------------------
-- 【设计说明】
-- 站点表存储火车站的所有站点信息。
-- station_name → city 存在函数依赖，但station_name是主键，因此符合3NF。
-- city属性原子性满足（不可再分城市名）。
--
-- 【候选键】station_name (站点名称唯一)
-- 【函数依赖】station_name → city
-- 【范式】1NF ✅ 2NF ✅ 3NF ✅
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS stations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '站点ID',
    station_name VARCHAR(50) NOT NULL COMMENT '站点名称',
    city VARCHAR(50) NOT NULL COMMENT '所在城市',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_station_name (station_name),
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点信息表';

-- ----------------------------------------------------------------------------
-- 2. 车次表 (trains)
-- ----------------------------------------------------------------------------
-- 【设计说明】
-- 车次表存储车次的基本信息。
-- 所有非主键属性直接依赖于主键(id)，无部分依赖，无传递依赖。
-- train_number唯一标识一个车次。
--
-- 【候选键】train_number (车次号唯一)
-- 【函数依赖】id → 所有属性 或 train_number → 所有属性
-- 【范式】1NF ✅ 2NF ✅ 3NF ✅
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS trains (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车次ID',
    train_number VARCHAR(20) NOT NULL COMMENT '车次号',
    departure_city VARCHAR(50) NOT NULL COMMENT '出发城市',
    arrival_city VARCHAR(50) NOT NULL COMMENT '到达城市',
    total_seats INT NOT NULL COMMENT '总座位数',
    remaining_seats INT NOT NULL COMMENT '剩余座位数',
    departure_time TIME NOT NULL COMMENT '每日发车时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 0停运 1正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_train_number (train_number),
    INDEX idx_departure (departure_city),
    INDEX idx_arrival (arrival_city),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车次信息表';

-- ----------------------------------------------------------------------------
-- 3. 车次站点关联表 (train_stations)
-- ----------------------------------------------------------------------------
-- 【设计说明】
-- 存储车次经停的站点信息及对应价格。
-- price由(train_id, station_id)共同决定，嵌入此表符合业务逻辑。
--
-- 【特殊设计决策】
-- 为什么price不单独建价格表？
--   方案A(独立价格表): 相同站点组合可复用，但需增加表数量，JOIN复杂
--   方案B(嵌入本表): 查询简单，票面价直接获取，相同站点需重复价格
-- 选择方案B理由: 车票价格由"出发站+到达站+车次"共同决定（不同车次
-- 同一区间可能定价不同），嵌入train_stations表符合业务逻辑，且价格
-- 查询是高频操作。
--
-- 【候选键】(train_id, station_id)
-- 【函数依赖】(train_id, station_id) → stop_order, arrival_time, departure_time, price
-- 【范式】1NF ✅ 2NF ✅ 3NF ✅
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS train_stations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    station_id BIGINT NOT NULL COMMENT '站点ID',
    stop_order INT NOT NULL COMMENT '站点顺序',
    arrival_time TIME COMMENT '到达时间',
    departure_time TIME COMMENT '出发时间',
    price DECIMAL(10,2) NOT NULL COMMENT '从起点到该站的价格',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES stations(id) ON DELETE CASCADE,
    UNIQUE KEY uk_train_station (train_id, station_id),
    INDEX idx_train (train_id),
    INDEX idx_station (station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车次经停站点及价格表';

-- ----------------------------------------------------------------------------
-- 4. 业务员表 (salespeople)
-- ----------------------------------------------------------------------------
-- 【设计说明】
-- 存储业务员信息。
-- employee_code唯一标识一个业务员，符合3NF。
--
-- 【候选键】employee_code
-- 【函数依赖】employee_code → name, phone, id_card, status
-- 【范式】1NF ✅ 2NF ✅ 3NF ✅
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS salespeople (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '业务员ID',
    employee_code VARCHAR(20) NOT NULL COMMENT '员工工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '联系电话',
    id_card VARCHAR(18) COMMENT '身份证号',
    status TINYINT DEFAULT 1 COMMENT '状态: 0离职 1在职',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_employee_code (employee_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务员信息表';

-- ----------------------------------------------------------------------------
-- 5. 车票表 (tickets)
-- ----------------------------------------------------------------------------
-- 【设计说明】
-- 存储车票销售记录。
--
-- 【特殊设计决策】
-- 为什么tickets表不拆分为"座位表"和"票表"？
--   方案A(拆分): 座位复用更清晰，但增加JOIN查询，售票时需双重检查
--   方案B(整合): 售票原子操作，一个事务完成，数据偶有冗余（座位状态）
-- 选择方案B理由: 票务系统的核心操作是"售票"和"退票"，要求快速、原子
-- 完成。整合方案避免多表事务开销，符合OLTP系统特点。且座位冗余只是
-- 状态值（已售/未售），不造成数据不一致。
--
-- 【候选键】(train_id, sale_date, seat_number) 复合主键
-- 【函数依赖】(train_id, sale_date, seat_number) → 所有票务属性
-- 【范式】1NF ✅ 2NF ✅ 3NF ✅
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车票ID',
    train_id BIGINT NOT NULL COMMENT '车次ID',
    passenger_name VARCHAR(50) NOT NULL COMMENT '乘客姓名',
    passenger_id_card VARCHAR(18) NOT NULL COMMENT '乘客身份证号',
    departure_station_id BIGINT NOT NULL COMMENT '出发站点ID',
    arrival_station_id BIGINT NOT NULL COMMENT '到达站点ID',
    seat_number VARCHAR(10) NOT NULL COMMENT '座位号',
    price DECIMAL(10,2) NOT NULL COMMENT '票价',
    sale_date DATE NOT NULL COMMENT '乘车日期',
    sale_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '售票时间',
    salesperson_id BIGINT NOT NULL COMMENT '售票员ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0已退票 1有效',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (train_id) REFERENCES trains(id),
    FOREIGN KEY (departure_station_id) REFERENCES stations(id),
    FOREIGN KEY (arrival_station_id) REFERENCES stations(id),
    FOREIGN KEY (salesperson_id) REFERENCES salespeople(id),
    -- 复合唯一键确保同一车次同一天同一座位唯一（防止重复售票）
    UNIQUE KEY uk_train_date_seat (train_id, sale_date, seat_number),
    INDEX idx_train_date (train_id, sale_date),
    INDEX idx_salesperson (salesperson_id),
    INDEX idx_sale_date (sale_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车票销售记录表';

-- ----------------------------------------------------------------------------
-- 6. 退票记录表 (refund_records)
-- ----------------------------------------------------------------------------
-- 【设计说明】
-- 存储退票记录。
--
-- 【特殊设计决策】
-- 为什么refund_amount冗余存储而不计算？
--   方案A(计算字段): 无冗余，数据一致性好，但每次查询需计算，历史记录可能变化
--   方案B(冗余存储): 查询快，历史准确，占用少量存储
-- 选择方案B理由: 退票金额可能根据退票时间有不同计算规则（开车前/后），
-- 记录时应存储实际退票金额，保证财务可追溯性。
--
-- 【候选键】id
-- 【函数依赖】id → 所有属性
-- 【范式】1NF ✅ 2NF ✅ 3NF ✅
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS refund_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退票记录ID',
    ticket_id BIGINT NOT NULL COMMENT '原车票ID',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    refund_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '退票时间',
    operator_id BIGINT NOT NULL COMMENT '操作员ID',
    reason VARCHAR(200) COMMENT '退票原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    FOREIGN KEY (operator_id) REFERENCES salespeople(id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_refund_time (refund_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退票记录表';

-- ----------------------------------------------------------------------------
-- 7. 备份记录表 (backup_records)
-- ----------------------------------------------------------------------------
-- 【设计说明】
-- 记录系统数据备份历史。
--
-- 【候选键】id
-- 【函数依赖】id → 所有属性
-- 【范式】1NF ✅ 2NF ✅ 3NF ✅
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS backup_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '备份记录ID',
    backup_file VARCHAR(255) NOT NULL COMMENT '备份文件名',
    backup_path VARCHAR(500) NOT NULL COMMENT '备份文件路径',
    backup_size BIGINT COMMENT '备份文件大小(字节)',
    backup_type VARCHAR(20) DEFAULT 'FULL' COMMENT '备份类型: FULL完全',
    status VARCHAR(20) DEFAULT 'SUCCESS' COMMENT '状态: SUCCESS/FAILED',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_created (created_at),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据备份记录表';
