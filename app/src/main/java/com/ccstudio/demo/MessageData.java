package com.ccstudio.demo;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Johnny on 2016/12/20.
 */

public class MessageData implements Serializable {
    @Retention(SOURCE)
    @IntDef({USER_TYPE_SELLER, USER_TYPE_BUYER})
    public @interface UserType {}
    public static final int USER_TYPE_UNKNOWN = 0;
    public static final int USER_TYPE_SELLER = 1;
    public static final int USER_TYPE_BUYER = 2;

    public final String name;
    public final String message;
    public final String date;
    public final String avatarUrl;
    public final @MessageData.UserType int userType;

    public MessageData(String name, String message, String date, String avatarUrl, int userType) {
        this.name = name;
        this.message = message;
        this.date = date;
        this.avatarUrl = avatarUrl;
        this.userType = userType;
    }
}
