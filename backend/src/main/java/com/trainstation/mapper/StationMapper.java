package com.trainstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trainstation.entity.Station;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StationMapper extends BaseMapper<Station> {
}
