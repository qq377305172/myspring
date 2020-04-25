package com.jing.service.impl;

import com.example.demo.dao.OmsOrderDao;
import com.example.demo.dao.PmsSkuInfoDao;
import com.example.demo.entity.OmsOrder;
import com.example.demo.entity.PmsSkuInfo;
import com.example.demo.service.OrderService;
import com.example.demo.service.PaymentService;
import com.example.demo.util.ActiveMQUtil;
import com.example.demo.util.JsonUtil;
import com.example.demo.util.RedisUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.jms.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Admin
 * @title: OrderServiceImpl
 * @projectName demo
 * @description: TODO
 * @date 2020/3/21 18:06
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Resource
    private OmsOrderDao omsOrderDao;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private PmsSkuInfoDao pmsSkuInfoDao;
    @Resource
    private ActiveMQUtil activeMQUtil;
    @Resource
    private PaymentService paymentService;

    @Override
    public String genTradeNo(Long memberId) {
        Jedis jedis = null;
        try {
            String userTradeNoKey = "user:" + memberId + ":tradeNo";
            UUID uuid = UUID.randomUUID();
            jedis = redisUtil.getJedis();
            jedis.setex(userTradeNoKey, 60 * 30, uuid.toString());
            return uuid.toString();
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public boolean checkTradeNo(Long memberId, String tradeNo) {
        Jedis jedis = null;
        try {
            String userTradeNoKey = "user:" + memberId + ":tradeNo";
            jedis = redisUtil.getJedis();
            String s = jedis.get(userTradeNoKey);
            return null != tradeNo && tradeNo.equals(s);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public boolean delTradeNo(Long memberId, String tradeNo) {
        Jedis jedis = null;
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            jedis = redisUtil.getJedis();

            String userTradeNoKey = "user:" + memberId + ":tradeNo";

            Long eval = (Long) jedis.eval(script, Collections.singletonList(userTradeNoKey),
                    Collections.singletonList(tradeNo));

            return eval != null && eval == 1;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public boolean checkPrice(Long productSkuId, BigDecimal price) {
        Example example = new Example(PmsSkuInfo.class);
        example.createCriteria().andEqualTo("id", productSkuId).andEqualTo("price", price);
        return pmsSkuInfoDao.selectCountByExample(example) == 1;
    }

    @Override
    public int saveOrder(OmsOrder omsOrder) {
        return omsOrderDao.insertSelective(omsOrder);
    }

    @Override
    public OmsOrder getOrderByTradeNo(String tradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(tradeNo);
        return omsOrderDao.selectOne(omsOrder);
    }

    @Override
    public int updateProcessStatus(String orderId, int status) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setId(Long.valueOf(orderId));
        omsOrder.setStatus(status);
        return omsOrderDao.updateByPrimaryKeySelective(omsOrder);
    }

    @Transactional
    @Override
    public void sendOrderResult(String orderId) {
        OmsOrder omsOrder = omsOrderDao.selectByPrimaryKey(Long.valueOf(orderId));
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue orderResultQueue = session.createQueue("ORDER_RESULT_QUEUE");
            producer = session.createProducer(orderResultQueue);

            ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
            activeMQTextMessage.setText(JsonUtil.obj2Json(omsOrder));
            producer.send(activeMQTextMessage);
            int i = updateProcessStatus(orderId, 2);
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != producer) {
                    producer.close();
                }
                if (null != session) {
                    session.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<OmsOrder> getUnpaidOrderList() {
        return null;
    }

    @Async
    @Override
    public void checkExpireOrder(OmsOrder orderInfo) {
        Date expireDate = DateUtils.addDays(orderInfo.getCreateTime(), 1);
        if (new Date().after(expireDate)) {
            updateProcessStatus(String.valueOf(orderInfo.getId()), 4);
            paymentService.closePayment(orderInfo.getId());
        }
    }
}
