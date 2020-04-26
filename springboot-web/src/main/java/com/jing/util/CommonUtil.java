package com.jing.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Admin
 * @date 2020/3/23 20:17
 */
public class CommonUtil {
    public static Long getMemberId(HttpServletRequest request) {
        return Integer.valueOf(String.valueOf(request.getAttribute("memberId"))).longValue();
    }

    public static String getNickName(HttpServletRequest request) {
        return String.valueOf(request.getAttribute("nickName"));
    }
}
