//package com.jing.controller;
//
//import com.jing.service.UserService;
//import com.jing.util.HttpClientUtil;
//import com.jing.util.JsonUtil;
//import com.jing.util.JwtUtil;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author Admin
// * @date 2020/3/12 21:02
// */
//@Controller
//public class PassPortController {
//
//    @Resource
//    private UserService userService;
//
//    @RequestMapping("/vlogin")
//    public String vlogin(@RequestParam("code") String authorizationCode, HttpServletRequest request) {
//        //1 用授权码authorizationCode换取access_token
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("client_id", "4246669400");
//        paramMap.put("client_secret", "f00d6de6e4bc3f04fa5353b9dc5da744");
//        paramMap.put("grant_type", "authorization_code");
//        paramMap.put("redirect_uri", "http://127.0.0.1:9000/vlogin");
//        paramMap.put("code", authorizationCode);
//        String doPost = HttpClientUtil.doPost("https://api.weibo.com/oauth2/access_token", paramMap);
//        Map<String, Object> map = JsonUtil.json2Map(doPost);
//        String accessToken = (String) map.get("access_token");
//        String uid = (String) map.get("uid");
//        //2 用户access_token换取用户信息
//        String doGet = HttpClientUtil.doGet("https://api.weibo.com/2/users/show.json?uid=" + uid + "&access_token=" + accessToken);
//        Map<String, Object> userMap = JsonUtil.json2Map(doGet);
//        Long userId = Long.valueOf(String.valueOf(userMap.get("id")));
//        //3 将用户信息保存至会员表,用户类型设置为新浪微博用户
//        UmsMember umsMember = new UmsMember();
//        umsMember.setSourceUid(userId);
//        umsMember.setSourceType(2);
//        umsMember.setAccessToken(accessToken);
//        umsMember.setAccessCode(authorizationCode);
//        umsMember.setCity((String) userMap.get("location"));
//        umsMember.setNickname((String) userMap.get("screen_name"));
//        String gender = String.valueOf(userMap.get("gender"));
//        if ("m".equals(gender)) {
//            umsMember.setGender(1);
//        } else {
//            umsMember.setGender(2);
//        }
//        UmsMember umsMemberCheck = userService.checkOAuthUserExist(userId);
//        if (null == umsMemberCheck) {
//            //之前未登陆过
//            Long memberId = userService.saveOAuthUserInfo(umsMember);
//            umsMember.setId(memberId);
//            umsMemberCheck = umsMember;
//        }
//        //4 生成jwt的token,并携带token重定向至首页
//        String token = generateToken(request, umsMember);
//        return "redirect:http://127.0.0.1:9000/index?token=" + token;
//    }
//
//    @ResponseBody
//    @RequestMapping("/verify")
//    public Map<String, Object> verify(String token, String currentIp) {
//        Map<String, Object> userMap = JwtUtil.decode(token, "gmall", currentIp);
//        if (null == userMap) {
//            return null;
//        }
//        userMap.put("status", "success");
//        return userMap;
////        return JsonUtil.map2Json(userMap);
//    }
//
//    /**
//     * 登录方法
//     *
//     * @param umsMember 会员信息
//     */
//    @ResponseBody
//    @RequestMapping("/login")
//    public String login(UmsMember umsMember, HttpServletRequest request) {
//        String token;
//        UmsMember login = userService.login(umsMember);
//        if (null == login) {
//            token = "failed";
//            return token;
//        }
//        token = generateToken(request, login);
//        userService.saveToken(token, login.getId());
//        return token;
//    }
//
//    private String generateToken(HttpServletRequest request, UmsMember login) {
//        String token;
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put("memberId", login.getId());
//        userMap.put("nickName", login.getNickname());
//        String ip = request.getHeader("x-forwarded-for");
//        ip = ip == null ? request.getRemoteAddr() : ip;
//        ip = ip == null ? "127.0.0.1" : ip;
//        token = JwtUtil.encode("gmall", userMap, ip);
//        return token;
//    }
//
//    /**
//     * 跳转登录页面
//     *
//     * @param returnUrl 登陆成功跳转的页面
//     * @param map       页面写值
//     * @return 登录页面
//     */
//    @RequestMapping("/toLogin")
//    public String tologin(String returnUrl, ModelMap map) {
//        map.put("returnUrl", returnUrl);
//        return "login";
//    }
//}
