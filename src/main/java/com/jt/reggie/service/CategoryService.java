package com.jt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jt.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(long id);
}
