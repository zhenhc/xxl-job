package com.xxl.job.executor.sample.frameless.test;

import com.xxl.job.executor.sample.frameless.config.FrameLessXxlJobConfig;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class FramelessApplicationTest {

    @Test
    public void test(){
        System.out.println("111");
    }

    @Test
    public void readProperties(){
        Properties properties = FrameLessXxlJobConfig.loadProperties("xxl-job-executor.properties");
        for (Map.Entry<Object,Object> entry : properties.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }

    }

}
