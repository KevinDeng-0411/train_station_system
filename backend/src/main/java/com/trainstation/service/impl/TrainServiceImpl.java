package com.trainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.dto.TrainCreateRequest;
import com.trainstation.entity.Train;
import com.trainstation.entity.Station;
import com.trainstation.entity.TrainStation;
import com.trainstation.mapper.TrainMapper;
import com.trainstation.mapper.StationMapper;
import com.trainstation.mapper.TrainStationMapper;
import com.trainstation.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TrainServiceImpl implements TrainService {

    @Autowired
    private TrainMapper trainMapper;

    @Autowired
    private StationMapper stationMapper;

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Override
    public List<Train> getAllTrains() {
        return trainMapper.selectList(null);
    }

    @Override
    public Train getTrainById(Long id) {
        return trainMapper.selectById(id);
    }

    @Override
    public Train getTrainByNumber(String trainNumber) {
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Train::getTrainNumber, trainNumber);
        return trainMapper.selectOne(wrapper);
    }

    @Override
    public Page<Train> getTrainPage(int pageNum, int pageSize, String keyword) {
        Page<Train> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Train> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Train::getTrainNumber, keyword)
                   .or()
                   .like(Train::getDepartureCity, keyword)
                   .or()
                   .like(Train::getArrivalCity, keyword);
        }
        return trainMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean saveTrain(Train train) {
        return trainMapper.insert(train) > 0;
    }

    @Override
    @Transactional
    public Train createTrainWithStations(TrainCreateRequest request) {
        // 1. 查找始发站和终到站
        Station depStation = stationMapper.selectById(request.getDepartureStationId());
        Station arrStation = stationMapper.selectById(request.getArrivalStationId());
        if (depStation == null || arrStation == null) {
            throw new RuntimeException("站点不存在");
        }

        // 2. 创建车次
        Train train = new Train();
        train.setTrainNumber(request.getTrainNumber());
        train.setDepartureCity(depStation.getCity());
        train.setArrivalCity(arrStation.getCity());
        train.setTotalSeats(request.getTotalSeats());
        train.setRemainingSeats(request.getTotalSeats());
        train.setDepartureTime(request.getDepartureTime());
        train.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        trainMapper.insert(train);

        // 3. 自动添加始发站（stop_order=1）
        TrainStation depTs = new TrainStation();
        depTs.setTrainId(train.getId());
        depTs.setStationId(depStation.getId());
        depTs.setStopOrder(1);
        depTs.setDepartureTime(request.getDepartureTime());
        depTs.setPrice(new java.math.BigDecimal("0.00"));
        trainStationMapper.insert(depTs);

        // 4. 自动添加终到站（stop_order=2，后续可改）
        TrainStation arrTs = new TrainStation();
        arrTs.setTrainId(train.getId());
        arrTs.setStationId(arrStation.getId());
        arrTs.setStopOrder(2);
        arrTs.setPrice(new java.math.BigDecimal("0.00"));
        trainStationMapper.insert(arrTs);

        return train;
    }

    @Override
    public boolean updateTrain(Train train) {
        return trainMapper.updateById(train) > 0;
    }

    @Override
    public boolean deleteTrain(Long id) {
        return trainMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateRemainingSeats(Long trainId, int delta) {
        Train train = trainMapper.selectById(trainId);
        if (train == null) return false;
        int newRemaining = train.getRemainingSeats() + delta;
        if (newRemaining < 0 || newRemaining > train.getTotalSeats()) return false;
        train.setRemainingSeats(newRemaining);
        return trainMapper.updateById(train) > 0;
    }
}
