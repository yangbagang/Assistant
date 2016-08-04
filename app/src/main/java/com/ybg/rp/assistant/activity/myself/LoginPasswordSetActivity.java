package com.ybg.rp.assistant.activity.myself;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.login.LoginActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.base.BaseActivity;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.StrUtil;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 登录密码设置
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.myself
 * @修改记录:
 *
 * @date 2015/12/17 0017
 */
public class LoginPasswordSetActivity extends BaseActivity {

    @Bind(R.id.password_set_et_old)
    EditText et_old;             //旧密码

    @Bind(R.id.password_set_et_new)
    EditText et_new;             //新密码

    @Bind(R.id.password_set_et_repeat)
    EditText et_repeat;           //重复新密码

    @Bind(R.id.password_set_btn_sure)
    TextView btn_sure;            //确定

    private NetworkXUtils xUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password_set);
        ButterKnife.bind(this);
        xUtils = NetworkXUtils.getInstance(this);

        initView();

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改密码
                modifyPassword();
            }
        });
    }

    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "修改密码", null, null);
    }

    private void modifyPassword() {
        String oldPassword = et_old.getText().toString().trim();
        String newPassword = et_new.getText().toString().trim();
        String repeatPassword = et_repeat.getText().toString().trim();
        if (StrUtil.isEmpty(oldPassword)) {
            Utils.showToast(this, "原密码不能为空");
            return;
        }
        if (StrUtil.isEmpty(newPassword)) {
            Utils.showToast(this, "新密码不能为空");
            return;
        }
        if (StrUtil.isEmpty(repeatPassword)) {
            Utils.showToast(this, "新密码不能为空");
            return;
        }
        if (!newPassword.equals(repeatPassword)) {
            Utils.showToast(this, "新密码必须相同");
            return;
        }
        if(containsEmoji(newPassword)) {
            Utils.showToast(this, "不能有表情符号");
            return;
        }
        if(newPassword.equals(oldPassword)) {
            Utils.showToast(this, "新密码和旧密码不能相同");
            return;
        }
        VCParams params = new VCParams("app/operatorInfo/updatePassword");
        params.addBodyParameter("operatorId", VCApplipcation.getInstance().getOperatorId());
        params.addBodyParameter("oldPassword", oldPassword);
        params.addBodyParameter("newPassword", newPassword);
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("/updatePassword:", result.toString());
                try {
                    String success = result.getString("success");
                    //修改成功
                    if (success.equals("true")) {
                        Utils.showToast(LoginPasswordSetActivity.this, "密码修改成功");
                        VCApplipcation.getInstance().clearLoginParams();
                        startActivity(new Intent(LoginPasswordSetActivity.this, LoginActivity.class));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String msg) {
                TbLog.i("/updatePassword:", "失败信息:" + msg);
                Utils.showToast(LoginPasswordSetActivity.this, msg);
            }
        });
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

}
