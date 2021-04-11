package com.imooc.config;

import com.imooc.service.OrderService;
import com.imooc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Orderjob {

    @Autowired
    private OrderService orderService;

    //@Scheduled(cron = "0/3 * * * * ?")
    //每隔一个小事来一次
    @Scheduled(cron = "0 0 0/1 * * * * ?")
    public void autoCloseOrder(){
        orderService.closeOrder();
        //获取当前的时间，传入一个格式化的内容
        System.out.println("执行定时任务，当前时间"
                + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN));
    }
}
