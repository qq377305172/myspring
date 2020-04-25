package com.jing.service;

import com.example.demo.entity.UmsMember;
import com.example.demo.entity.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @author Admin
 * @title: UserService
 * @projectName demo
 * @description: TODO
 * @date 2020/3/15 13:24
 */
public interface UserService {
    UmsMember login(UmsMember umsMember);

    void saveToken(String token, Long memberId);

    Long saveOAuthUserInfo(UmsMember umsMember);

    UmsMember checkOAuthUserExist(Long userId);

    List<UmsMemberReceiveAddress> listReceiveAddressByMemberId(Long memberId);

    UmsMemberReceiveAddress getReceiveAddressById(Long receiveAddressId);
}
