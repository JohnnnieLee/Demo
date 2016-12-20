package com.ccstudio.demo;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Johnny on 2016/12/20.
 */

public class MessageData {
    @Retention(SOURCE)
    @IntDef({USER_TYPE_SELLER, USER_TYPE_BUYER})
    public @interface UserType {}
    public static final int USER_TYPE_SELLER = 0;
    public static final int USER_TYPE_BUYER = 1;

    public final String message;
    public final String date;
    public final @MessageData.UserType int userType;

    public MessageData(String message, String date, @UserType int userType) {
        this.message = message;
        this.date = date;
        this.userType = userType;
    }
}
