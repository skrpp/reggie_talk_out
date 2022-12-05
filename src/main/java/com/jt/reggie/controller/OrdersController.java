package com.jt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jt.reggie.common.BaseContext;
import com.jt.reggie.common.R;
import com.jt.reggie.dto.OrdersDto;
import com.jt.reggie.entity.OrderDetail;
import com.jt.reggie.entity.Orders;
import com.jt.reggie.entity.ShoppingCart;
import com.jt.reggie.entity.User;
import com.jt.reggie.service.OrderDetailService;
import com.jt.reggie.service.OrdersService;
import com.jt.reggie.service.impl.ShoppingCartServiceImpl;
import com.jt.reggie.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;
    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);

        return R.success("下单成功");
    }

    /**
     * 查看历史订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("userPage")
    public R<Page> pagePhone(int page, int pageSize){
        Page<Orders> pageInfo = new Page(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        // 用户ID
        Long currentId = BaseContext.getCurrentId();


        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, currentId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        //订单赋值
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = records.stream().map((item)->{

            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item,ordersDto);

            Long itemId = item.getId();

            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,itemId);

            long count = orderDetailService.count(wrapper);
            List<OrderDetail> list = orderDetailService.list(wrapper);

//            ordersDto.getSumNum(count);

            ordersDto.setOrderDetails(list);

            return ordersDto;


        }).collect(Collectors.toList());

        // 完成dishDtoPage的results的内容封装
        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }
    /**
     * 后台回显
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> pagePC(int page, int pageSize, Long number, Date beginTime, Date endTime){

        // 定制基本Page
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        // 定制带有名字的特殊Orders
        Page<OrdersDto> ordersDtoPage = new Page<>();

        // 书写限制条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null,Orders::getId,number);
        if (beginTime != null && endTime != null){
            queryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        }

        ordersService.page(pageInfo, queryWrapper);

        // 普通赋值
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        // 订单赋值
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {

            // 新创内部元素
            OrdersDto ordersDto = new OrdersDto();

            // 普通值赋值
            BeanUtils.copyProperties(item,ordersDto);

            // 特殊值赋值
            Long userId = item.getUserId();

            User user = userService.getById(userId);

            ordersDto.setUserName(user.getName());

            return ordersDto;
        }).collect(Collectors.toList());

        // 完成dishDtoPage的results的内容封装
        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }

    /**
     * 订单状态修改
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> orderStatus(@RequestBody Orders orders){

        ordersService.updateById(orders);

        return R.success("订单已派送");
    }



    /**
     * 客户端点击再来一单
     * 我们需要将订单内的菜品重新加入购物车，所以在此之前我们需要将购物车清空（业务层实现方法）
     */
    @PostMapping("/again")
    public R<String> againSubmit(@RequestBody Map<String,String> map){
        // 获得ID
        String ids = map.get("id");

        long id = Long.parseLong(ids);

        // 制作判断条件
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);

        //获取该订单对应的所有的订单明细表
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        //通过用户id把原来的购物车给清空
        shoppingCartService.clean();

        //获取用户id
        Long userId = BaseContext.getCurrentId();

        // 整体赋值
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {

            // 以下均为赋值操作

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(item.getImage());

            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();

            if (dishId != null) {
                // 如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            } else {
                // 添加到购物车的是套餐
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(item.getName());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        // 将携带数据的购物车批量插入购物车表
        shoppingCartService.saveBatch(shoppingCartList);

        return R.success("操作成功");
    }
}
