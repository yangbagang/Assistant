package com.ybg.rp.assistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.login.LoginActivity;
import com.ybg.rp.assistant.utils.AppPreferences;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.TbLog;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity
 * @修改记录:
 *
 * @date 2016/3/21 0021
 */
public class SplashActivity extends TbActivity {

    private AppPreferences preferences = AppPreferences.getInstance();

    @Bind(R.id.iv_splash)
    ImageView iv_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        // 初始化首选项配置
        if (!preferences.hasInit()) {
            preferences.init(getSharedPreferences(
                    Config.PREFERENCE_FILE_NAME, Activity.MODE_PRIVATE));
        }

        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initScaleAnimation();
        String tee = android.os.Build.MODEL;
        TbLog.i("手机型号", tee);
    }

    /**
     * 缩放动画
     */
    private void initScaleAnimation() {
        ScaleAnimation anim = new ScaleAnimation(1f, 1.4f, 1f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true);
        anim.setDuration(2000);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            /**动画结束*/
            @Override
            public void onAnimationEnd(Animation animation) {
                if (preferences.getString("token", null) != null && preferences.getString("operatorId", "") != null) {
                    //进入主页
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    //进入登录页面
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        iv_splash.startAnimation(anim);
    }

}
