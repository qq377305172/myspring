package com.jing.controller;

import com.example.demo.annotations.LoginRequired;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.util.CommonUtil;
import com.example.demo.util.CookieUtil;
import com.example.demo.util.JsonUtil;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Admin
 * @title: CartController
 * @projectName demo
 * @description: TODO
 * @date 2020/3/3 16:34
 */
@Controller
public class CartController {
    @Resource
    private AttrService attrService;
    @Resource
    private CartService cartService;
    @Resource
    private UserService userService;
    @Resource
    private OrderService orderService;

    @JmsListener(destination = "DEDUCT", containerFactory = "jmsQueueListener")
    public void consumeSkuDeduct(OmsOrder omsOrder) {
        Long orderId = omsOrder.getId();
        int status = omsOrder.getStatus();
        int updateResult;
        if (status == 2) {
            updateResult = orderService.updateProcessStatus(orderId.toString(), 2);
        } else {
            updateResult = orderService.updateProcessStatus(orderId.toString(), 1);
        }
    }

    @JmsListener(destination = "PAYMENT_RESULT_QUEUE", containerFactory = "jmsQueueListener")
    public void consumePaymentResult(MapMessage mapMessage) {
        String orderId = null;
        String result = null;
        try {
            orderId = mapMessage.getString("orderId");
            result = mapMessage.getString("result");
        } catch (JMSException e) {
            e.printStackTrace();
        }
        int updateResult;
        if ("success".equals(result)) {
            updateResult = orderService.updateProcessStatus(orderId, 1);
        } else {
            updateResult = orderService.updateProcessStatus(orderId, 0);
        }
        orderService.sendOrderResult(orderId);
    }

    @LoginRequired
    @RequestMapping("/submitOrder")
    public ModelAndView submitOrder(Long receiveAddressId, String tradeNo, HttpServletRequest request,
                                    HttpServletResponse response, ModelMap modelMap) {
        Long memberId = CommonUtil.getMemberId(request);
        //检验交易码,此举为了防止同一订单重复提交,检验不通过则不允许提交
        boolean checkTradeNo = orderService.checkTradeNo(memberId, tradeNo);
        if (!checkTradeNo) {
            return null;
        }
        String nickName = getNickName(request);
        //1 获取要购买的商品列表
        List<OmsCartItem> omsCartItemList = cartService.getCartByMemberId(memberId);
//        List<Long> skuIds = new ArrayList<>();
//        for (OmsCartItem omsCartItem : omsCartItemList) {
//            if (omsCartItem.getIsChecked() == 1) {
//                Long productSkuId = omsCartItem.getProductSkuId();
//                skuIds.add(productSkuId);
//            }
//        }
//        Map<Long, PmsSkuInfo> skuIdSkuInfoMapping = new HashMap<>();
//        List<PmsSkuInfo> skuInfoList = productService.listSkuInfoBySkuIds(skuIds);
//        for (PmsSkuInfo pmsSkuInfo : skuInfoList) {
//            skuIdSkuInfoMapping.put(pmsSkuInfo.getId(), pmsSkuInfo);
//        }
        //要购买的商品id集合
        List<OmsCartItem> BoughtProductList = new ArrayList<>();
        //订单详情对象集合
        List<OmsOrderItem> omsOrderItemList = new ArrayList<>();

        String extOrderNo = "gmall" + System.currentTimeMillis() + DateFormatUtils.format(new Date(), "yyyyMMdd");
        for (OmsCartItem omsCartItem : omsCartItemList) {
            //只封装选中的商品
            if (omsCartItem.getIsChecked() == 1) {
//                Long productSkuId = omsCartItem.getProductSkuId();
//                PmsSkuInfo pmsSkuInfo = skuIdSkuInfoMapping.get(productSkuId);
                //检验价格,检验不通过则提交失败
                if (orderService.checkPrice(omsCartItem.getProductSkuId(), omsCartItem.getPrice())) {
                    //价格异常
                    return null;
                }
                //封装订单详情对象
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductPrice(omsCartItem.getPrice());
                omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                //外部订单号,用来和其他系统进行交互,防止重复
                omsOrderItem.setOrderSn(extOrderNo);
                omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                omsOrderItem.setProductSkuCode("");
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setProductSn("");
                omsOrderItemList.add(omsOrderItem);

                BoughtProductList.add(omsCartItem);
            }
        }

        modelMap.put("omsOrderItemList", omsOrderItemList);

        //获取总价格
        BigDecimal totalAmount = CalculationTotalAmount(omsCartItemList);
        //2 保存订单
        //封装订单对象
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOmsOrderItemList(omsOrderItemList);
        //自动确认时间
        omsOrder.setAutoConfirmDay(7);
        //会员id
        omsOrder.setMemberId(memberId);
        //会员昵称
        omsOrder.setMemberUsername(nickName);
        //外部订单号
        omsOrder.setOrderSn(extOrderNo);
        omsOrder.setPayAmount(totalAmount);
        omsOrder.setOrderType(1);

        //收货信息
        UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getReceiveAddressById(receiveAddressId);
        omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
        omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
        omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
        omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
        omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
        omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
        omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
        omsOrder.setReceiveTime(DateUtils.addDays(new Date(), 1));
        omsOrder.setSourceType(0);
        omsOrder.setStatus(0);
        omsOrder.setTotalAmount(totalAmount);

        //创建时间
        omsOrder.setCreateTime(new Date());
        int saveResult = orderService.saveOrder(omsOrder);
        if (saveResult == 0)
            return null;
        //3 从购物车中删除订单购买的商品
//        int deleteResult = cartService.deleteBoughtProducts(memberId, BoughtProductList);


        //删除缓存中的交易码
        boolean b = orderService.delTradeNo(memberId, tradeNo);
        if (!b) {
            return null;
        }
        //4 重定向到支付页面
        ModelAndView modelAndView = new ModelAndView("redirect:http://127.0.0.1:9000/payment_select");
        modelAndView.addObject("tradeNo", tradeNo);
        modelAndView.addObject("totalAmount", totalAmount);
        return modelAndView;
    }


