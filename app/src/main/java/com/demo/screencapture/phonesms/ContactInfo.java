package com.demo.screencapture.phonesms;

import java.io.Serializable;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/15
 */

public class ContactInfo implements Serializable {
    public String contactName;
    public String phoneNumber;

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "[contactName:"+contactName+",phoneNumber:"+phoneNumber+"]";
    }
}
