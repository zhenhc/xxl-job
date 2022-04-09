package com.xxl.job.admin.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : zhenhc
 * @date : 2022-03-27 23:54
 **/
@Slf4j
public class ControllerTest {

    private Jedis jedis;
    private String cookie;

    @BeforeEach
    private void init(){
        Resource resourceObj = ResourceUtil.getResourceObj("application.properties");
        BufferedReader reader = resourceObj.getReader(Charset.defaultCharset());
        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String property = properties.getProperty("spring.redis.host");
        jedis = new Jedis(property);
        cookie = jedis.get(LoginService.REDIS_COOKIE);
    }
    @Test
    public void test(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startDate = "2022-03-21 00:00:00";
        String endDate = "2022-03-28 23:59:59";
        Date start = null;
        Date end = null;
        try {
            start = simpleDateFormat.parse(startDate);
            end = simpleDateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("startDate",start);
        map.put("endDate",end);
        String body = HttpRequest.get("http://localhost:8080/xxl-job-admin/chartInfo")
                .cookie("xxljob_adminlte_settings=on; JSESSIONID=node01jcmyn6wg0ecb1fzavzu8h69ug8.node0; XXL_JOB_LOGIN_IDENTITY=7b226964223a312c22757365726e616d65223a2261646d696e222c2270617373776f7264223a226531306164633339343962613539616262653536653035376632306638383365222c22726f6c65223a312c227065726d697373696f6e223a6e756c6c7d")
                .body(JSONUtil.toJsonStr(map))
                .execute()
                .body();
        System.out.println(body);
    }

    @Test
    public void loginTest(){
        Map<String,Object> map = new HashMap<>();
        map.put("userName","admin");
        map.put("password","123456");
        String body = HttpRequest.post("http://localhost:8080/xxl-job-admin/login")
                .form("userName","admin")
                .form("password","123456")
                .execute()
                .body();
        System.out.println(body);
    }

    @Test
    public void jobInfoPageListTest(){
        Map<String,Object> map = new HashMap();
        map.put("jobGroup",1);
        map.put("triggerStatus",-1);
        String body = HttpRequest.post("http://localhost:8080/xxl-job-admin/jobinfo/pageList")
                .cookie(cookie)
                .form(map)
                .execute()
                .body();
        System.out.println(body);
    }

    @Test
    public void chartInfoTest(){
        String body = HttpRequest.post("http://localhost:8080/xxl-job-admin/chartInfo")
                .cookie(cookie)
                .execute()
                .body();
        System.out.println(body);
    }

    @Test
    public void jobLogPageListTest(){
        Map<String,Object> map = new HashMap();
        map.put("jobGroup",0);
        map.put("jobId",0);
        map.put("logStatus",-1);
        String body = HttpRequest.post("http://localhost:8080/xxl-job-admin/joblog/pageList")
                .cookie(cookie)
                .form(map)
                .execute()
                .body();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        JSONArray data = jsonObject.getJSONArray("data");
        List<Object> list = data;
        List<Map<String, String>> collect = list.stream().map(o -> {
            JSONObject o1 = (JSONObject) o;
            Map<String, String> map1 = new HashMap<>();
            map1.put("id", o1.getStr("id"));
            return map1;
        }).collect(Collectors.toList());
        System.out.println(JSONUtil.toJsonStr(collect));
        FileUtil.writeString(body,new File("doc/json/jobLog.json"), Charset.defaultCharset());
    }

    @Test
    public void test1(){
        File file = new File("doc");
        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);
        System.out.println(System.getProperty("user.dir"));

        Resource resourceObj = ResourceUtil.getResourceObj("application.properties");
        BufferedReader reader = resourceObj.getReader(Charset.defaultCharset());
        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String property = properties.getProperty("spring.redis.host");
        System.out.println(property);
    }

    @Test
    public void test2(){
        for (int i=0;i<10;i++) {
            DateTime dateTime = RandomUtil.randomDate(null, DateField.DAY_OF_MONTH, -1000, 1000);
            System.out.println(dateTime);
        }
    }

    @Test
    public void test3(){
        Field[] fields = ReflectUtil.getFields(XxlJobInfo.class);
        List<String> collect = Arrays.stream(fields).map(field -> "xji_"+field.getName())
                        .collect(Collectors.toList());
        //System.out.println(collect);

        List<String> fieldList = com.xxl.job.admin.core.util.ReflectUtil.getFieldList(XxlJobInfo.class,"xji");
        System.out.println(fieldList);
    }
}
