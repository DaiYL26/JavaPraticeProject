package com.example.login.controller;

import com.example.login.service.SettingService;
import com.example.login.vo.Result;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
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
        return settingService.getSettingStatus(userid);
    }

}


