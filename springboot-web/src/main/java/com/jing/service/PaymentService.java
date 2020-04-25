package com.jing.service;

import com.example.demo.entity.PaymentInfo;

/**
 * @author Admin
 * @title: PaymentService
 * @projectName demo
 * @description: TODO
 * @date 2020/3/25 20:52
 */
public interface PaymentService {
    Long save(PaymentInfo paymentInfo);

    int update(PaymentInfo paymentInfo);

    void sendPaymentResult(Long orderId, String success);

    PaymentInfo getPaymentInfo(PaymentInfo paymentInfo);

    void updateByPrimaryKeySelective(PaymentInfo paymentInfo4Upt);

    String checkAlipayPayment(PaymentInfo paymentInfo);

    void sendDelayPaymentResult(String outTradeNo, int delaySec, int i);

    void closePayment(Long id);
}
