package com.jing.thread;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author cj
 * @date 2020/4/13 15:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class User {
    private int age;
    private String name;
}

public class AtomicReferenceDemo {
    public static void main(String[] args) {
        User user = new User(1, "z");
        AtomicReference<User> atomicReference = new AtomicReference<>();
        atomicReference.set(user);
        atomicReference.compareAndSet(user, new User(2, "a"));
        System.out.println(atomicReference.get());
        Map<String,Object> map = new HashMap<>();
        map.put("age",1);
        map.put("name","asd");
        User bean = BeanUtil.toBean(map, User.class);
        System.out.println(bean);
    }
}
