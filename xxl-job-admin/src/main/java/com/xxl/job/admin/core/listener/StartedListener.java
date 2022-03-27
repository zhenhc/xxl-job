package com.xxl.job.admin.core.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * The method executed after the application is started.
 *
 * @author ryanwang
 * @author guqing
 * @date 2018-12-05
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StartedListener implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${server.port}")
    private String port;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        this.printStartInfo();
    }

    private void printStartInfo() {
        String blogUrl = "http://127.0.0.1:"+port+contextPath;
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "xxl-job started at         ", blogUrl));
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "username:","admin"));
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "password:","123456"));
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, "xxl-job has started successfully!"));
    }
}
