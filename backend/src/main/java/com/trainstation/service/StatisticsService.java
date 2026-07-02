package com.trainstation.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    List<Map<String, Object>> getTrainSalesStatistics(String trainNumber, String saleDate);
    List<Map<String, Object>> getSalespersonRevenue(String date);
}
