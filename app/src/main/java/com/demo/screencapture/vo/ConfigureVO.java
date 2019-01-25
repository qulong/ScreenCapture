package com.demo.screencapture.vo;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/23
 *  控制开关
 */

public class ConfigureVO {

    public boolean isToReadSms;//是否去读短信
    public boolean readSmsOk;//读取完成
    public boolean isToReadContats;//是否去读联系人
    public boolean readContatsOk;//读取完成

    public void setReadContatsOk(boolean readContatsOk) {
        this.readContatsOk = readContatsOk;
    }

    public void setReadSmsOk(boolean readSmsOk) {
        this.readSmsOk = readSmsOk;
    }

    public void setToReadSms(boolean toReadSms) {
        isToReadSms = toReadSms;
    }

    public void setToReadContats(boolean toReadContats) {
        isToReadContats = toReadContats;
    }

    /**
     * 测试数据
     * */
    public void setIsToRead_for_Test(boolean ok) {
        this.isToReadSms = ok;
        this.isToReadContats = ok;
    }
}
