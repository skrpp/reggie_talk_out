package com.jt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jt.reggie.common.R;
import com.jt.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
//    清除购物车数据
    public R<String> clean();
}
