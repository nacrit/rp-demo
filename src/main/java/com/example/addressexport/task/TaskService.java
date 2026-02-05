package com.example.addressexport.task;

import com.example.addressexport.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@EnableScheduling
@Slf4j
public class TaskService {
    @Scheduled(cron = "0/37 * * * * ?")
    private void platformAssets() {
        log.info("clean expire token ..");
        LoginUtil.cleanExpireToken();
    }

}
