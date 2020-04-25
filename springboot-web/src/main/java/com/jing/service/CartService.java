package com.jing.service;

import com.example.demo.entity.OmsCartItem;

import java.util.List;

/**
 * @author Admin
 * @title: CartService
 * @projectName demo
 * @description: TODO
 * @date 2020/3/7 13:39
 */
public interface CartService {
    OmsCartItem getCartByMemberIdAndSkuId(Long memberId, Long skuId);

    int updateCart(OmsCartItem omsCartItemFromDB);

    int addCart(OmsCartItem omsCartItem);

    void sync(Long omsCartItemFromDB);

    List<OmsCartItem> getCartByMemberId(Long memberId);

    void checkCart(Long memberId, long skuId, int isChecked);

    int deleteBoughtProducts(Long memberId, List<OmsCartItem> boughtProductIdList);
}
