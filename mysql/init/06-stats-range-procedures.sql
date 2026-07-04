-- ============================================================================
-- 火车站票务管理系统 - 日期范围统计存储过程
-- 用于支持"最近 7 天"等范围统计
-- ============================================================================
USE train_station_db;

DELIMITER $$

-- 车次售票明细（按日期范围）
DROP PROCEDURE IF EXISTS sp_train_sales_by_range$$
CREATE PROCEDURE sp_train_sales_by_range(
    IN p_train_number VARCHAR(20),
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    DECLARE v_train_id BIGINT;
    SELECT id INTO v_train_id FROM trains WHERE train_number COLLATE utf8mb4_unicode_ci = p_train_number;
    IF v_train_id IS NULL THEN
        SELECT 'Train not found' AS error_message;
    ELSE
        SELECT
            tr.train_number,
            ds.station_name AS departure_station,
            as_st.station_name AS arrival_station,
            COUNT(tk.id) AS ticket_count,
            COALESCE(SUM(tk.price), 0) AS total_amount,
            tr.remaining_seats
        FROM trains tr
        LEFT JOIN tickets tk ON tr.id = tk.train_id
            AND tk.sale_date BETWEEN p_start_date AND p_end_date
            AND tk.status = 1
        LEFT JOIN stations ds ON tk.departure_station_id = ds.id
        LEFT JOIN stations as_st ON tk.arrival_station_id = as_st.id
        WHERE tr.train_number COLLATE utf8mb4_unicode_ci = p_train_number
        GROUP BY tr.id, ds.id, as_st.id, tr.train_number, tr.remaining_seats
        ORDER BY ds.id, as_st.id;
    END IF;
END$$

-- 业务员收入（按日期范围）
DROP PROCEDURE IF EXISTS sp_salesperson_revenue_by_range$$
CREATE PROCEDURE sp_salesperson_revenue_by_range(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT
        sp.employee_code,
        sp.name AS salesperson_name,
        COUNT(tk.id) AS ticket_count,
        COALESCE(SUM(tk.price), 0) AS total_revenue
    FROM salespeople sp
    LEFT JOIN tickets tk ON sp.id = tk.salesperson_id
        AND DATE(tk.sale_time) BETWEEN p_start_date AND p_end_date
        AND tk.status = 1
    WHERE sp.status = 1
    GROUP BY sp.id, sp.employee_code, sp.name
    ORDER BY total_revenue DESC;
END$$

DELIMITER ;
