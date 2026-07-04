package com.trainstation.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    List<Map<String, Object>> getTrainSalesStatistics(String trainNumber, String saleDate);
    List<Map<String, Object>> getSalespersonRevenue(String date);
    List<Map<String, Object>> getTrainSalesByRange(String trainNumber, String startDate, String endDate);
    List<Map<String, Object>> getSalespersonRevenueByRange(String startDate, String endDate);

    // 仪表盘新增接口
    Map<String, Object> getKpi();
    Map<String, Object> get7DayTrend();
    List<Map<String, Object>> getTrainTopRevenue(int limit, String startDate, String endDate);
    List<Map<String, Object>> getStationPopularity(String type, int limit, String startDate, String endDate);
}
