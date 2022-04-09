package com.xxl.job.admin.controller;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.*;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.awt.datatransfer.Clipboard;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        //System.out.println(fieldList);

        String content = "ZZZaaabbbccc中文1234";
        List<String> resultFindAll = ReUtil.findAll("\\w{2}", content, 0, new ArrayList<String>());
        //System.out.println(resultFindAll);

        Set<Class<?>> classes = ClassUtil.scanPackage("cn.hutool");
        Map<String, Object> stringObjectHashMap = new HashMap<>();

        Map<String, Object> map1 = classes.stream()
                .filter(aClass -> aClass.getName().endsWith("Util"))
                .map(aClass -> {
                    String name = aClass.getName();
                    List<String> fieldList1 = com.xxl.job.admin.core.util.ReflectUtil.getFieldList(name.getClass());
                    Method[] methods = ReflectUtil.getMethods(name.getClass());
                    List<String> methodList = Arrays.stream(methods).map(method -> method.getName())
                            .collect(Collectors.toList());
                    Map<String, List<String>> stringListMap = new HashMap<>();
                    stringListMap.put("fieldList", fieldList1);
                    stringListMap.put("methodList", methodList);
                    stringObjectHashMap.put(name, stringListMap);
                    return stringObjectHashMap;
                })
                .collect(Collectors.toList()).get(0);
        Map<String,Object> map = new HashMap<>();
        map.put("Classs",map1);
        map.put("count",map1.size());
        System.out.println(JSONUtil.toJsonStr(map));
    }

    @Test
    public void test4(){
        //请求列表页
        String listContent = HttpUtil.get("https://www.oschina.net/action/ajax/get_more_news_list?newsType=&p=2");
        System.out.println(listContent);
        //使用正则获取所有标题
        List<String> titles = ReUtil.findAll("<span class=\"text-ellipsis\">(.*?)</span>", listContent, 1);
        for (String title : titles) {
            //打印标题
            Console.log(title);
        }
    }

    @Test
    public void test5(){
        List<File> files = FileUtil.loopFiles(new File("D:\\developer"), 3, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        });
        System.out.println(JSONUtil.toJsonStr(files));
    }

    @Test
    public void test6(){
//定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

//图形验证码写出，可以写出到文件，也可以写出到流
        lineCaptcha.write("E:/line.png");
//输出code
        Console.log(lineCaptcha.getCode());
//验证图形验证码的有效性，返回boolean值
        lineCaptcha.verify("1234");

//重新生成验证码
        lineCaptcha.createCode();
        lineCaptcha.write("E:/line.png");
//新的验证码
        Console.log(lineCaptcha.getCode());
//验证图形验证码的有效性，返回boolean值
        lineCaptcha.verify("1234");
    }
}
