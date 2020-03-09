package com.heng.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testString(){
        String redisKey="test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));

    }

    @Test
    public void testHash(){
        //hash
        String redisKey="test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        //list
        redisKey="test:teacher";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,1));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        //set
        redisKey="test:student";
        redisTemplate.opsForSet().add(redisKey,"a","b","c","a");
        redisTemplate.opsForSet().size(redisKey);
        redisTemplate.opsForSet().pop(redisKey);
        redisTemplate.opsForSet().members(redisKey);
        //sortedSet
        redisKey="test:school";
        redisTemplate.opsForZSet().add(redisKey,"sawa",20);
        redisTemplate.opsForZSet().add(redisKey,"sfwafaw",10);
        redisTemplate.opsForZSet().add(redisKey,"sawa",100);
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"sawa"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"sawa"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,1));

        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:student",10, TimeUnit.SECONDS);
    }
    //多次访问同一个key
    @Test
    public void testBound(){
        String redisKey="test:count";
        BoundValueOperations operations=redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    //编程式事务
    @Test
    public void testTransactional(){
        Object obj=redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey="text:tx";
                operations.multi();
                operations.opsForSet().add(redisKey,"sa");
                operations.opsForSet().add(redisKey,"swada");
                operations.opsForSet().add(redisKey,"swfawfwaa");

                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
