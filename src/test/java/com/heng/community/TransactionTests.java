package com.heng.community;

import com.heng.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest

@RunWith(SpringRunner.class)

@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {
    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1(){
        Object obg=alphaService.save1();
        System.out.println(obg);
    }

    @Test
    public void testSave2(){
        alphaService.save2();

    }
}
