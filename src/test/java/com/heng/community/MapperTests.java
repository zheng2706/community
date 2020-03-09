package com.heng.community;

import com.heng.community.dao.DiscussPostMapper;
import com.heng.community.dao.LgoinTicketMapper;
import com.heng.community.dao.MessageMapper;
import com.heng.community.dao.UserMapper;
import com.heng.community.entity.DiscussPost;
import com.heng.community.entity.LoginTicket;
import com.heng.community.entity.Message;
import com.heng.community.entity.User;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    LgoinTicketMapper lgoinTicketMapper;

    @Autowired
    MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user=userMapper.selectById(101);
        System.out.println(user);
        user=userMapper.selectByName("liubei");
        System.out.println(user);
        user=userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows=userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate(){
        int rows=userMapper.updateStatus(150,1);
        System.out.println(rows);
        rows=userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);
        rows=userMapper.updatePassword(150,"825714");
        System.out.println(rows);
    }

    @Test
    public void testSelectPost(){
        List<DiscussPost> postList=discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost post:postList){
            System.out.println(post);
        }
        int rows=discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsetLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(1);
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        lgoinTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket=lgoinTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        lgoinTicketMapper.updateStatus("abc",0);
        loginTicket=lgoinTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters() {
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);



    }
}
