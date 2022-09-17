package com.baiyun.xiangchengma.server.application;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * Create by wangbai on 2022/9/9.
 * Describe:BaseApplication
 */
public class BaseApplication extends Application {

    //获取到主线程的上下文
    private static BaseApplication baseApplication;
    private static final String TAG = "BaseApplication";

    @Override
    protected void attachBaseContext(Context base) {
        // 在这里调用Context的方法会崩溃
        super.attachBaseContext(base);
        // 在这里可以正常调用Context的方法
        // 也可以在这里初始化全局变量
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
//        ToastUtil.init(this);
    }

    /**
     * 获取BaseApplication实例
     */
    public static BaseApplication getInstance() {
        return baseApplication;
    }

    /**
     * 适配字体过大的手机（老年机）
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}