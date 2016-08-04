package com.ybg.rp.assistant.activity.myself;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.myself
 * @修改记录:
 *
 * @date 2015/12/28 0028
 */
public class MoreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);

        initView();
    }

    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "更多", null, null);
    }


}
