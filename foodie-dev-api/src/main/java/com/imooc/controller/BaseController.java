package com.imooc.controller;


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
    String payReturnUrl ="http://w2g927.natappfree.cc/orders/notifyMerchanOrderPaid";

}
