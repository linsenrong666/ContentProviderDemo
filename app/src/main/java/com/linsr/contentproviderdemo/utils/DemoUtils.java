package com.linsr.contentproviderdemo.utils;

import android.text.TextUtils;

import java.util.UUID;

/**
 * Description
 @author Linsr
 */

public class DemoUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean isEmpty(CharSequence charSequence){
        return TextUtils.isEmpty(charSequence);
    }
}
