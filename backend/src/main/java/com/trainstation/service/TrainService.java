package com.trainstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.dto.TrainCreateRequest;
import com.trainstation.entity.Train;
import java.util.List;

public interface TrainService {
    List<Train> getAllTrains();
    Train getTrainById(Long id);
    Train getTrainByNumber(String trainNumber);
    Page<Train> getTrainPage(int pageNum, int pageSize, String keyword);
    boolean saveTrain(Train train);
    Train createTrainWithStations(TrainCreateRequest request);
    boolean updateTrain(Train train);
    boolean deleteTrain(Long id);
    boolean updateRemainingSeats(Long trainId, int delta);
}
