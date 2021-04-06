package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrderService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.models.auth.In;
import org.aspectj.weaver.ast.Or;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private Sid sid;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public String createOrder(SubmitOrderBO submitOrderBO) {
        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        //包邮费用设置为0
        Integer postAmount = 0;

        UserAddress address = addressService.queryUserAddress(userId, addressId);
        String orderId = sid.nextShort();

        // 1.新订单数据保存
        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);

        newOrder.setReceiverMobile(address.getMobile());
        newOrder.setReceiverName(address.getReceiver());
        newOrder.setReceiverAddress(address.getProvince() + " "
                + address.getCity() + " "
                + address.getDistrict() + " "
                + address.getDetail());

        //邮费
        newOrder.setPostAmount(postAmount);
        newOrder.setPayMethod(payMethod);
        //用户的备注
        newOrder.setLeftMsg(leftMsg);
        //是否被评价过
        newOrder.setIsComment(YesOrNo.NO.type);
        //是否删除
        newOrder.setIsDelete(YesOrNo.NO.type);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());
        // 2。循环根据itemSpecIds保存订单商品信息
        String itemSpecIdArr[] = itemSpecIds.split(",");
        Integer totalAmout = 0; //商品原始的价格
        Integer realPayAmount = 0; //优惠后的实际价格
        for (String itemSpecId : itemSpecIdArr){

            //TODO 整合redis后，商品购买的数量重新从redis的购物车中获得
            int buyCounts = 1;

            //2.1 根据规格id，查询规格的具体信息
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmout += itemsSpec.getPriceNormal()*buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() *buyCounts;

            //2.2 根据商品id，获取商品信息以及商品图片
            String itemId = itemsSpec.getItemId();
            Items items = itemService.queryItemById(itemId);
            String imgUrl = itemService.querItemMainImgById(itemId);

            //2.3 循环保存子订单数据到数据库
            String subOrderId = sid.nextShort();
            OrderItems subOrderItem = new OrderItems();
            subOrderItem.setId(subOrderId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setItemName(items.getItemName());
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItem);

            //2.4 在用户提交订单以后，规格表中需要扣除库存
            itemService.decreaseItemSpecStock(itemSpecId,buyCounts);
        }



        //商品的价格
            newOrder.setTotalAmount(totalAmout);
        //真实的价格
            newOrder.setRealPayAmount(realPayAmount);
            ordersMapper.insert(newOrder);

        //3保存订单状态表
        OrderStatus waitPayOrderSatus = new OrderStatus();
        waitPayOrderSatus.setOrderId(orderId);
        //订单状态
        waitPayOrderSatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderSatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderSatus);

        return orderId;
    }

    @Override
    public void updateOrderStatus(String orderId, Integer orderSatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderSatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);

    }
}
