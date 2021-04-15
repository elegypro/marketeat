package com.imooc.service.center;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.utils.PagedGridResult;
import io.swagger.models.auth.In;

public interface MyOrdersService {

    //查询我的订单列表
    public PagedGridResult queryMyOrders(String userId,
                                         Integer orderStatus,
                                         Integer page,
                                         Integer pageSize);

    //订单状态 --> 商家发货
    public void updateDeliverOrderSatus(String orderId);

}
