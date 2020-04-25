package com.jing.service.impl;

import com.example.demo.dao.UmsMemberDao;
import com.example.demo.dao.UmsMemberReceiveAddressDao;
import com.example.demo.entity.UmsMember;
import com.example.demo.entity.UmsMemberReceiveAddress;
import com.example.demo.service.UserService;
import com.example.demo.util.JsonUtil;
import com.example.demo.util.RedisUtil;
import com.jfinal.kit.StrKit;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Admin
 * @title: UserServiceImpl
 * @projectName demo
 * @description: TODO
 * @date 2020/3/15 13:24
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UmsMemberDao umsMemberDao;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UmsMemberReceiveAddressDao umsMemberReceiveAddressDao;

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            if (null != jedis) {
                String umsMemberStr = jedis.get("user:" + umsMember.getPassword() + ":info");
                if (StrKit.notBlank(umsMemberStr)) {
                    return JsonUtil.json2Obj(umsMemberStr, UmsMember.class);
                }
            }

            UmsMember umsMemberFromDb = queryUmsMember(umsMember);
            if (null != jedis) {
                jedis.setex("user:" + umsMember.getPassword() + ":info", 60 * 60 * 24, JsonUtil.obj2Json(umsMemberFromDb));
            }
            return umsMemberFromDb;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public void saveToken(String token, Long memberId) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            if (null != jedis) {
                jedis.setex("user:" + memberId + ":info", 60 * 60 * 24, token);
            }

        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public Long saveOAuthUserInfo(UmsMember umsMember) {
        umsMemberDao.insertSelective(umsMember);
        return umsMember.getId();
    }

    @Override
    public UmsMember checkOAuthUserExist(Long userId) {
        Example example = new Example(UmsMember.class);
        example.createCriteria().andEqualTo("sourceUid", userId);
        return umsMemberDao.selectOneByExample(example);
    }

    @Override
    public List<UmsMemberReceiveAddress> listReceiveAddressByMemberId(Long memberId) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        return umsMemberReceiveAddressDao.selectByExample(example);
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(Long receiveAddressId) {
        return umsMemberReceiveAddressDao.selectByPrimaryKey(receiveAddressId);
    }

    private UmsMember queryUmsMember(UmsMember umsMember) {
        List<UmsMember> umsMembers = umsMemberDao.queryAll(umsMember);
        return null == umsMember ? null : umsMembers.get(0);
    }
}
