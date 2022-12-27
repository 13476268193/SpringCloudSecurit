package com.base.core.util;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.base.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class BeanMapUtils {
    /**
     * 将对象装换为map，支持驼峰命名转下划线
     * 支持hutool的@Alias注解
     * @param bean 要转换的类
     * @param isUnderscore 是否转成蛇形命名
     * @return
     */
    public static Map<String, Object> bean2Map(Object bean, boolean isUnderscore) {
        Map<String, Object> map;
        if (isUnderscore) {
            String s = JSONUtil.toJsonStr(bean);
            bean = JSONUtil.parseObj(s);
            try {
                map = formatKey((JSONObject) bean, false);
            } catch (Exception e) {
                log.error("bean2Map 对象装换为map出现错误！！！e:", e);
                throw new BusinessException("公共类错误");
            }
        }else {
            map = BeanUtil.beanToMap(bean, false, false);
        }
        return map;
    }

    /**
     * 转换为驼峰格式/转换为下划线方式
     *
     * @param json 等待转换的方法
     * @param upper 首字母大写或者小写
     * @return 转换后的
     */
    public static JSONObject formatKey(final JSONObject json, boolean upper) {
        JSONObject real = new JSONObject();
        for (String it : json.keySet()) {
            Object objR = json.get(it);
            // 转换为驼峰格式
            String key = StrUtil.toUnderlineCase(it);
            // 首字母大写或者小写
            key = upper ? StrUtil.upperFirst(key) : StrUtil.lowerFirst(key);
            if (objR instanceof JSONObject) {
                real.set(key, formatKey((JSONObject) objR, upper));
            }else if (objR instanceof JSONArray) {
                JSONArray jsonA = new JSONArray();
                for (Object objA : (JSONArray) objR) {
                    jsonA.add(formatKey((JSONObject) objA, upper));
                }
                real.set(key, jsonA);
            }else {
                real.set(key, objR);
            }
        }
        return real;
    }
}


