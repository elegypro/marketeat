package com.imooc.controller;

import com.imooc.enums.PayMethod;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value="订单相关", tags={"订单相关的api接口"})//修改swagger日志中的标注
@RequestMapping("orders")
@RestController
public class OrdersController {


    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(
           @RequestBody SubmitOrderBO submitOrderBO){
           System.out.println(submitOrderBO.toString());

           if(submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type && submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type){
               return IMOOCJSONResult.errorMsg("支付方式不支持！");
           }

           //1.创建订单
           //2 创建订单以后，移除购物车中已经结算（已交付）的商品
           //3。向支付中心发送当前订单，用于保存支付中心的订单数据
           return IMOOCJSONResult.ok();
    }



}
