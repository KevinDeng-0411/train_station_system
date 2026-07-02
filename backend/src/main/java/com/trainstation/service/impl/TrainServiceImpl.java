package com.trainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.entity.Train;
import com.trainstation.mapper.TrainMapper;
import com.trainstation.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TrainServiceImpl implements TrainService {

    @Autowired
    private TrainMapper trainMapper;

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
