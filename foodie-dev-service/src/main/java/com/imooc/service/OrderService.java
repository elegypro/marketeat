package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.bo.UserBO;

import java.util.List;

public interface OrderService {

    //用于创建订单相关信息
    public void createOrder(SubmitOrderBO submitOrderBO);
}
