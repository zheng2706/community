package com.heng.community.service;

import com.heng.community.dao.LgoinTicketMapper;
import com.heng.community.dao.UserMapper;
import com.heng.community.entity.LoginTicket;
import com.heng.community.entity.User;
import com.heng.community.util.CommunityConstant;
import com.heng.community.util.CommunityUtil;
import com.heng.community.util.MailClient;
import com.heng.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService  implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClientl;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.dath.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

//    @Autowired
//    private LgoinTicketMapper lgoinTicketMapper;

    public User findUserById(int userId){
        User user=getCache(userId);
        if (user==null){
            user=initCacha(userId);
        }
        return user;
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();
        //空值处理
        if (user==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证账号
        User u=userMapper.selectByName(user.getUsername());
        if (u!=null){
            map.put("usernameMsg","账号已存在");
            return map;
        }
        u=userMapper.selectByEmail(user.getEmail());
        if (u!=null){
            map.put("emailMsg","邮箱已存在");
            return map;
        }
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format(("http://images.nowcoder.com/head/%dt.png"),new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context =new Context();
        context.setVariable("email",user.getEmail());
        String url =domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content =templateEngine.process("/mail/activation",context);
        mailClientl.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    public int activation(int userId, String code){
        User user=userMapper.selectById(userId);
        if (user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username,String password,int expiredSecond){
        Map<String,Object> map=new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        // 验证账号
        User user=userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }
        // 验证激活状态
        if (user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //验证密码
        password=CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
        }

        //生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSecond*1000));
//        lgoinTicketMapper.insertLoginTicket(loginTicket);
        String redisKey= RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket=(LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket){
//        return lgoinTicketMapper.selectByTicket(ticket);
        String redisKey= RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId,String headerUrl){
        int rows=userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;
    }

    public User findUserByName(String name){
        return userMapper.selectByName(name);
    }

    //1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        User user=(User)redisTemplate.opsForValue().get(redisKey);
        return user;
    }
    //2.取不到时，初始化缓存数据
    private User initCacha(int userId){
        User user=userMapper.selectById(userId);
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3.当数据变更时，清除缓存数据
    private void clearCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
