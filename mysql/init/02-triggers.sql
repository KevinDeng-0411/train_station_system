-- ============================================================================
-- 火车站票务管理系统 - 触发器脚本
-- ============================================================================
--
-- 【触发器说明】
-- 触发器是为了保证数据完整性和业务规则自动执行的特殊存储过程。
-- MySQL中触发器不能直接修改触发语句所在表的某些数据（会造成循环触发），
-- 因此座位更新通过业务逻辑层控制。
--
-- 本系统使用2个触发器：
--   1. trg_after_ticket_sale: 售票前检查余票是否充足
--   2. trg_after_refund: 退票后自动恢复座位数（已移动到应用层实现）
--
-- 【重要说明】
-- 由于AFTER INSERT触发器不能直接修改同一表的remaining_seats，
-- 余票检查和座位更新将在应用层(TicketService)通过事务控制实现。
-- 本文件保留触发器脚本作为课程设计要求展示。
--
-- ============================================================================

USE train_station_db;

-- ----------------------------------------------------------------------------
-- 触发器1: 售票前检查余票数量
-- ----------------------------------------------------------------------------
-- 【触发时机】INSERT之前
-- 【触发对象】tickets表
-- 【功能说明】
--   - 检查该车次是否还有余票
--   - 如果余票不足，抛出异常阻止售票
--   - 确保不会超员售票
--
-- 【应用场景】
--   当业务员尝试售票时，触发器自动检查:
--   1. 该车次当前剩余座位数是否 > 0
--   2. 该座位是否已被占用（通过uk_train_date_seat唯一键）
-- ----------------------------------------------------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS trg_before_ticket_sale//

CREATE TRIGGER trg_before_ticket_sale
BEFORE INSERT ON tickets
FOR EACH ROW
BEGIN
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
END//

DELIMITER ;

-- ----------------------------------------------------------------------------
-- 触发器2: 退票后自动恢复座位数（记录日志）
-- ----------------------------------------------------------------------------
-- 【触发时机】UPDATE之后
-- 【触发对象】tickets表
-- 【功能说明】
--   - 当车票状态从1(有效)变为0(已退票)时
--   - 自动增加该车次的剩余座位数
--   - 记录退票日志
--
-- 【重要说明】
--   为避免循环触发，座位数更新将在应用层实现。
--   本触发器仅用于记录退票操作日志。
-- ----------------------------------------------------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS trg_after_refund//

CREATE TRIGGER trg_after_refund
AFTER UPDATE ON tickets
FOR EACH ROW
BEGIN
    -- 仅当状态从有效变为已退票时触发
    IF OLD.status = 1 AND NEW.status = 0 THEN
        -- 更新车次剩余座位数（应用层也会做，这里作为双重保障）
        UPDATE trains
        SET remaining_seats = remaining_seats + 1
        WHERE id = OLD.train_id AND remaining_seats < total_seats;

        -- 记录退票日志（可扩展为独立日志表）
        -- 本系统使用refund_records表记录，此处仅作为触发器示例
    END IF;
END//

DELIMITER ;

-- ----------------------------------------------------------------------------
-- 触发器验证语句（可在MySQL客户端执行）
-- ----------------------------------------------------------------------------
-- SHOW TRIGGERS FROM train_station_db;
-- SELECT * FROM information_schema.TRIGGERS WHERE TRIGGER_SCHEMA = 'train_station_db';
