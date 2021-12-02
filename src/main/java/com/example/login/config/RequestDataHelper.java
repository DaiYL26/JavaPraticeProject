package com.example.login.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.Map;

public class RequestDataHelper {
    /**
     * 请求参数存取
     */
    private static final ThreadLocal<Map<String, Integer>> REQUEST_DATA = new ThreadLocal<>();

    /**
     * 设置请求参数
     *
     * @param requestData 请求参数 MAP 对象
     */
    public static void setRequestData(Map<String, Integer> requestData) {
        REQUEST_DATA.set(requestData);
    }

    /**
     * 获取请求参数
     *
     * @param param 请求参数
     * @return 请求参数 MAP 对象
     */
    public static <T> T getRequestData(String param) {
        Map<String, Integer> dataMap = getRequestData();
        if (CollectionUtils.isNotEmpty(dataMap)) {
            T res = (T) dataMap.get(param);
            dataMap.remove(param);
            return res;
        }
        return null;
    }

    /**
     * 获取请求参数
     *
     * @return 请求参数 MAP 对象
     */
    public static Map<String, Integer> getRequestData() {
        return REQUEST_DATA.get();
    }
}