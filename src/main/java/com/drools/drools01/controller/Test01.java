package com.drools.drools01.controller;

import com.drools.drools01.model.ScoreInfo;
import com.drools.drools01.utils.CacheManager;
import com.drools.drools01.utils.KieSessionUtils;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class Test01 {

    @Autowired
    CacheManager cacheManager;

    @RequestMapping(value = "/test01", method = RequestMethod.GET)
    public void test01() throws FileNotFoundException {

        Map<String, String> amountMap = new HashMap<>();
        ScoreInfo info = new ScoreInfo();
        info.setCount(10);

        String drl = (String) cacheManager.get("score_sign");
        if (drl == null) {
            drl = KieSessionUtils.getDRL("C:\\DROOLS\\score_sign.xls");
            cacheManager.put("score_sign", drl);
        }

        System.out.println(drl);
        KieSession kieSession = KieSessionUtils.createKieSessionFromDRL(drl);
        kieSession.getAgenda().getAgendaGroup("score_sign").setFocus();
        kieSession.insert(info);
        kieSession.setGlobal("amountMap", amountMap);
        kieSession.fireAllRules();

        System.out.println("评估规则ok");
        String score = amountMap.get("score");
        String coupon = amountMap.get("coupon");
        System.out.println("获得积分奖励:" + score);
        System.out.println("获得美金奖励:" + coupon);


    }
}
