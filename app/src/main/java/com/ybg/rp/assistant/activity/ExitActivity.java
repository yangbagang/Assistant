package com.ybg.rp.assistant.activity;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * 退出程序
 */
// 需要先使用addActivity()添加
// 使用exit()退出
public class ExitActivity extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private static ExitActivity instance;

    private Activity payAllActivity;


    /* 单列 */
    public synchronized static ExitActivity getInstance() {
        if (null == instance) {
            instance = new ExitActivity();
        }
        return instance;
    }

    // 添加要退出的Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    // 退出
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* 不需要终止程序 */
        // finally {
        // System.exit(0);
        // }
    }

    /*// 退出指定页
    public void exitPayAllActivity() {
        try {
            if (null != payAllActivity) {
                payAllActivity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void setPayAllActivity(Activity payAllActivity) {
        this.payAllActivity = payAllActivity;
    }

    public Activity getPayAllActivity() {
        return payAllActivity;
    }

    /* 清理内存 */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
