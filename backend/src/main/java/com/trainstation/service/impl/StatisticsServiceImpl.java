package com.trainstation.service.impl;

import com.trainstation.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
