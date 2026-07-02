package com.trainstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.entity.Salesperson;
import java.util.List;

public interface SalespersonService {
    List<Salesperson> getAllSalespeople();
    Salesperson getSalespersonById(Long id);
    Page<Salesperson> getSalespersonPage(int pageNum, int pageSize, String keyword);
    boolean saveSalesperson(Salesperson salesperson);
    boolean updateSalesperson(Salesperson salesperson);
    boolean deleteSalesperson(Long id);
}
