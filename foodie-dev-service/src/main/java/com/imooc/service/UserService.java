package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
//import org.apache.catalina.User;

public interface UserService {
    public boolean queryUsernameIsExist(String username);

    //接受前端到后端到数据包
    public Users createUser(UserBO userBO);

    //检索用户名和密码是否匹配，用于登录
    public Users queryUserForLogin(String username, String password);
}
