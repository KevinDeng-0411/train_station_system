-- 火车站票务管理系统 - 存储过程脚本
USE train_station_db;

-- 存储过程1: 统计指定车次指定日期的售票情况
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_train_sales_statistics$$
CREATE PROCEDURE sp_train_sales_statistics(IN p_train_number VARCHAR(20), IN p_sale_date DATE)
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
        LEFT JOIN tickets tk ON tr.id = tk.train_id AND tk.sale_date = p_sale_date AND tk.status = 1
        LEFT JOIN stations ds ON tk.departure_station_id = ds.id
        LEFT JOIN stations as_st ON tk.arrival_station_id = as_st.id
        WHERE tr.train_number COLLATE utf8mb4_unicode_ci = p_train_number
        GROUP BY tr.id, ds.id, as_st.id, tr.train_number, tr.remaining_seats
        ORDER BY ds.id, as_st.id;
    END IF;
END$$
DELIMITER ;

-- 存储过程2: 统计指定日期各业务员的销售收入
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_salesperson_revenue$$
CREATE PROCEDURE sp_salesperson_revenue(IN p_date DATE)
BEGIN
    SELECT
        sp.employee_code,
        sp.name AS salesperson_name,
        COUNT(tk.id) AS ticket_count,
        COALESCE(SUM(tk.price), 0) AS total_revenue
    FROM salespeople sp
    LEFT JOIN tickets tk ON sp.id = tk.salesperson_id AND DATE(tk.sale_time) = p_date AND tk.status = 1
    WHERE sp.status = 1
    GROUP BY sp.id, sp.employee_code, sp.name
    ORDER BY total_revenue DESC;
END$$
DELIMITER ;

-- 存储过程3: 获取车次详细信息（含站点和价格）
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_train_details$$
CREATE PROCEDURE sp_train_details(IN p_train_number VARCHAR(20))
BEGIN
    SELECT
        tr.train_number,
        tr.departure_city,
        tr.arrival_city,
        tr.total_seats,
        tr.remaining_seats,
        tr.departure_time,
        ts.stop_order,
        s.station_name,
        s.city,
        ts.arrival_time,
        ts.departure_time,
        ts.price
    FROM trains tr
    INNER JOIN train_stations ts ON tr.id = ts.train_id
    INNER JOIN stations s ON ts.station_id = s.id
    WHERE tr.train_number COLLATE utf8mb4_unicode_ci = p_train_number
    ORDER BY ts.stop_order;
END$$
DELIMITER ;
