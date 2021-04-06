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

    // 微信支付成功 ->支付中心 -> 平台
    //                       -> 回调通知的url
    String payReturnUrl ="http://localhost:8081/orders/notifyMerchanOrderPaid";

}
