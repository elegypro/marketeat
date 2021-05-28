package com.imooc.controller;


import com.imooc.pojo.Orders;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    //支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";
    // 微信支付成功 ->支付中心 -> 平台
    //                       -> 回调通知的url
    String payReturnUrl = "http://w2g927.natappfree.cc/orders/notifyMerchanOrderPaid";

    //用户上传头像的位置
    //public static final String IMAGE_USER_FACE_LOCATION = "/Users/zhangxuanzhi/Desktop/apache-tomcat-9.0.44/webapps/images/foodie/faces";
    //File.separator 可以根据不同的操作系统来切换不同的斜杠/
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "Users" +
            File.separator + "zhangxuanzhi" +
            File.separator + "Desktop" +
            File.separator + "apache-tomcat-9.0.44" +
            File.separator + "webapps" +
            File.separator + "images" +
            File.separator + "foodie" +
            File.separator + "faces";

    @Autowired
    public MyOrdersService myOrdersService;
    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @return
     */
    public IMOOCJSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return IMOOCJSONResult.errorMsg("订单不存在！");
        }
        return IMOOCJSONResult.ok(order);
    }
}

