package com.trainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.entity.Salesperson;
import com.trainstation.mapper.SalespersonMapper;
import com.trainstation.service.SalespersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SalespersonServiceImpl implements SalespersonService {

    @Autowired
    private SalespersonMapper salespersonMapper;

    @Override
    public List<Salesperson> getAllSalespeople() {
        return salespersonMapper.selectList(null);
    }

    @Override
    public Salesperson getSalespersonById(Long id) {
        return salespersonMapper.selectById(id);
    }

    @Override
    public Page<Salesperson> getSalespersonPage(int pageNum, int pageSize, String keyword) {
        Page<Salesperson> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Salesperson> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Salesperson::getName, keyword)
                   .or()
                   .like(Salesperson::getEmployeeCode, keyword);
        }
        wrapper.eq(Salesperson::getStatus, 1);
        return salespersonMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean saveSalesperson(Salesperson salesperson) {
        return salespersonMapper.insert(salesperson) > 0;
    }

    @Override
    public boolean updateSalesperson(Salesperson salesperson) {
        return salespersonMapper.updateById(salesperson) > 0;
    }

    @Override
    public boolean deleteSalesperson(Long id) {
        return salespersonMapper.deleteById(id) > 0;
    }
}
