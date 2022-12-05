package com.jt.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jt.reggie.common.BaseContext;
import com.jt.reggie.common.R;
import com.jt.reggie.entity.ShoppingCart;
import com.jt.reggie.mapper.ShoppingCartMapper;
import com.jt.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>implements ShoppingCartService {

    @Override
    public R<String> clean() {

            // 进行用户比对
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

            // 删除即可
            this.remove(queryWrapper);

            return R.success("清空成功");

    }
}
