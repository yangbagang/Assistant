package com.ybg.rp.assistant.activity.login;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.login
 * @修改记录:
 *
 * @date 2016/4/22 0022
 */
public class ExitSomeActivity extends Application {

    private LinkedList<Activity> mList = new LinkedList<Activity>();
    private static ExitSomeActivity instance;

    /* 单列 */
    public synchronized static ExitSomeActivity getInstance() {
        if (null == instance) {
            instance = new ExitSomeActivity();
        }
        return instance;
    }

    // 添加要退出的Activity
    public void addActivity(Activity activity) {
        for (int i = 0; i < mList.size(); i++) {
            Activity mAc = mList.get(i);
            if (mAc.equals(activity)) {
                return;
            }
        }
        mList.add(activity);
    }

    //清除数据
    public void clearAll() {
        mList.clear();
    }

    //退出
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
}
