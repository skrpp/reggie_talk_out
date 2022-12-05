package com.jt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jt.reggie.dto.SetmealDto;
import com.jt.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    //根据id查询菜品信息和对应的口味信息
    public SetmealDto getByIdWithDish(Long id);

    //修改套餐，同时更新套餐内菜品信息
    public void updateWithDish(SetmealDto setmealDto);

    //删除套餐，同时需要删除套餐和菜品的关联数据
    public void removeWithDish(List<Long> ids);


}
