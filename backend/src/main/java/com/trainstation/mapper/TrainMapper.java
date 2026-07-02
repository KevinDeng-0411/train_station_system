package com.trainstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trainstation.entity.Train;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainMapper extends BaseMapper<Train> {
}
