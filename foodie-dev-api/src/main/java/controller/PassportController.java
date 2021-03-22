package controller;

import com.imooc.pojo.Users;
import com.imooc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.util.StringUtil;

@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService usersService;

    @GetMapping("usernameIsExist")
    public int usernameIsExist(@RequestParam String username){
         //apache工具类里面，判断字符串的，这个可以额外的判断是否为空
        //1.判断用户名是否为空
        if(StringUtils.isBlank(username)){
            return 500;
        }
        //2。查找注册的用户名是否存在
        boolean isExist = usersService.queryUsernameIsExist(username);
        if (isExist){
            return 500;
        }
        //请求成功用户名没有重复
        return  200;
    }

}
