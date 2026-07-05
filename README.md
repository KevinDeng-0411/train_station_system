# 火车站票务管理系统 🚆

> 数据库课程设计 · Spring Boot + Vue 3 + MySQL 8.0 · Docker一键部署

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?logo=springboot)
![Vue](https://img.shields.io/badge/Vue-3.4-42b883?logo=vuedotjs)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)
![ECharts](https://img.shields.io/badge/ECharts-5.5-orange?logo=apacheecharts)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 项目简介

一个完整的火车站票务管理系统，实现了车票销售、退票、统计、备份等核心功能。系统采用前后端分离架构，使用Docker容器化部署，开箱即用。**前端使用 ui-ux-pro-max 设计系统重构，建立了完整的设计 token 体系并支持深色模式。**

### ✨ 项目亮点

- 🎨 **设计系统驱动 UI** - 三层 token 架构（Primitive → Semantic → Component）+ 完整 design-tokens.css
- 🌙 **深色模式** - 一键切换 + localStorage 持久化 + 全套 Element Plus 适配
- 📊 **商业仪表盘** - 4个KPI卡片 + 4个ECharts图表（折线、饼图、柱状、玫瑰）
- ✨ **微动效** - 数字滚动入场 / 表格行 hover 抬升 / 座位图脉冲 / KPI 卡光晕
- 📐 **完整3NF数据库设计** - 严格遵循第三范式，详细说明见 [DATABASE_DESIGN.md](DATABASE_DESIGN.md)
- 🐳 **一键Docker部署** - 含6个工具（MySQL + 后端 + 前端 + phpMyAdmin + Adminer + SchemaSpy）
- 🔒 **业务规则自动化** - 触发器自动维护座位数，确保数据一致性
- 📈 **存储过程统计分析** - 5个存储过程（含日期范围版）
- 💾 **数据备份恢复** - 一键创建备份，按需恢复

---

## 🌐 服务总览

启动后打开 **http://localhost** 即可使用。所有服务端口：

| 服务 | 地址 | 凭据 | 用途 |
|------|------|------|------|
| 🚂 **主应用** | **http://localhost** | - | 火车站票务系统前端 |
| 📡 后端API | http://localhost:8080/api | - | REST 接口 |
| 🗄 MySQL | localhost:3306 | `root` / `root123456` | 数据库 |
| 🔥 **phpMyAdmin** | **http://localhost:8082** | 容器间用 `mysql` 作主机 | ⭐ 数据浏览 + **交互式ER图Designer** |
| 📋 Adminer | http://localhost:8081 | 同上 | 轻量级数据库浏览 |
| 📊 SchemaSpy 报告 | `docs/erd/index.html` | - | 自动生成的ER图报告 |

> 💡 **关键**：在phpMyAdmin / Adminer 登录时，**服务器字段填 `mysql`**（容器名），不是 `localhost`！

---

## 🎯 功能特性

### 1. 车次管理 🚆
- ✅ 车次的增删改查
- ✅ 车次号、出发/到达城市、发车时间管理
- ✅ 总座位数与余票实时显示
- ✅ **新增车次自动同步始发/终到站记录**

### 2. 车次站点与价格管理 💰
- ✅ 配置车次经停站点
- ✅ **区间票价 = 到达站累计价 − 出发站累计价**（正确计算）
- ✅ 站点顺序与到达/出发时间
- ✅ **TrainDetail 弹窗**：可视化经停站管理

### 3. 业务员管理
- ✅ 业务员档案（工号、姓名、联系方式）
- ✅ 在职/离职状态管理
- ✅ 自动填充创建时间（MetaObjectHandler）

### 4. 车票销售 🎫
- ✅ 智能选车次、站点、座位
- ✅ **座位图可视化**（A/B/C 过道 D/F，5列布局，脉冲呼吸选中态）
- ✅ **触发器自动检查余票**（防止超员）
- ✅ **触发器自动减少座位数**
- ✅ 自动分配座位号
- ✅ 自动生成销售记录

### 5. 车票退票 🔄
- ✅ 自动验证退票资格
- ✅ **触发器自动恢复座位数**
- ✅ 自动生成退款记录
- ✅ 记录退票原因与操作员

### 6. 统计报表 📊（**商业仪表盘风格**）
- ✅ **4个KPI卡片** - 今日售票 / 今日收入 / 累计收入 / 在售车次（带数字滚动动画）
- ✅ **最近7天售票趋势折线图** - 双轴（售票数 + 收入）
- ✅ **业务员收入占比饼图** - 环形图
- ✅ **车次收入TOP10柱状图** - 水平渐变柱
- ✅ **出发站热门玫瑰图** - 南丁格尔玫瑰图
- ✅ 表格：车次售票明细 + 业务员收入排行（**金/银/铜排名徽章**）
- ✅ **日期范围筛选** - 默认最近 7 天，含"今天/昨天/7天/30天/本月"快捷选项
- ✅ 富文本Tooltip：玻璃态阴影 + 色块 + 图标 + 完整数据展示

### 7. 数据备份与恢复 💾
- ✅ 完整数据库备份（mysqldump）
- ✅ 历史备份记录查看
- ✅ **文件大小格式化**（KB/MB）
- ✅ 一键恢复指定备份

### 8. 通用体验增强 ✨
- ✅ **深色模式**（🌙 切换按钮 + localStorage 持久化）
- ✅ **响应式布局**（桌面/平板/手机断点）
- ✅ **空状态组件**（EmptyState：圆环旋转 + 浮动插画）
- ✅ **骨架屏加载**（SkeletonBlock：shimmer 流动）
- ✅ **JetBrains Mono** 等宽字体（数字/代码/时间）
- ✅ **表单可访问性**（深色 label + 青蓝边框 + 12px 内边距）

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
| **ECharts** | 5.5 | 数据可视化 |
| **vue-echarts** | 7.0 | ECharts Vue集成 |
| Pinia | 2.1 | 状态管理 |
| Axios | 1.6 | HTTP客户端 |
| Vue Router | 4.2 | 路由管理 |
| **JetBrains Mono** | 4.0+ | 等宽字体（Google Fonts CDN）|

### 设计系统
| 资源 | 路径 | 规范 |
|------|------|------|
| **Design Tokens** | `frontend/src/assets/design-tokens.css` | 三层架构（Primitive/Semantic/Component）|
| **Utility Styles** | `frontend/src/assets/shared-styles.css` | 工具类（layout/text/animation）|
| **深色模式** | `[data-theme="dark"]` 覆盖 | 完整适配 |
| **设计参考** | ui-ux-pro-max 规范 | 行业标准 |

### 数据库
- MySQL 8.0
- 字符集：utf8mb4 / utf8mb4_unicode_ci
- 7张业务表，2个触发器，5个存储过程
- 30个索引（P0/P1 性能优化 + 基础约束）

### 工具链（Docker化）
| 工具 | 端口 | 用途 |
|------|------|------|
| phpMyAdmin | 8082 | 数据浏览 + 交互式ER图（Designer功能） |
| Adminer | 8081 | 单PHP文件轻量数据库管理 |
| SchemaSpy | 8084 | 自动生成ER图报告 |

### 部署
- Docker / Docker Compose
- 单机一键启动，零环境依赖

---

## 🚀 快速开始

### 前置要求
只需要安装一个：**Docker Desktop**
- [Mac](https://docs.docker.com/desktop/install/mac-install/)
- [Windows](https://docs.docker.com/desktop/install/windows-install/)
- [Linux](https://docs.docker.com/desktop/install/linux-install/)

### 启动步骤

```bash
# 1. 进入项目目录
cd train-station-system

# 2. 一键启动所有服务（含6个Docker容器）
docker-compose up -d --build

# 3. 查看服务状态
docker-compose ps

# 4. 等待所有容器变为 healthy（约1-2分钟）
# train_mysql       healthy
# train_backend     Up
# train_frontend    Up
# train_adminer     Up
# train_phpmyadmin  Up
# train_schemaspy   Up
```

首次启动需要 3-5 分钟（下载Maven依赖和npm包）。

### 立即访问

打开浏览器访问：

| 用途 | 地址 |
|------|------|
| 🚂 **使用系统** | **http://localhost** |
| 🌙 **切换深色模式** | 右上角 🌙 按钮 |
| 🔍 **查看E-R图（推荐）** | **http://localhost:8082** (phpMyAdmin Designer) |
| 📊 浏览ER报告 | 打开 `docs/erd/index.html` |
| 📋 快速数据浏览 | http://localhost:8081 (Adminer) |

### 停止服务

```bash
docker-compose down            # 停止容器
docker-compose down -v         # 停止并清理数据卷（重置数据）
```

---

## 📂 项目结构

```
train-station-system/
├── docker-compose.yml              # 容器编排（6个服务）
├── README.md                       # 本文件
├── DATABASE_DESIGN.md              # 数据库设计说明文档（验收用）
├── .gitignore                      # Git忽略配置
│
├── mysql/                          # MySQL数据初始化
│   ├── Dockerfile
│   └── init/
│       ├── 01-schema.sql          # 数据库表结构（3NF）
│       ├── 02-triggers.sql        # 触发器脚本
│       ├── 03-procedures.sql      # 存储过程脚本
│       ├── 04-sample-data.sql     # 示例数据
│       ├── 05-performance-indexes.sql  # P0/P1 性能索引
│       └── 06-stats-range-procedures.sql # 日期范围统计
│
├── backend/                        # Spring Boot后端
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/trainstation/
│       │   ├── config/             # 配置类（含MetaObjectHandler自动填充）
│       │   ├── controller/         # REST 控制器（8个）
│       │   ├── service/            # 业务逻辑层
│       │   ├── mapper/             # 数据访问层（含JOIN查询）
│       │   ├── entity/             # 数据库实体
│       │   ├── dto/                # 数据传输对象
│       │   └── exception/          # 全局异常处理
│       └── resources/
│           └── application.yml
│
├── frontend/                       # Vue 3前端
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json                 # echarts + vue-echarts 依赖
│   ├── vite.config.js
│   ├── index.html                   # JetBrains Mono CDN
│   └── src/
│       ├── main.js                 # ECharts按需引入 + design tokens
│       ├── App.vue                  # 全局布局 + 深色模式切换
│       ├── assets/
│       │   ├── design-tokens.css   # 设计系统 token（核心）
│       │   └── shared-styles.css   # 工具类 + 动画
│       ├── api/index.js             # Axios封装
│       ├── router/
│       ├── views/                   # 7个页面（含仪表盘）
│       ├── components/
│       │   ├── EmptyState.vue       # 空状态组件
│       │   ├── SkeletonBlock.vue    # 骨架屏组件
│       │   └── chart/               # 5个ECharts组件 + 座位图
│       └── utils/chartTheme.js      # 统一ECharts主题
│
├── docs/erd/                        # SchemaSpy自动生成（.gitignore）
│   └── index.html                    # ER图报告
│
└── ACCEPTANCE.md / COURSE_REPORT.docx  # 验收文档（.gitignore，本地保留）
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

### 范式分析（全部满足3NF / BCNF）

| 表 | 候选键 | 范式 |
|----|--------|------|
| trains | train_number | 3NF ✅ |
| stations | station_name | 3NF ✅ |
| train_stations | (train_id, station_id) | 3NF ✅ |
| salespeople | employee_code | 3NF ✅ |
| tickets | (train_id, sale_date, seat_number) | 3NF ✅ |

**详细设计**：参考 [DATABASE_DESIGN.md](DATABASE_DESIGN.md)（包含函数依赖分析、设计决策理由）

### 触发器

| 触发器 | 时机 | 作用 |
|--------|------|------|
| `trg_before_ticket_sale` | BEFORE INSERT | 检查余票、座位占用 |
| `trg_after_refund` | AFTER UPDATE | 自动恢复座位数 |

### 存储过程（5 个）

| 存储过程 | 参数 | 用途 |
|----------|------|------|
| `sp_train_sales_statistics` | 车次号, 日期 | 统计车次售票明细（单日）|
| `sp_salesperson_revenue` | 日期 | 业务员销售收入排序（单日）|
| `sp_train_details` | 车次号 | 车次详细信息（含站点价格）|
| `sp_train_sales_by_range` | 车次号, 起始日, 结束日 | **车次售票明细（日期范围）**|
| `sp_salesperson_revenue_by_range` | 起始日, 结束日 | **业务员收入（日期范围）**|

### 索引策略（30 个）

| 类型 | 数量 | 优化目标 |
|------|------|----------|
| PRIMARY KEY | 7 | 实体完整性 |
| UNIQUE | 5 | 用户自定义完整性 |
| 基础 INDEX | 10 | 常规查询加速 |
| 性能优化复合索引 | 8 | P0/P1 关键查询（10x~15x 提升）|

---

## 📡 API 接口

### 车次管理
```
GET    /api/trains         车次列表
GET    /api/trains/page   分页查询
GET    /api/trains/{id}    车次详情
POST   /api/trains         新增车次（自动同步经停站）
POST   /api/trains/with-stations  新增车次 + 经停站
PUT    /api/trains/{id}    更新车次
DELETE /api/trains/{id}    删除车次
```

### 经停站管理
```
GET    /api/trains/{id}/stations              车次经停站列表
POST   /api/trains/{trainId}/stations         添加经停站
PUT    /api/trains/{trainId}/stations/{stationId}/price  更新票价
PUT    /api/trains/{trainId}/stations/{stationId}/order  更新顺序
DELETE /api/trains/{trainId}/stations/{stationId}          删除经停站
```

### 售票与退票
```
POST   /api/tickets/sale              售票
POST   /api/tickets/{id}/refund       退票
GET    /api/tickets                  车票列表（关联查询）
GET    /api/tickets/sold-seats       已售座位查询
```

### 统计接口
```
# 传统表格统计（存储过程 - 单日）
GET /api/statistics/trains/{trainNumber}/date/{date}       车次统计（单日）
GET /api/statistics/salespeople/date/{date}               业务员统计（单日）

# 仪表盘接口（日期范围）
GET /api/statistics/trains/{trainNumber}/range             车次统计（日期范围）
GET /api/statistics/salespeople/range                      业务员统计（日期范围）

# KPI 仪表盘
GET /api/statistics/kpi              4个KPI卡片数据
GET /api/statistics/trend             最近7天售票趋势
GET /api/statistics/train-top?limit=10     车次收入TOP10
GET /api/statistics/station-popular?type=departure&limit=8  站点热门排行
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

### 设计系统（ui-ux-pro-max 规范）

| 层级 | 内容 | 文件 |
|------|------|------|
| **Primitive** | 原始色板（cyan/emerald/amber/red/slate）+ 间距 + 圆角 + 阴影 | `design-tokens.css` |
| **Semantic** | 主色/语义色/文字色阶/背景/玻璃态 + 间距/阴影/动效别名 | `design-tokens.css` |
| **Component** | 按钮/卡片/表格/对话框/输入框/标签/导航/KPI/图表卡 专用 | `design-tokens.css` |
| **Utility** | `.w-full` / `.text-money` / `.route-arrow` / `.hint-banner` | `shared-styles.css` |

### 视觉设计
- **配色**：青蓝色系（`#0891B2` → `#06B6D4` → `#22D3EE`）主色，搭配玻璃态白色
- **背景**：动态渐变 + 浮动光球（20秒循环动画）
- **导航**：玻璃态顶部栏 + backdrop-filter 模糊效果
- **火车头SVG图标**：自绘品牌标识
- **实时时间**：JetBrains Mono字体显示秒级时间
- **系统状态指示器**：绿色脉冲点
- **深色模式**：完整适配，一键切换 + localStorage 持久化

### 仪表盘设计
- **玻璃态卡片**：所有KPI和图表卡片采用 backdrop-filter blur
- **ECharts主题**：统一的 8色调色板
- **富文本Tooltip**：色块 + 图标 + 完整数据展示
- **响应式**：vue-echarts autoresize 自动适配窗口
- **金/银/铜排名徽章**：车次/业务员收入排行

### 交互动效（Phase 4-6 新增）
- **KPI 数字滚动**：`@keyframes countUp` 弹性入场
- **表格行 hover**：`translateX(2px)` 微动效（覆盖全部 7 个视图）
- **KPI 卡 hover**：`::after` 渐变光晕
- **座位图选中**：`seatPulse` 呼吸动画
- **空状态插画**：`floatY` 浮动 + `spin` 圆环旋转
- **骨架屏加载**：`shimmer` 渐变流动
- **路由切换**：fade + slide 过渡
- **按钮/输入框 hover**：渐变 + 抬升
- **页面切换**：fade + translateY 动画

---

## 🧪 功能测试

```bash
# === 售票测试 ===
curl -X POST http://localhost:8080/api/tickets/sale \
  -H "Content-Type: application/json" \
  -d '{"trainId":1,"departureStationId":1,"arrivalStationId":2,
       "seatNumber":"1A","price":88.00,"saleDate":"2026-07-04",
       "passengerName":"张三","passengerIdCard":"110101199001011234",
       "salespersonId":1}'

# === 仪表盘 ===
curl http://localhost:8080/api/statistics/kpi
curl http://localhost:8080/api/statistics/trend
curl "http://localhost:8080/api/statistics/train-top?limit=10"
curl "http://localhost:8080/api/statistics/station-popular?type=departure&limit=8"

# === 范围统计（默认最近7天）===
curl "http://localhost:8080/api/statistics/salespeople/range"
curl "http://localhost:8080/api/statistics/salespeople/range?startDate=2026-07-01&endDate=2026-07-07"

# === 单日统计（存储过程）===
curl http://localhost:8080/api/statistics/trains/G101/date/2026-07-04
curl http://localhost:8080/api/statistics/salespeople/date/2026-07-04
```

---

## 📝 课程设计亮点

1. **范式设计严谨** - 7张表全部满足3NF/BCNF，详细说明设计决策理由
2. **业务规则自动化** - 触发器自动维护座位数，无需应用层干预
3. **存储过程封装业务** - 5个存储过程（含日期范围版），复杂统计查询在数据库层完成
4. **完整的备份恢复机制** - 生产级数据安全保障
5. **现代前端设计** - Glassmorphism风格 + 深色模式 + 设计系统
6. **可视化统计仪表盘** - 4 KPI + 4图表 + 日期范围筛选
7. **微动效设计** - 数字滚动/表格 hover/座位脉冲/KPI 光晕
8. **Docker容器化** - 工程化交付，含数据库可视化管理工具
9. **完整文档** - README + DATABASE_DESIGN + ACCEPTANCE + COURSE_REPORT 四重支持

---

## 📚 相关文档

| 文档 | 路径 | 用途 |
|------|------|------|
| 📘 数据库设计说明书 | [DATABASE_DESIGN.md](DATABASE_DESIGN.md) | 课程设计验收核心 |
| 📗 数据库脚本 | [mysql/init/](mysql/init/) | 6个 SQL 初始化文件 |
| 📙 ER图报告 | [docs/erd/index.html](docs/erd/index.html) | SchemaSpy 自动生成 |
| 📕 验收对照表 | `ACCEPTANCE.md` (本地) | 验收场景对照 |
| 📒 课程设计报告 | `COURSE_REPORT.docx` (本地) | 课程设计报告 Word 版 |

> 💡 验收文档（ACCEPTANCE.md / COURSE_REPORT.docx）已加入 `.gitignore`，由学生本地保管，不进入版本控制。

---

## 🆚 Docker服务与登录凭据速查

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
数据库服务
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
主机：localhost:3306
容器内通信：mysql:3306
用户名：root
密  码：root123456
数据库：train_station_db

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
phpMyAdmin  →  http://localhost:8082
Adminer     →  http://localhost:8081
SchemaSpy   →  http://localhost:8084
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
登录方式：
  - 服务器/主机 填 "mysql"（不是localhost）
  - 用户名     root
  - 密  码     root123456
  - 数据库     train_station_db

🔥 phpMyAdmin 的"设计器"标签 = 交互式E-R图
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 👥 作者

**KevinDeng** · 数据库课程设计

---

## 📄 License

MIT License © 2026

---

## 🙏 致谢

- Spring Boot / MyBatis-Plus / Element Plus 社区
- MySQL 官方文档
- Vue 3 + ECharts 官方
- Glassmorphism 设计风格启发
- phpMyAdmin / SchemaSpy 开源项目
- **ui-ux-pro-max 设计系统规范**
