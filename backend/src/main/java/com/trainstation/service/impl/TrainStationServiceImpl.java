package com.trainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trainstation.entity.TrainStation;
import com.trainstation.entity.Station;
import com.trainstation.mapper.TrainStationMapper;
import com.trainstation.mapper.StationMapper;
import com.trainstation.dto.TrainStationPriceDto;
import com.trainstation.service.TrainStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainStationServiceImpl implements TrainStationService {

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Autowired
    private StationMapper stationMapper;

    @Override
    public List<TrainStationPriceDto> getStationsByTrainId(Long trainId) {
        LambdaQueryWrapper<TrainStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainStation::getTrainId, trainId)
               .orderByAsc(TrainStation::getStopOrder);
        List<TrainStation> list = trainStationMapper.selectList(wrapper);

        return list.stream().map(ts -> {
            TrainStationPriceDto dto = new TrainStationPriceDto();
            dto.setStationId(ts.getStationId());
            dto.setStopOrder(ts.getStopOrder());
            dto.setArrivalTime(ts.getArrivalTime() != null ? ts.getArrivalTime().toString() : null);
            dto.setDepartureTime(ts.getDepartureTime() != null ? ts.getDepartureTime().toString() : null);
            dto.setPrice(ts.getPrice());

            Station station = stationMapper.selectById(ts.getStationId());
            if (station != null) {
                dto.setStationName(station.getStationName());
                dto.setCity(station.getCity());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean updateStationPrice(Long trainId, Long stationId, BigDecimal price) {
        LambdaQueryWrapper<TrainStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainStation::getTrainId, trainId)
               .eq(TrainStation::getStationId, stationId);
        TrainStation ts = trainStationMapper.selectOne(wrapper);
        if (ts == null) return false;
        ts.setPrice(price);
        return trainStationMapper.updateById(ts) > 0;
    }

    @Override
    public boolean saveTrainStation(TrainStation trainStation) {
        return trainStationMapper.insert(trainStation) > 0;
    }

    @Override
    public boolean deleteTrainStation(Long id) {
        return trainStationMapper.deleteById(id) > 0;
    }
}
