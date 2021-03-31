package com.imooc.controller;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.service.AddressService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


@Api(value="地址相关", tags={"地址相关的api接口"})//修改swagger日志中的标注
@RequestMapping("address")
@RestController
public class AddressController {

   //用户在确认订单页面，可以确认收货地址，做如下操作
    /**
     * 1。 查询用户的所有的收获地址
     * 2。新增收货地址
     * 3。删除收货地址
     * 4。修改收货地址
     * 5。设置默认地址
     */

    @Autowired
    private AddressService addressService;
    @ApiOperation(value = "根据用户id查询收获地址列表", notes = "根据用户id查询收获地址列表", httpMethod = "POST")
    @PostMapping("/list")
    public IMOOCJSONResult list(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
           @RequestParam String userId){
        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("");
        }
        List<UserAddress> list = addressService.querAll(userId);
        return IMOOCJSONResult.ok(list);
    }

}
