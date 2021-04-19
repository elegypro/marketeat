package com.imooc.service.center;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.utils.PagedGridResult;
import io.swagger.models.auth.In;
import org.springframework.core.annotation.Order;

public interface MyOrdersService {

    //查询我的订单列表
    public PagedGridResult queryMyOrders(String userId,
                                         Integer orderStatus,
                                         Integer page,
                                         Integer pageSize);

    //订单状态 --> 商家发货
    public void updateDeliverOrderSatus(String orderId);

    //查询我的订单
    public Orders queryMyOrder(String userId, String orderId);

    //更新订单状态- 确认收货
    public boolean updateReceiveOrderStatus(String orderId);

    //删除订单(逻辑删除)
    public boolean deleteOrder(String userId, String orderId);


}
