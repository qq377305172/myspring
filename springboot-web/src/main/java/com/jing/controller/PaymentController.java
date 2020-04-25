package com.jing.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.example.demo.annotations.LoginRequired;
import com.example.demo.config.AlipayConfig;
import com.example.demo.entity.OmsOrder;
import com.example.demo.entity.PaymentInfo;
import com.example.demo.service.OrderService;
import com.example.demo.service.PaymentService;
import com.example.demo.util.CommonUtil;
import com.example.demo.util.JsonUtil;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Admin
 * @title: PaymentController
 * @projectName demo
 * @description: TODO
 * @date 2020/3/23 20:14
 */
@Controller
public class PaymentController {
    private final Logger LOG = LoggerFactory.getLogger(PaymentController.class);
    @Resource
    private AlipayClient alipayClient;
    @Resource
    private PaymentService paymentService;
    @Resource
    private OrderService orderService;



    @RequestMapping("/sendResult")
    @LoginRequired
    public String sendResult(@RequestParam Long orderId) {
        paymentService.sendPaymentResult(orderId, "success");
        return null;
    }

    @RequestMapping("/alipay/callback/return")
    @LoginRequired
    public String alipayCallbackReturn(Long memberId, String nickName, HttpServletRequest request, ModelMap modelMap) {
        String sign = request.getParameter("sign");
        if (StrKit.isBlank(sign)) {
            return null;
        }
        String tradeNo = request.getParameter("trade_no");
        String outTradeNo = request.getParameter("out_trade_no");
        String tradeStatus = request.getParameter("trade_status");
        String totalAmount = request.getParameter("total_amount");
        String callBackContent = request.getQueryString();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setTotalAmount(new BigDecimal(totalAmount));
        paymentInfo.setCallbackContent(callBackContent);
        paymentInfo.setOrderSn(tradeNo);
        paymentInfo.setAlipayTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setCallbackTime(new Date());
        int update = paymentService.update(paymentInfo);
        return "finish";
    }

    @RequestMapping("/alipay/submit")
    @LoginRequired
    public String alipaySubmit(Long memberId, String nickName, String tradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        String result = null;
        Map<String, String> map = new HashMap<>();
        map.put("out_trade_no", tradeNo);
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", totalAmount.toString());
        map.put("subject", "小米");


        String json = JsonUtil.map2Json(map);
        AlipayTradeAppPayRequest alipayTradeAppPayRequest = new AlipayTradeAppPayRequest();
        alipayTradeAppPayRequest.setBizContent(json);
        //回调函数
        alipayTradeAppPayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayTradeAppPayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        try {
            result = alipayClient.pageExecute(alipayTradeAppPayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        OmsOrder omsOrder = orderService.getOrderByTradeNo(tradeNo);
        //生成并保存用户支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setAlipayTradeNo(tradeNo);
        paymentInfo.setPaymentStatus("未付款");
        paymentInfo.setSubject("小米");
        paymentInfo.setTotalAmount(totalAmount);
        paymentInfo.setCreateTime(new Date());
        Long id = paymentService.save(paymentInfo);
        return result;
    }

    @RequestMapping("/payment_select")
    @LoginRequired
    public String payment_select(String tradeNo, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        Long memberId = CommonUtil.getMemberId(request);
        String nickName = CommonUtil.getNickName(request);
        modelMap.put("memberId", memberId);
        modelMap.put("nickName", nickName);
        modelMap.put("tradeNo", tradeNo);
        modelMap.put("totalAmount", totalAmount);
        return "payment_select";
    }
}
