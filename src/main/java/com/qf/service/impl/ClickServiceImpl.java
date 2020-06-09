package com.qf.service.impl;

import com.qf.mapper.ClickMapper;
import com.qf.pojo.ClickNumber;
import com.qf.service.IClickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.PrimitiveIterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenJie
 * @date 2020-06-08 20:21:43
 * 功能说明
 */
@Service
public class ClickServiceImpl implements IClickService {

    @Resource
    private ClickMapper clickMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private String lockLua = "local lockName = KEYS[1] --锁的名字\n" +
            "local lockValue = ARGV[1] --锁的value\n" +
            "local lockTimeOut = tonumber(ARGV[2]) --超时时间\n" +
            "\n" +
            "\n" +
            "--尝试加锁 setnx key value\n" +
            "local result = tonumber(redis.call('setnx', lockName, lockValue))\n" +
            "\n" +
            "\n" +
            "if result == 1 then\n" +
            "    --上锁成功，设置超时时间\n" +
            "                redis.call('expire', lockName, lockTimeOut)\n" +
            "    return '1'\n" +
            "end\n" +
            "\n" +
            "\n" +
            "--上锁失败\n" +
            "return '0'";


    private String unlock = "local lockName = KEYS[1]\n" +
            "local lockValue = ARGV[1]\n" +
            "\n" +
            "\n" +
            "--获得分布式锁中的数据\n" +
            "local lockUUID = redis.call('get', lockName);\n" +
            "\n" +
            "\n" +
            "if lockValue == lockUUID then\n" +
            "       --删除锁\n" +
            "       redis.call('del', lockName)\n" +
            "       return '1'\n" +
            "end\n" +
            "\n" +
            "\n" +
            "--锁不是我添加的\n" +
            "return '0'";



    @Override
    public void click() {
        String lockValue = UUID.randomUUID().toString();
        //Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "123");

        // 加分布式锁
        String result = stringRedisTemplate.execute(new DefaultRedisScript<>(lockLua, String.class),
                Collections.singletonList("lock"), lockValue, "10000");
        if ("1".equals(result)){
            stringRedisTemplate.expire("lock",10, TimeUnit.SECONDS);
            ClickNumber clickNumber = clickMapper.selectById(1);
            clickNumber.setNumber(clickNumber.getNumber()+1);
            clickMapper.updateById(clickNumber);

            // 释放锁
            String lockUUID = stringRedisTemplate.opsForValue().get("lock");
            /*if (lockValue.equals(lockUUID)){
                stringRedisTemplate.delete("lock");
            }*/
            String lockStr = stringRedisTemplate.execute(new DefaultRedisScript<>(unlock, String.class),
                    Collections.singletonList("lock"), lockValue);
            System.out.println(lockStr);

        }else {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 重新尝试获得锁
            click();
        }
    }
}
