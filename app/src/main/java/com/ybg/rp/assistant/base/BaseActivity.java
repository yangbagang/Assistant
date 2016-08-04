package com.ybg.rp.assistant.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.TbActivity;


/**
 * 初始化标题栏的基Activity
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.base
 * @修改记录:
 *
 * @date 2016/3/2 0002
 */
public class BaseActivity extends TbActivity {
    private ImageView iv_left;      //返回
    private TextView tv_right;      //右侧标题
    private TextView tv_center;     //中间标题

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    /**
     * 初始化控件
     *
     * @param layout
     */
    private void init(LinearLayout layout) {
        iv_left = (ImageView) layout.findViewById(R.id.base_iv_left);
        tv_center = (TextView) layout.findViewById(R.id.base_tv_center);
        tv_right = (TextView) layout.findViewById(R.id.base_tv_right);
        /**返回按钮*/
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 设置标题
     *
     * @param layout
     * @param centerTitle
     * @param rightTitle
     * @param rightGotoClass
     */
    public void setTitle(LinearLayout layout, String centerTitle, String rightTitle, final Class rightGotoClass) {
        init(layout);
        if (rightTitle != null) {
            tv_right.setText(rightTitle);
        }
        tv_center.setText(centerTitle);
        if (rightGotoClass != null) {
            tv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), rightGotoClass));
                }
            });
        }
    }
}
