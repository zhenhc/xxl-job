package com.xxl.job.admin.core.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : zhenhc
 * @date : 2022-04-09 10:17
 **/
public class ReflectUtil {

    //反射获取实体类对象的字段名称
    public static List<String> getFieldList(Class<?> beanClass){
        Field[] fields = cn.hutool.core.util.ReflectUtil.getFields(beanClass);
        List<String> collect = Arrays.stream(fields).map(field -> field.getName())
                .collect(Collectors.toList());
        return collect;
    }

    //反射获取实体类对象的字段名称并加上前缀
    public static List<String> getFieldList(Class<?> beanClass,String suffix){
        Field[] fields = cn.hutool.core.util.ReflectUtil.getFields(beanClass);
        List<String> collect = Arrays.stream(fields).map(field -> suffix+"_"+field.getName())
                .collect(Collectors.toList());
        return collect;
    }
}
