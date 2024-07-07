package com.imooc.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SMSUtils {
    @Autowired
    private TencentCloudProperties tencentCloudProperties;

    public void sendSMS(String phone, String code) throws Exception {
        log.info("注意！！！！手机号:{},验证码为：{}", phone, code);
    }

//    public static void main(String[] args) {
//        try {
//            new SMSUtils().sendSMS("18812345612", "7896");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}


