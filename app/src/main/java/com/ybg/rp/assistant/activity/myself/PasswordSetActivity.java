package com.ybg.rp.assistant.activity.myself;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 密码设置
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.myself
 * @修改记录:
 *
 * @date 2015/12/3 0003
 */
public class PasswordSetActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.login_password)
    LinearLayout mLoginPassword;//登录密码

    @Bind(R.id.trade_password)
    LinearLayout mTradePassword;//交易密码

    @Bind(R.id.unlock_password)
    LinearLayout mUnlockPassword;//解锁密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_set);
        ButterKnife.bind(this);

        initView();

        mLoginPassword.setOnClickListener(this);
        mTradePassword.setOnClickListener(this);
        mUnlockPassword.setOnClickListener(this);
    }
    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "密码设置", null, null);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //登录密码
            case R.id.login_password:
                startActivity(new Intent(PasswordSetActivity.this,LoginPasswordSetActivity.class));
                break;

            //交易密码
            case R.id.trade_password:
                startActivity(new Intent(PasswordSetActivity.this,TradePasswordActivity.class));
                break;
        }
    }
}
