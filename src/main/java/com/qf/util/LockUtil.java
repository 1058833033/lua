package com.qf.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenJie
 * @date 2020-06-08 22:01:15
 * 功能说明
 */
@Component
public class LockUtil {

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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public boolean lock(String key, Integer time){
        String uuid = UUID.randomUUID().toString();
        threadLocal.set(uuid);

        // 加分布式锁
        Boolean absent = stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, time, TimeUnit.MILLISECONDS);

        // 加分布式锁
        /*String result = stringRedisTemplate.execute(new DefaultRedisScript<>(lockLua, String.class),
                Collections.singletonList(key), uuid, time.toString());*/
        //return "1".equals(result);
        return absent;
    }

    public boolean unlock(String key){
        String uuid = threadLocal.get();
        String lockStr = stringRedisTemplate.execute(new DefaultRedisScript<>(unlock, String.class),
                Collections.singletonList(key), uuid);
        threadLocal.set(null);
        return "1".equals(lockStr);
    }
}
