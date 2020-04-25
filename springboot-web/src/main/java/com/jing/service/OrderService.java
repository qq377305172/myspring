package com.jing.service;

import com.example.demo.entity.OmsOrder;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Admin
 * @title: OrderService
 * @projectName demo
 * @description: TODO
 * @date 2020/3/21 18:06
 */
public interface OrderService {
    String genTradeNo(Long memberId);

    boolean checkTradeNo(Long memberId, String tradeNo);

    boolean delTradeNo(Long memberId, String tradeNo);

    boolean checkPrice(Long productSkuId, BigDecimal price);

    int saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderByTradeNo(String tradeNo);

    int updateProcessStatus(String orderId, int status);

    void sendOrderResult(String orderId);

    List<OmsOrder> getUnpaidOrderList();

    void checkExpireOrder(OmsOrder orderInfo);
}
