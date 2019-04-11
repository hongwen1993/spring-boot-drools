package com.drools.drools01.task;

import com.drools.drools01.utils.CacheManager;
import com.drools.drools01.utils.KieSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class CacheRule {

    @Autowired
    CacheManager cacheManager;

    @Scheduled(cron = "0/10 * * * * *")
    public void readAndWrite() throws FileNotFoundException {
        String scoreRule = KieSessionUtils.getDRL("C:\\DROOLS\\score_sign.xls");
        cacheManager.put("score_sign", scoreRule);
    }

}
