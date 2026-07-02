package com.trainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trainstation.entity.Station;
import com.trainstation.mapper.StationMapper;
import com.trainstation.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public List<Station> getAllStations() {
        return stationMapper.selectList(null);
    }

    @Override
    public Station getStationById(Long id) {
        return stationMapper.selectById(id);
    }

    @Override
    public Station getStationByName(String name) {
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Station::getStationName, name);
        return stationMapper.selectOne(wrapper);
    }

    @Override
    public boolean saveStation(Station station) {
        return stationMapper.insert(station) > 0;
    }

    @Override
    public boolean updateStation(Station station) {
        return stationMapper.updateById(station) > 0;
    }

    @Override
    public boolean deleteStation(Long id) {
        return stationMapper.deleteById(id) > 0;
    }
}