    @LoginRequired()
    @RequestMapping("/toTrade")
    public String toTrade(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap) {
        Long memberId = CommonUtil.getMemberId(request);
        String nickName = getNickName(request);
        modelMap.put("nickName", nickName);
        //1 查询收件人地址列表
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = userService.listReceiveAddressByMemberId(memberId);
//        userAddressList
        modelMap.put("userAddressList", umsMemberReceiveAddressList);
        //2 查询购物车集合
        List<OmsCartItem> omsCartItemList = cartService.getCartByMemberId(memberId);
//        orderDetailList
        List<OmsOrderItem> orderDetailList = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItemList) {
            if (omsCartItem.getIsChecked() == 1) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductId(omsCartItem.getProductId());
                orderDetailList.add(omsOrderItem);
            }
        }
        BigDecimal totalAmount = CalculationTotalAmount(omsCartItemList);
        modelMap.put("totalAmount", totalAmount);
        modelMap.put("orderDetailList", orderDetailList);
        String tradeNo = orderService.genTradeNo(memberId);
        modelMap.put("tradeNo", tradeNo);
        return "trade";
    }

    private String getNickName(HttpServletRequest request) {
        return String.valueOf(request.getAttribute("nickName"));
    }

    @LoginRequired(loginSuccess = false)
    @RequestMapping("/checkCart")
    public String checkCart(@RequestParam("isChecked") int isChecked, @RequestParam("skuId") long skuId,
                            ModelMap modelMap, HttpServletRequest request) {
        Long memberId = CommonUtil.getMemberId(request);
        if (memberId == null) {

        } else {
            cartService.checkCart(memberId, skuId, isChecked);
        }
        List<OmsCartItem> omsCartItemList = cartService.getCartByMemberId(memberId);
        //被勾选的商品总额
        BigDecimal totalAmount = CalculationTotalAmount(omsCartItemList);
        modelMap.put("totalAmount", totalAmount);
        modelMap.put("cartList", omsCartItemList);
        return "cartListInner";
    }

    @LoginRequired(loginSuccess = false)
    @RequestMapping("/cartList")
    public String cartList(HttpServletRequest request,
                           ModelMap modelMap) {
        List<OmsCartItem> omsCartItemList = null;
        long memberId = CommonUtil.getMemberId(request);
        if (0 == memberId) {
            //未登录,查询缓存
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StrKit.notBlank(cartListCookie)) {
                omsCartItemList = JsonUtil.json2list(cartListCookie, OmsCartItem.class);
            }
        } else {
            //已登录,查询数据库
            omsCartItemList = cartService.getCartByMemberId(memberId);
        }
        modelMap.put("cartList", omsCartItemList);
        //被勾选的商品总额
        BigDecimal totalAmount = CalculationTotalAmount(omsCartItemList);
        modelMap.put("totalAmount", totalAmount);
        return "cartList";
    }

    private BigDecimal CalculationTotalAmount(List<OmsCartItem> omsCartItemList) {
        BigDecimal totalAmount = new BigDecimal(0);
        if (null == omsCartItemList)
            return totalAmount;
        for (OmsCartItem omsCartItem : omsCartItemList) {
            if (omsCartItem.getIsChecked() == 1)
                totalAmount = totalAmount.add(omsCartItem.getPrice());
        }
        return totalAmount;
    }

    @LoginRequired(loginSuccess = false)
    @RequestMapping("/addToCart")
    public String addToCart(@RequestParam(name = "quantity") BigDecimal quantity, @RequestParam("skuId") Long skuId, HttpServletRequest request,
                            HttpServletResponse response) {
        Long memberId = CommonUtil.getMemberId(request);
        PmsSkuInfo pmsSkuInfo = attrService.getSkuById(skuId);
        if (null == pmsSkuInfo)
            return "redirect:/toSuccess";
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(quantity);
        if (memberId == null) {
            //未登录
            //将购物车信息存入cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            List<OmsCartItem> omsCartItems = new ArrayList<>();
            if (StrKit.notBlank(cartListCookie)) {
                omsCartItems = JsonUtil.json2list(cartListCookie, OmsCartItem.class);
                handle(omsCartItem, omsCartItems);
            } else {
                omsCartItems.add(omsCartItem);
            }
            CookieUtil.setCookie(request, response, "cartListCookie", JsonUtil.collection2Json(omsCartItems), 60 * 60 * 24 * 3, true);
        } else {
            //已登录
            //将购物车信息存入数据库和缓存
            //根据当前用户id和商品skuId查询购物车
            OmsCartItem omsCartItemFromDB = cartService.getCartByMemberIdAndSkuId(memberId, skuId);
            if (null != omsCartItemFromDB) {
                //购物车中存在
                omsCartItemFromDB.setQuantity(omsCartItemFromDB.getQuantity().add(quantity));
                cartService.updateCart(omsCartItemFromDB);
            } else {
                //购物车中不存在
                omsCartItem.setMemberId(memberId);
                cartService.addCart(omsCartItem);
            }
            //更新缓存
            cartService.sync(memberId);
        }
        return "redirect:/toSuccess";
    }

    private void handle(OmsCartItem omsCartItem, List<OmsCartItem> omsCartItems) {
        boolean flag = false;
        for (OmsCartItem item : omsCartItems) {
            if (item.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                item.setQuantity(item.getQuantity().add(omsCartItem.getQuantity()));
                item.setPrice(item.getPrice().add(omsCartItem.getPrice()));
                flag = true;
            }
        }
        if (!flag)
            omsCartItems.add(omsCartItem);
    }

    @LoginRequired(loginSuccess = false)
    @RequestMapping("/toSuccess")
    public String toSuccess() {
        return "success";
    }
}
