//package com.jing.controller.consumer;
//
//import com.jing.service.PaymentService;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.jms.JMSException;
//import javax.jms.MapMessage;
//
///**
// * @author Admin
// * @date 2020/3/28 9:56
// */
//@Component
//public class PaymentConsumer {
//    @Resource
//    private PaymentService paymentService;
//
//    @JmsListener(destination = "PAYMENT_RESULT_CHECK_QUEUE", containerFactory = "jmsQueueListener")
//    public void consumeCheckResult(MapMessage mapMessage) {
//        int delaySec = 0;
//        String outTradeNo = null;
//        int checkCount = 0;
//        try {
//            delaySec = mapMessage.getInt("delaySec");
//            outTradeNo = mapMessage.getString("outTradeNo");
//            checkCount = mapMessage.getInt("checkCount");
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//
//        PaymentInfo paymentInfo = new PaymentInfo();
//        paymentInfo.setAlipayTradeNo(outTradeNo);
//        String paymentStatus = paymentService.checkAlipayPayment(paymentInfo);
//        if ("未付款".equals(paymentStatus) && checkCount > 0) {
//            System.out.println("checkCount = " + checkCount);
//            paymentService.sendDelayPaymentResult(outTradeNo, delaySec, checkCount - 1);
//        }
//
//    }
//
//}
