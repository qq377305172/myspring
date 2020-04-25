package com.jing.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.example.demo.controller.PaymentController;
import com.example.demo.dao.PaymentInfoDao;
import com.example.demo.entity.PaymentInfo;
import com.example.demo.service.PaymentService;
import com.example.demo.util.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;

/**
 * @author Admin
 * @title: PaymentServiceImpl
 * @projectName demo
 * @description: TODO
 * @date 2020/3/25 20:52
 */
@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {
    private final Logger LOG = LoggerFactory.getLogger(PaymentController.class);
    @Resource
    private AlipayClient alipayClient;
    @Resource
    private ActiveMQUtil activeMQUtil;
    @Resource
    private PaymentInfoDao paymentInfoDao;

    @Override
    public Long save(PaymentInfo paymentInfo) {
        int i = paymentInfoDao.insertSelective(paymentInfo);
        return paymentInfo.getId();
    }

    @Override
    public int update(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn", paymentInfo.getOrderSn());
        return paymentInfoDao.updateByExampleSelective(paymentInfo, example);
    }

    @Override
    public void sendPaymentResult(Long orderId, String success) {
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue paymentResultQueue = session.createQueue("PAYMENT_RESULT_QUEUE");
            ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();
            activeMQMapMessage.setString("orderId", String.valueOf(orderId));
            activeMQMapMessage.setString("result", success);
            producer = session.createProducer(paymentResultQueue);
            producer.send(activeMQMapMessage);
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != producer) {
                    producer.close();
                }
                if (null != producer) {
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
    public PaymentInfo getPaymentInfo(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("alipayTradeNo", paymentInfo.getAlipayTradeNo());
        return paymentInfoDao.selectOneByExample(example);
    }

    @Override
    public void updateByPrimaryKeySelective(PaymentInfo paymentInfo4Upt) {
        paymentInfoDao.updateByPrimaryKeySelective(paymentInfo4Upt);
    }


    public String checkAlipayPayment(PaymentInfo paymentInfo) {
        LOG.info("开始主动检查支付状态: " + paymentInfo.toString());
        //先检查当前数据库是否已经变为“已支付状态”
        if (paymentInfo.getId() == null) {
            LOG.info("outTradeNo:" + paymentInfo.getAlipayTradeNo());
            paymentInfo = getPaymentInfo(paymentInfo);
        }
        if ("已支付".equals(paymentInfo.getPaymentStatus())) {
            LOG.info("该单据已支付:" + paymentInfo.getAlipayTradeNo());
            return "已支付";
        }

        //如果不是已支付，继续去查询alipay的接口
        LOG.info("查询alipay的接口");
        AlipayTradeQueryRequest alipayTradeQueryRequest = new AlipayTradeQueryRequest();
        alipayTradeQueryRequest.setBizContent("{\"out_trade_no\":\"" + paymentInfo.getAlipayTradeNo() + "\"}");
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(alipayTradeQueryRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }


        if (null != response && response.isSuccess()) {
            String tradeStatus = response.getTradeStatus();
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                LOG.info("支付完成");
                //如果结果是支付成功 ,则更新支付状态
                PaymentInfo paymentInfo4Upt = new PaymentInfo();
                paymentInfo4Upt.setPaymentStatus("已支付");
                paymentInfo4Upt.setCallbackTime(new Date());
                paymentInfo4Upt.setCallbackContent(response.getBody());
                paymentInfo4Upt.setId(paymentInfo.getId());
                updateByPrimaryKeySelective(paymentInfo4Upt);

                // 然后发送通知给订单
                sendPaymentResult(paymentInfo.getOrderId(), "success");
                return "已支付";
            } else {
                LOG.info("支付尚未完成");
                return "未付款";
            }
        }
        return "未付款";

    }

    @Override
    public void sendDelayPaymentResult(String outTradeNo, int delaySec, int checkCount) {
        //发送支付结果
        Connection connection = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue paymentResultQueue = session.createQueue("PAYMENT_RESULT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(paymentResultQueue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("outTradeNo", outTradeNo);
            mapMessage.setInt("delaySec", delaySec);
            mapMessage.setInt("checkCount", checkCount);
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delaySec * 1000);
            producer.send(mapMessage);

            session.commit();
            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closePayment(Long orderId) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderId", orderId);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus("close");
        paymentInfoDao.updateByExampleSelective(paymentInfo, example);
    }

}
