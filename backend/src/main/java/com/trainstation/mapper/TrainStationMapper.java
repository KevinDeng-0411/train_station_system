package com.trainstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trainstation.entity.TrainStation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainStationMapper extends BaseMapper<TrainStation> {
}
