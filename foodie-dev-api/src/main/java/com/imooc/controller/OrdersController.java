package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.AddressService;
import com.imooc.service.OrderService;
import com.imooc.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Api(value = "订单相关", tags = {"订单相关的api接口"})//修改swagger日志中的标注
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(
            @RequestBody SubmitOrderBO submitOrderBO,
            //传递数据到前端
            HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println(submitOrderBO.toString());

        if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type && submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type) {
            return IMOOCJSONResult.errorMsg("支付方式不支持！");
        }

        //判断redis中有没有购物车，购物车为空，数据不正确
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopcartJson)) {
            return IMOOCJSONResult.errorMsg("购物车数据不正确");
        }
        List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);

        //1.创建订单
        OrderVO orderVO = orderService.createOrder(shopcartList,submitOrderBO);
        String orderId = orderVO.getOrderId();

        //2 创建订单以后，移除购物车中已经结算（已交付）的商品
        /**
         * 1001
         * 2002 ——>用户购买
         * 3003 ——>用户购买
         */

        //清理覆盖现有的redis中的购物数据
        shopcartList.removeAll(orderVO.getToBeRemovedShopcatdList());
        redisOperator.set(FOODIE_SHOPCART+":"+submitOrderBO.getUserId(),JsonUtils.objectToJson(shopcartList));
        // 整合redis后，完善购物车中的已经结算商品清除，并且同步前端cookie
        CookieUtils.setCookie(request,response,FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartList),true);
        //3。向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        //为了方便测试购买，所以所有的支付金额都统一改为一分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        //设置传输类型
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "password");

        HttpEntity<MerchantOrdersVO> entity =
                new HttpEntity<>(merchantOrdersVO, headers);
        //发起相应的请求,拿到responseEntity
        ResponseEntity<IMOOCJSONResult> responseEntity =
                restTemplate.postForEntity(paymentUrl,
                        entity,
                        IMOOCJSONResult. class);//IMOOCJSONResult返回回来的类型
        //获取responseEntity里面的内容
        IMOOCJSONResult paymentResult = responseEntity.getBody();
        if(paymentResult.getStatus() != 200){

//          return IMOOCJSONResult.errorMsg("支付中心订单创建失败，请联系管理员");
            orderService.updateOrderStatus(orderId,OrderStatusEnum.WAIT_DELIVER.type);
            return IMOOCJSONResult.ok(orderId);
        }

        return IMOOCJSONResult.ok(orderId);
    }

    @PostMapping("notifyMerchanOrderPaid")
    public Integer notifyMerchanOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(String orderId){
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return IMOOCJSONResult.ok(orderStatus);
    }


}
