package com.trainstation.service;

import com.trainstation.entity.TrainStation;
import com.trainstation.dto.TrainStationPriceDto;
import java.math.BigDecimal;
import java.util.List;

public interface TrainStationService {
    List<TrainStationPriceDto> getStationsByTrainId(Long trainId);
    boolean updateStationPrice(Long trainId, Long stationId, BigDecimal price);
    boolean saveTrainStation(TrainStation trainStation);
    boolean deleteTrainStation(Long id);
}
