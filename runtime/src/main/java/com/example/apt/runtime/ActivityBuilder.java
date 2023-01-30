package com.example.apt.runtime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * @author zouji
 * @Description:
 * @date 2023/1/30
 */
public class ActivityBuilder {

    public static final ActivityBuilder INSTANCE = new ActivityBuilder();
    public void startActivity(Context context, Intent intent) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }  //context可能是Activity的也可能是Application的
        context.startActivity(intent);
    }
}
