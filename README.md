# 火车站票务管理系统 🚆

> 数据库课程设计 · Spring Boot + Vue 3 + MySQL 8.0 · Docker一键部署

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![Vue](https://img.shields.io/badge/Vue-3.4-42b883?logo=vuedotjs)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 项目简介

一个完整的火车站票务管理系统，实现了车票销售、退票、统计、备份等核心功能。系统采用前后端分离架构，使用Docker容器化部署，开箱即用。

### ✨ 项目亮点

- 🎨 **现代化UI设计** - 玻璃态(Glassmorphism)界面 + 青蓝渐变主题
- 📊 **完整的数据库设计** - 严格遵循第三范式(3NF)，并提供设计文档
- 🐳 **一键Docker部署** - 无需本地安装MySQL/Java/Node.js
- 🔒 **业务规则自动化** - 触发器自动维护座位数，确保数据一致性
- 📈 **存储过程统计分析** - 支持车次售票和业务员收入统计
- 💾 **数据备份恢复** - 一键创建备份，按需恢复

---

## 🎯 功能特性

### 1. 车次管理
- ✅ 车次的增删改查
- ✅ 车次号、出发/到达城市、发车时间管理
- ✅ 总座位数与余票实时显示

### 2. 车次站点与价格管理
- ✅ 配置车次经停站点
- ✅ 各区间票价独立管理
- ✅ 站点顺序与到达/出发时间

### 3. 业务员管理
- ✅ 业务员档案（工号、姓名、联系方式）
- ✅ 在职/离职状态管理
- ✅ 添加自动填充创建时间

### 4. 车票销售 🚄
- ✅ 智能选车次、站点、座位
- ✅ **触发器自动检查余票**（防止超员）
- ✅ **触发器自动减少座位数**
- ✅ 自动分配座位号
- ✅ 自动生成销售记录

### 5. 车票退票 🔄
- ✅ 自动验证退票资格
- ✅ **触发器自动恢复座位数**
- ✅ 自动生成退款记录
- ✅ 记录退票原因与操作员

### 6. 存储过程统计 📊
- ✅ **sp_train_sales_statistics** - 指定车次指定日期售票情况
- ✅ **sp_salesperson_revenue** - 业务员销售收入统计

### 7. 数据备份与恢复 💾
- ✅ 完整数据库备份（mysqldump）
- ✅ 历史备份记录查看
- ✅ 一键恢复指定备份

---

## 🛠️ 技术栈

### 后端
| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2 | 核心框架 |
| Java | 21 | 编程语言 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL Connector | 8.0 | 数据库驱动 |
| HikariCP | - | 数据库连接池 |
| MySQL Client | - | 备份恢复 |

### 前端
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4 | 前端框架 |
| Vite | 5.0 | 构建工具 |
| Element Plus | 2.5 | UI组件库 |
| Pinia | 2.1 | 状态管理 |
| Axios | 1.6 | HTTP客户端 |
| Vue Router | 4.2 | 路由管理 |

### 数据库
- MySQL 8.0
- 字符集：utf8mb4 / utf8mb4_unicode_ci
- 7张表，2个触发器，3个存储过程

### 部署
- Docker / Docker Compose
- 单机一键启动，零环境依赖

---

## 🚀 快速开始

### 前置要求
只需要安装一个：**Docker Desktop** (或 Docker + Docker Compose)
- [Mac](https://docs.docker.com/desktop/install/mac-install/)
- [Windows](https://docs.docker.com/desktop/install/windows-install/)
- [Linux](https://docs.docker.com/desktop/install/linux-install/)

### 启动步骤

```bash
# 1. 进入项目目录
cd train-station-system

# 2. 一键启动所有服务
docker-compose up -d --build

# 3. 查看服务状态
docker-compose ps

# 4. 访问应用
# 前端: http://localhost
# 后端API: http://localhost:8080/api
# MySQL: localhost:3306
```

首次启动需要 3-5 分钟（下载Maven依赖和npm包）。

### 停止服务
```bash
docker-compose down            # 停止容器
docker-compose down -v         # 停止并清理数据卷
```

---

## 📂 项目结构

```
train-station-system/
├── docker-compose.yml              # Docker容器编排
├── README.md                       # 本文件
├── .gitignore                      # Git忽略配置
│
├── mysql/                          # MySQL数据初始化
│   ├── Dockerfile
│   └── init/
│       ├── 01-schema.sql          # 数据库表结构（3NF）
│       ├── 02-triggers.sql        # 触发器脚本
│       ├── 03-procedures.sql      # 存储过程脚本
│       └── 04-sample-data.sql     # 示例数据
│
├── backend/                        # Spring Boot后端
│   ├── Dockerfile
│   ├── pom.xml                     # Maven依赖
│   └── src/main/
│       ├── java/com/trainstation/
│       │   ├── TrainStationApplication.java
│       │   ├── config/             # 配置类（含MetaObjectHandler）
│       │   ├── controller/         # 7个REST控制器
│       │   ├── service/            # 业务逻辑层
│       │   ├── mapper/             # 数据访问层（含JOIN查询）
│       │   ├── entity/             # 数据库实体
│       │   ├── dto/                # 数据传输对象
│       │   └── exception/          # 全局异常处理
│       └── resources/
│           └── application.yml
│
└── frontend/                       # Vue 3前端
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── main.js
        ├── App.vue                # 全局布局（玻璃态导航）
        ├── api/                   # API封装
        ├── router/                # 路由配置
        └── views/                 # 7个页面
```

---

## 🗄️ 数据库设计

### ER图概览

```
trains ─────┐
            │
            ├──< train_stations >──── stations
            │
            ├──< tickets <────────── salespeople
            │       │
            │       └──< refund_records
            │
└──< backup_records
```

### 核心表结构

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `trains` | 车次信息 | id, train_number, total_seats, remaining_seats |
| `stations` | 站点信息 | id, station_name, city |
| `train_stations` | 车次站点+价格 | train_id, station_id, price |
| `salespeople` | 业务员 | id, employee_code, name, status |
| `tickets` | 车票记录 | train_id, sale_date, seat_number, status |
| `refund_records` | 退票记录 | ticket_id, refund_amount, reason |
| `backup_records` | 备份历史 | backup_file, status |

### 范式分析

设计严格遵循3NF：

| 表 | 候选键 | 函数依赖 | 范式 |
|----|--------|----------|------|
| trains | train_number | 无传递依赖 | 3NF ✅ |
| stations | station_name | station_name → city | 3NF ✅ |
| train_stations | (train_id, station_id) | 组合主键直接依赖 | 3NF ✅ |
| salespeople | employee_code | 无传递依赖 | 3NF ✅ |
| tickets | (train_id, sale_date, seat_number) | 完全函数依赖 | 3NF ✅ |

详见 [mysql/init/01-schema.sql](mysql/init/01-schema.sql) 顶部注释的设计决策说明。

### 触发器

| 触发器 | 时机 | 作用 |
|--------|------|------|
| `trg_before_ticket_sale` | BEFORE INSERT | 检查余票、座位占用 |
| `trg_after_refund` | AFTER UPDATE | 自动恢复座位数 |

### 存储过程

| 存储过程 | 参数 | 用途 |
|----------|------|------|
| `sp_train_sales_statistics` | 车次号, 日期 | 统计车次售票明细 |
| `sp_salesperson_revenue` | 日期 | 业务员销售收入排序 |
| `sp_train_details` | 车次号 | 车次详细信息（含站点价格） |

---

## 📡 API 接口

### 车次管理
```
GET    /api/trains         车次列表
GET    /api/trains/page   分页查询
GET    /api/trains/{id}    车次详情
POST   /api/trains         新增车次
PUT    /api/trains/{id}    更新车次
DELETE /api/trains/{id}    删除车次
```

### 售票与退票
```
POST   /api/tickets/sale              售票
POST   /api/tickets/{id}/refund       退票
GET    /api/tickets                  车票列表（关联查询）
GET    /api/tickets/check-seat       检查座位可用性
```

### 统计接口（调用存储过程）
```
GET /api/statistics/trains/{trainNumber}/date/{date}    车次统计
GET /api/statistics/salespeople/date/{date}            业务员统计
```

### 数据备份
```
POST   /api/backup              创建备份
GET    /api/backup              备份列表
POST   /api/backup/{id}/restore 恢复指定备份
DELETE /api/backup/{id}        删除备份
```

---

## 🎨 设计特色

### 视觉设计
- **配色**：青蓝色系 (`#0891B2` → `#06B6D4`) 主色，搭配玻璃态白色
- **背景**：动态渐变 + 浮动光球（20秒循环动画）
- **导航**：玻璃态顶部栏 + backdrop-filter 模糊效果
- **火车头SVG图标**：自绘品牌标识
- **实时时间**：JetBrains Mono字体显示秒级时间
- **系统状态指示器**：绿色脉冲点

### 交互动效
- 路由切换 fade + slide 过渡
- 按钮 hover 渐变提升
- 表格行 hover 浅色高亮
- 输入框聚焦青色边框
- 分页激活页渐变

---

## 🧪 功能测试

```bash
# 车票销售测试
curl -X POST http://localhost:8080/api/tickets/sale \
  -H "Content-Type: application/json" \
  -d '{"trainId":1,"departureStationId":1,"arrivalStationId":2,
       "seatNumber":"1A","price":88.00,"saleDate":"2026-07-02",
       "passengerName":"张三","passengerIdCard":"110101199001011234",
       "salespersonId":1}'

# 车次统计
curl http://localhost:8080/api/statistics/trains/G101/date/2026-07-02

# 业务员统计
curl http://localhost:8080/api/statistics/salespeople/date/2026-07-02
```

---

## 📝 课程设计亮点

1. **范式设计严谨** - 7张表全部满足3NF，详细说明设计决策理由
2. **业务规则自动化** - 触发器自动维护座位数，无需应用层干预
3. **存储过程封装业务** - 复杂统计查询在数据库层完成
4. **完整的备份恢复机制** - 生产级数据安全保障
5. **现代前端设计** - Glassmorphism风格，区分常见模板化UI
6. **Docker容器化** - 工程化交付，一键启动

---

## 👥 作者

**KevinDeng** · 数据库课程设计

---

## 📄 License

MIT License © 2026

## 🙏 致谢

- Spring Boot / MyBatis-Plus / Element Plus 社区
- MySQL 文档
- Glassmorphism设计风格启发
