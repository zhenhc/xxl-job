package com.xxl.job.admin.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.xxl.job.admin.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhenhc
 * @date : 2022-03-27 23:54
 **/
public class ControllerTest {

    private Jedis jedis;
    private String cookie;

    @BeforeEach
    private void init(){
        jedis = new Jedis("10.3.24.133");
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
    public void pageListTest(){
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
}
