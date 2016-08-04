package com.ybg.rp.assistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ybg.rp.assistant.net.TbNetXRequest;

/**
 * 包            名:      com.ybg.rp.assistant.activity
 * 类            名:      TbActivity
 * 修 改 记 录:     // 修改历史记录，包括修改日期、修改者及修改内容
 * 版 权 所 有:     版权所有(C)2010-2015
 * 公             司:
 *
 * @author ybg
 * @version V1.0
 * @date 2016/1/4
 */
public class TbActivity extends AppCompatActivity {

    protected Activity mActivity;
    /** 是否停止请求*/
    protected boolean idStopRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        ExitActivity.getInstance().addActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (idStopRequest)
            TbNetXRequest.getInstance(mActivity).exitCancelable(mActivity);
    }

    /**
     * Activity结束.
     */
    @Override
    public void finish() {
        super.finish();
    }

    /**
     * 退出所有AbActivity
     */
    public void closeAllActivity() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        ExitActivity.getInstance().exit();// 退出程序
    }

    /**
     * 取代Fragment
     *
     * @param id_content
     * @param fragment
     */
    public void replaceFragment(int id_content, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id_content, fragment);
        transaction.commit();
    }
}
