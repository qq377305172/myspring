package com.jing.controller;

import cn.hutool.core.util.StrUtil;
import com.jing.config.JedisConfig;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.List;

/**
 * @author Admin
 * @date 2020/3/28 15:06
 */
@Controller
public class SecKillController {
    @Resource
    private JedisConfig jedisConfig;
    @Resource
    private ActiveMQUtil activeMQUtil;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private Redisson redisson;

    @Resource
    private StringRedisTemplate redisTemplate;

    public void test() {
        String lockKey = "lockKey";
        RLock lock = redisson.getLock(lockKey);
        try {
            lock.lock();
            String stockStr = redisTemplate.opsForValue().get("stock");
            int stock = StrUtil.isBlank(stockStr) ? 0 : Integer.parseInt(stockStr);
            if (stock > 0) {
                stock--;
                redisTemplate.opsForValue().set("stock", String.valueOf(stock));
            }
        } finally {
            lock.unlock();
        }
    }

    @RequestMapping("/kill")
    @ResponseBody
    public String index() {
        //先到先得
        RSemaphore semaphore = redissonClient.getSemaphore("");
        boolean b = semaphore.tryAcquire(1);
        if (b) {
            //抢购成功
        } else {
            //抢购失败
        }
        //随机
        Jedis jedis = jedisConfig.redisPoolFactory().getResource();
        String watch = jedis.watch("");
        Transaction tx = jedis.multi();
        tx.incrBy("", -1);
        List<Object> exec = tx.exec();
        if (null != exec && !exec.isEmpty()) {
            //抢购成功
        } else {
            //抢购失败
        }

        Connection connection = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            connection.start();
            Session session = connection.createSession(true, 1);
            MessageProducer producer = session.createProducer(null);
            producer.send(null);
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    //当前时间戳
    static long currentTimeMillis1 = System.currentTimeMillis();
    //水桶容量
    static long bucketCapacity = 10000;
    //出水速度,即服务器处理请求的能力
    static long outSpeed = 1;
    //当前水量,即当前的即时请求压力
    static long currentWaterQuantity = 100;

    /**
     * 当前请求线程进入漏桶方法,true则不被拒绝,false则说明当前服务器载水量不足,则被拒绝
     */
    public static boolean bucketAlgorithm() {
        long currentTimeMillis2 = System.currentTimeMillis();
        //可以认为 时间间隔*outSpeed 即为此次出水量
        //本次请求时间-上次请求时间=时间间隔
        currentWaterQuantity = Math.max(0, currentWaterQuantity - (currentTimeMillis2 - currentTimeMillis1) * outSpeed);
        //更新请求时间
        currentTimeMillis1 = currentTimeMillis2;
        if (currentWaterQuantity < bucketCapacity) {
            //执行漏水代码后,若桶未满,则继续加水
            currentWaterQuantity++;
            System.out.println("桶未满: " + currentWaterQuantity);
            return true;
        } else {
            //桶满,拒绝加水
            System.out.println("桶满");
            return false;
        }
    }

//    static long timeStamp = System.currentTimeMillis();
//    static long capacity = 100000; // 桶的容量
//    static long rate = 1;//令牌放入速度
//    static long tokens = 100000;//当前令牌数量
//
//    public static boolean control() {
//        //先执行添加令牌的操作
//        long now = System.currentTimeMillis();
//        tokens = Math.max(capacity, tokens + (now - timeStamp) * rate);
//        timeStamp = now;
//        if (tokens < 1) {
//            System.out.println("令牌已用完，拒绝访问");
//            return false; //令牌已用完，拒绝访问
//        } else {
//            System.out.println("还有令牌，领取令牌: " + tokens);
//            tokens--;
//            return true; //还有令牌，领取令牌
//        }
//    }


    public static void main(String[] args) {
        for (int i = 0; i < 10010; i++) {
//            bucketAlgorithm();
//            control();
        }
    }

}
