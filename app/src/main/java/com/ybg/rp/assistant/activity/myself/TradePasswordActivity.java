package com.ybg.rp.assistant.activity.myself;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.base.BaseActivity;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.myself
 * @修改记录:
 *
 * @date 2015/12/21 0021
 */
public class TradePasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_password);
        initView();
    }
    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "修改交易密码", null, null);
    }

}
