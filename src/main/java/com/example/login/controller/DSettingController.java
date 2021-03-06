package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.login.service.SettingService;
import com.example.login.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@SaCheckLogin
@RestController
@RequestMapping("/setting")
public class DSettingController {

    private final SettingService settingService;

    public DSettingController(SettingService settingService) {
        this.settingService = settingService;
    }


    @PostMapping("/updateInfo")
    public Result updateInfo(Long userid, String username, Integer studyword, Integer reviewCnt) {
        System.out.println("userid:"+userid+",username:"+username+",studyword:"+studyword+",reviewCnt:"+reviewCnt);

        return settingService.updateRecord(userid, username, studyword, reviewCnt);
    }

    @PostMapping("/getStatus")
    public Result getStatus(Long userid) {
        log.info(userid + " at the page settings");
        return settingService.getSettingStatus(userid);
    }

}


