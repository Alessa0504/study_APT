package com.example.apt.runtime;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author zouji
 * @Description:
 * @date 2023/1/30
 */
public class ActivityBuilder {

    public static final String BUILDER_NAME_POSIX = "Builder";

    public static final ActivityBuilder INSTANCE = new ActivityBuilder();

    private Application application;

    // Activity生命周期监听回调
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        // Activity onCreate -> dispatchActivityCreated -> onActivityCreated会被调用
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            performInject(activity, savedInstanceState);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
             performSaveState(activity, outState);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    };

    private void performInject(Activity activity, Bundle savedInstanceState) {
        try {
            // 反射调用方法
            Class.forName(activity.getClass().getName() + BUILDER_NAME_POSIX)
                    .getDeclaredMethod("inject", Activity.class, Bundle.class)
                    .invoke(null, activity, savedInstanceState);   //null代表静态方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSaveState(Activity activity, Bundle outState) {
        try {
            // 反射调用方法
            Class.forName(activity.getClass().getName() + BUILDER_NAME_POSIX)
                    .getDeclaredMethod("saveState", Activity.class, Bundle.class)
                    .invoke(null, activity, outState);   //null代表静态方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化，注册LifecycleCallbacks
     * @param context
     */
    public void init(Context context) {
        if(this.application != null) return;
        this.application = (Application) context.getApplicationContext();
        this.application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public void startActivity(Context context, Intent intent) {
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }  //context可能是Activity的也可能是Application的
        context.startActivity(intent);
    }
}
