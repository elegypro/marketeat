package com.imooc.service.center;

import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface MyCommentService {

    //根据id查询关联的商品
    public List<OrderItems> queryPendingComment(String orderId);

    //保存用户的评论
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentList);

    //我的查询分页
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize);

}
