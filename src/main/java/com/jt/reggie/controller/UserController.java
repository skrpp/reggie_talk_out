package com.jt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jt.reggie.common.R;
import com.jt.reggie.entity.User;
import com.jt.reggie.service.UserService;
import com.jt.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    //启动spring boot后，框架会自动扫描配置项并创建多种类型的redis操作对象
    @Resource
    private RedisTemplate<String,Object> redisTemplate;		//对象存取使用它
    @Resource
    private StringRedisTemplate stringRedisTemplate;		//简单类型使用它


    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码

            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//        用阿里云短信服务API完成发送短信
            log.info("code={}",code);
//            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);

//        需要将生成的验证码保存到Session
//            session.setAttribute(phone,code);

            //将生成的验证码缓存到redis中，并设置有效期为5分钟
            stringRedisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");

    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

//        获取验证码
//        Object codeInSession = session.getAttribute(phone);

        //从redis中获取缓存的验证码
        Object codeInSession = stringRedisTemplate.opsForValue().get(phone);

//        从Session中获取保存的验证码
//        进行验证码的比对（页面提交的验证码与Session中保存的验证码对比）
        if (codeInSession != null && codeInSession.equals(code)){
            //        如果能够比对成功，说明登陆成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){
                //        判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //如果用户登录成功，删除redis中缓存的验证码
            stringRedisTemplate.delete(phone);
            return R.success(user);

        }



        return R.error("登录失败");

    }

    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session){

        session.removeAttribute("userPhone");


        return R.success("退出成功");
    }

}
