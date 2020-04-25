package com.jing.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.kit.StrKit;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Whyn
 * @date 2020/2/22 11:48
 */
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 对象转json
     *
     * @param obj
     * @return
     * @throws IOException
     */
    public static String obj2Json(Object obj) {
        if (null == obj)
            return null;
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    /**
     * json转对象
     *
     * @param jsonStr
     * @param objClass
     * @param <T>
     * @throws IOException
     */
    public static <T> T json2Obj(String jsonStr, Class<T> objClass) {
        if (StringUtils.isEmpty(jsonStr) || null == objClass)
            return null;
        T obj = null;
        try {
            obj = mapper.readValue(jsonStr, objClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * json转list
     *
     * @return
     */
    public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz) {
        JavaType javaType = getCollectionType(List.class, clazz);
        List<T> list = null;
        try {
            list = mapper.readValue(jsonArrayStr, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json转set
     *
     * @return
     */
    public static <T> Set<T> json2Set(String jsonArrayStr, Class<T> clazz) {
        JavaType javaType = getCollectionType(Set.class, clazz);
        Set<T> list = null;
        try {
            list = mapper.readValue(jsonArrayStr, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 集合转json
     */
    public static String collection2Json(Collection<?> list) {
        if (CollectionUtils.isEmpty(list))
            return null;
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    /**
     * map转json
     */
    public static String map2Json(Map<?, ?> list) {
        if (CollectionUtils.isEmpty(list))
            return null;
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public static void main(String[] args) {
//        PmsProductInfo pmsProductInfo = new PmsProductInfo();
//        pmsProductInfo.setProductName("1");
//        PmsProductInfo pmsProductInfo2 = new PmsProductInfo();
//        pmsProductInfo2.setProductName("1");
//        Set<PmsProductInfo> list = new HashSet<>();
//        list.add(pmsProductInfo);
//        list.add(pmsProductInfo2);
//        Map<String, Object> map = new HashMap<>();
//        map.put("1", 1);
//        map.put("2", pmsProductInfo);
//        String s = map2Json(map);
        System.out.println(obj2Json(""));
//        String s = obj2JsonStr(pmsProductInfo);
//        System.out.println(s);
//        String s = "{\"id\":null,\"productName\":\"1\",\"description\":null,\"catalog3Id\":null,\"tmId\":null}";
//        PmsProductInfo pmsProductInfo = jsonStr2Obj(s, PmsProductInfo.class);
//        System.out.println(pmsProductInfo.toString());
    }

    /**
     * json转map
     */
    public static Map<String, Object> json2Map(String jsonStr) {
        if (StrKit.isBlank(jsonStr))
            return null;
        Map<String, Object> map = null;
        try {
            map = (Map<String, Object>) mapper.readValue(jsonStr, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> json2MapStr(String jsonStr) {
        if (StrKit.isBlank(jsonStr))
            return null;
        Map<String, String> map = null;
        try {
            map = (Map<String, String>) mapper.readValue(jsonStr, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
