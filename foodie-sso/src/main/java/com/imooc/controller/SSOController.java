package com.imooc.controller;



import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


//@Controller
@ApiIgnore
@RestController
public class SSOController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";


    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        model.addAttribute("returnUrl", returnUrl);
        //  后续完善校验是否登录
        // 1。获取userTicket门票，如果cookie中能够获取到，证明用户登录过，此时签发一个一次性的票据，并且回调
        String userTicket = getCookie(request,COOKIE_USER_TICKET);

        // 用户从未登录过，第一次进入则跳转到CAS的统一登录页面
        return "login";
    }
         //对usertick进行校验,CAS全局用户门票
        private boolean verifyUserTicket(){
        //0。验证门票不能为空
            if(StringUtils.isBlank()){

            }

        }

    /**
     * CAS的统一登录接口
     * 目的：
     * 1。登录后创建用户的全局会话
     * 2。创建用户的全局门票，用以表示在CAS端是否登录
     * 3。创建用户的临时票据，用于回跳回传
     *
     * @param username
     * @param password
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {

        model.addAttribute("returnUrl", returnUrl);
        // TODO 后续完善校验是否登录

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }

        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));

        if (userResult == null) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }

        //2。实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), uniqueToken);
        UsersVO usersVO = new UsersVO();
        //把咱们的属性去做相应的拷贝
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(),
                JsonUtils.objectToJson(usersVO));

        //3。生成ticket门票，全局门票，代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();

        // 3.1 用户全局的门票需要放入CAS端的cookie中
        setCookie(COOKIE_USER_TICKET, userTicket, response);

        //4.userTicket关联用户id，并且放入到redis中，代表这个用户有门票了
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, userResult.getId());

        //5.生成临时票据，回跳到调用端网站，是由CAS端所签发的一个一次性的临时ticket
        String tmpTicket = createTmpTicket();

        /**
         * userTicket:用于表示用户在CAS端的一个登录状态：已经登录
         * tmpTicket: 用于颁发给用户进行一次性的验证的票据，有时效性
         */
        return "login";
//        return "redirect" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    @GetMapping("/verifyTmpTicket")
    @ResponseBody
    public Object verifyTmpTicket(String tmpTicket,
                                  Model model,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {

        // 使用一次性临时票据来验证用户是否登录，如果登录过，把用户会话信息返回给站点
        // 使用完毕后，需要销毁临时票据
        String tempTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tempTicketValue)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        // 如果临时票据ok，则需要销毁，并且拿到CAS端cookie中的全局userTicket，以此再获取
        if (!tempTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        } else {
            // 销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        //1.验证并且获取用户的userTicket
        String userTicket = getCookie(request,COOKIE_USER_TICKET);
        String userId = redisOperator.get(REDIS_USER_TICKET+":"+userTicket);
        if (StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        //2.验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TICKET+":"+userTicket);
        if (StringUtils.isBlank(userRedis)){
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        // 验证成功，返回OK，携带用户会话
        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(userRedis,UsersVO.class));
    }


    /**
     * 创建临时票据
     *
     * @return
     */

    private String createTmpTicket() {
        String temTicket = UUID.randomUUID().toString().trim();

        try {
            redisOperator.set(REDIS_TMP_TICKET + ":" + temTicket,
                    MD5Utils.getMD5Str(temTicket), 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temTicket;
    }

    private void setCookie(String key,
                           String val,
                           HttpServletResponse response) {
        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // 获取cookie
      private String getCookie(HttpServletRequest request, String key) {

        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || StringUtils.isBlank(key)) {
            return null;
        }

        String cookieValue = null;
        for (int i = 0; i < cookieList.length; i++) {
            if (cookieList[i].getName().equals(key)){
                cookieValue = cookieList[i].getValue();
                break;
            }
        }
        return cookieValue;
    }


}