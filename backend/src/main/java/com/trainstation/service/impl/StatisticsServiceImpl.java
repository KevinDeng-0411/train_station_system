package com.trainstation.service.impl;

import com.trainstation.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getTrainSalesStatistics(String trainNumber, String saleDate) {
        String sql = "CALL sp_train_sales_statistics(?, ?)";
        CallableStatementCallback<List<Map<String, Object>>> callback = cs -> {
            cs.setString(1, trainNumber);
            cs.setString(2, saleDate);
            boolean hasResultSet = cs.execute();
            if (hasResultSet) {
                return resultSetToList(cs.getResultSet());
            }
            return new ArrayList<>();
        };
        return jdbcTemplate.execute(sql, callback);
    }

    @Override
    public List<Map<String, Object>> getSalespersonRevenue(String date) {
        String sql = "CALL sp_salesperson_revenue(?)";
        CallableStatementCallback<List<Map<String, Object>>> callback = cs -> {
            cs.setString(1, date);
            boolean hasResultSet = cs.execute();
            if (hasResultSet) {
                return resultSetToList(cs.getResultSet());
            }
            return new ArrayList<>();
        };
        return jdbcTemplate.execute(sql, callback);
    }

    @Override
    public List<Map<String, Object>> getTrainSalesByRange(String trainNumber, String startDate, String endDate) {
        String sql = "CALL sp_train_sales_by_range(?, ?, ?)";
        CallableStatementCallback<List<Map<String, Object>>> callback = cs -> {
            cs.setString(1, trainNumber);
            cs.setString(2, startDate);
            cs.setString(3, endDate);
            boolean hasResultSet = cs.execute();
            if (hasResultSet) {
                return resultSetToList(cs.getResultSet());
            }
            return new ArrayList<>();
        };
        return jdbcTemplate.execute(sql, callback);
    }

    @Override
    public List<Map<String, Object>> getSalespersonRevenueByRange(String startDate, String endDate) {
        String sql = "CALL sp_salesperson_revenue_by_range(?, ?)";
        CallableStatementCallback<List<Map<String, Object>>> callback = cs -> {
            cs.setString(1, startDate);
            cs.setString(2, endDate);
            boolean hasResultSet = cs.execute();
            if (hasResultSet) {
                return resultSetToList(cs.getResultSet());
            }
            return new ArrayList<>();
        };
        return jdbcTemplate.execute(sql, callback);
    }

    // ========== 仪表盘新增 ==========

    @Override
    public Map<String, Object> getKpi() {
        Map<String, Object> kpi = new HashMap<>();

        // 今日售票数
        Long todayTickets = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM tickets WHERE DATE(sale_time) = CURDATE() AND status = 1",
            Long.class
        );
        kpi.put("todayTickets", todayTickets == null ? 0 : todayTickets);

        // 今日收入
        Double todayRevenue = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(price), 0) FROM tickets WHERE DATE(sale_time) = CURDATE() AND status = 1",
            Double.class
        );
        kpi.put("todayRevenue", todayRevenue == null ? 0 : todayRevenue);

        // 总收入
        Double totalRevenue = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(price), 0) FROM tickets WHERE status = 1",
            Double.class
        );
        kpi.put("totalRevenue", totalRevenue == null ? 0 : totalRevenue);

        // 在售车次
        Long activeTrains = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM trains WHERE status = 1",
            Long.class
        );
        kpi.put("activeTrains", activeTrains == null ? 0 : activeTrains);

        // 在职业务员
        Long activeSalespeople = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM salespeople WHERE status = 1",
            Long.class
        );
        kpi.put("activeSalespeople", activeSalespeople == null ? 0 : activeSalespeople);

        // 票务总数（已售出，含已退）
        Long totalTickets = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM tickets",
            Long.class
        );
        kpi.put("totalTickets", totalTickets == null ? 0 : totalTickets);

        return kpi;
    }

    @Override
    public Map<String, Object> get7DayTrend() {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> ticketCounts = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        // 生成最近7天日期（包括今天）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i).format(formatter));
        }

        // 一次查询获取所有数据
        String sql = "SELECT DATE(sale_time) AS d, COUNT(*) AS cnt, COALESCE(SUM(price), 0) AS rev " +
                     "FROM tickets WHERE status = 1 AND sale_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                     "GROUP BY DATE(sale_time)";
        Map<String, double[]> dayMap = new HashMap<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : rows) {
                Object dObj = row.get("d");
                if (dObj != null) {
                    String d = dObj.toString();
                    double cnt = ((Number) row.get("cnt")).doubleValue();
                    double rev = ((Number) row.get("rev")).doubleValue();
                    dayMap.put(d, new double[]{cnt, rev});
                }
            }
        } catch (Exception ignored) {}

        // 填充数组，缺失的日期补0
        for (String d : dates) {
            double[] v = dayMap.getOrDefault(d, new double[]{0, 0});
            ticketCounts.add((long) v[0]);
            revenues.add(v[1]);
        }

        result.put("dates", dates);
        result.put("ticketCounts", ticketCounts);
        result.put("revenues", revenues);
        return result;
    }

    @Override
    public List<Map<String, Object>> getTrainTopRevenue(int limit, String startDate, String endDate) {
        // 默认显示全部数据
        String start = (startDate == null || startDate.isEmpty()) ? "1970-01-01" : startDate;
        String end = (endDate == null || endDate.isEmpty()) ? "2999-12-31" : endDate;
        String sql = "SELECT t.train_number, COUNT(tk.id) AS ticket_count, " +
                     "COALESCE(SUM(tk.price), 0) AS total_revenue " +
                     "FROM trains t LEFT JOIN tickets tk ON t.id = tk.train_id " +
                     "AND tk.status = 1 AND DATE(tk.sale_time) BETWEEN ? AND ? " +
                     "GROUP BY t.id, t.train_number " +
                     "ORDER BY total_revenue DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, start, end, limit);
    }

    @Override
    public List<Map<String, Object>> getStationPopularity(String type, int limit, String startDate, String endDate) {
        String start = (startDate == null || startDate.isEmpty()) ? "1970-01-01" : startDate;
        String end = (endDate == null || endDate.isEmpty()) ? "2999-12-31" : endDate;
        String column = "departure".equals(type) ? "departure_station_id" : "arrival_station_id";
        String sql = "SELECT s.station_name, COUNT(tk.id) AS ticket_count " +
                     "FROM tickets tk JOIN stations s ON tk." + column + " = s.id " +
                     "WHERE tk.status = 1 AND DATE(tk.sale_time) BETWEEN ? AND ? " +
                     "GROUP BY s.id, s.station_name " +
                     "ORDER BY ticket_count DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, start, end, limit);
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        int columnCount = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }
}
