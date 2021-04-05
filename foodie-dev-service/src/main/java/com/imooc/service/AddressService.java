package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {

    //根据id查询用户的收货地址
    public List<UserAddress> querAll(String userId);

    //用户新增地址
    public void addNewUserAddress(AddressBO addressBO);

    //用户修改地址
    public void updateUserAddress(AddressBO addressBO);

    //用户id和地址id，删除对应的用户地址信息
    public void deleteUserAddress(String userId,String addressId);

    //修改默认的地址
    public void updateUserAddressToBeDefault(String userId,String addressId);

    //根据用户id和地址，查询具体的用户地址对象信息
    public UserAddress queryUserAddress(String userId,String addressId);
}
