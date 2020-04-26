//package com.jing.util;
//
//import io.jsonwebtoken.*;
//
//import java.util.Map;
//
///**
// * @author Admin
// * @date 2020/3/14 14:23
// */
//public class JwtUtil {
//
//    public static String encode(String key, Map<String, Object> param, String salt) {
//        if (salt != null) {
//            key += salt;
//        }
//        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
//
//        jwtBuilder = jwtBuilder.setClaims(param);
//
//        return jwtBuilder.compact();
//
//    }
//
//
//    public static Map<String, Object> decode(String token, String key, String salt) {
//        Claims claims;
//        if (salt != null) {
//            key += salt;
//        }
//        try {
//            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
//        } catch (JwtException e) {
//            return null;
//        }
//        return claims;
//    }
//}
