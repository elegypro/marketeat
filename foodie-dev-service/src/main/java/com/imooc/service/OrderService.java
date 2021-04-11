package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /**
     * 使用定时任务关闭超期未支付订单，会存在的弊端
     * 1.会有时间差，程序不严谨
     *   10：39下单，11：00检查不足1小时，12：00检查，超过1小时
     * 2.不支持集群（单个节点同时部署多个）
     * 通过是个十个节点部署了一台集群，总共有十台计算机，这个时候我们的定时任务会存在莫个节点上，
     *所以它会每个小时同时的执行十次，我们需要单独来运行所有的定时方案
     * 解决方法：只使用一台计算机节点，单独用来运行所有的定时任务
     * 3.会对数据库全表搜索，及其影响性能
     * 定时任务，仅仅只适用于小型轻量级项目，传统项目
     * 消息队列MQ->RabbitMQ, RocketMQ,Kafka,ZeroMQ..
     *     延时任务（队列）
     *     10：12分下单的，未付款（10）状态，11：12检查，如果当前状态还是10，则直接
     *     关闭订单即可
     * @param submitOrderBO
     * @return
     */

    //用于创建订单相关信息
    public OrderVO createOrder(SubmitOrderBO submitOrderBO);

    //用于修改订单状态
    public void updateOrderStatus(String orderId,Integer orderSatus);

    //查询订单状态
    public OrderStatus queryOrderStatusInfo(String orderId);

    //关闭超时未支付订单
    public void closeOrder();


}
