package com.qf.application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author ChenJie
 * @date 2020-06-08 11:21:43
 * 功能说明
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    String lua = "local key = KEYS[1] local money = tonumber(redis.call('get', key)) if money >= 10000 then return \"yq\" else return \"qb\" end";

    @Test
    public void test1(){
        redisTemplate.opsForValue().set("name","chenjie");
    }

    @Test
    public void test2(){
        String result = (String) stringRedisTemplate.execute(new DefaultRedisScript(lua,String.class),Collections.singletonList("money"));
        System.out.println(result);
    }

    @Test
    public void test3(){
        //获得redis的原始连接
        RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();
        byte[] result = connection.eval(lua.getBytes(), ReturnType.VALUE, 1, "money".getBytes());
        System.out.println("eval->" + new String(result));
        connection.close();
    }
}
