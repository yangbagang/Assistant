package com.ybg.rp.assistant.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.MainActivity;
import com.ybg.rp.assistant.activity.TbActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.bean.AccountUser;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.AppPreferences;
import com.ybg.rp.assistant.utils.GsonUtils;
import com.ybg.rp.assistant.utils.StrUtil;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;
import com.ybg.rp.assistant.view.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.login
 * @修改记录:
 *
 * @date 2016/3/21 0021
 */
public class LoginActivity extends TbActivity implements View.OnClickListener {

    private AppPreferences preferences = AppPreferences.getInstance();

    @Bind(R.id.login_et_phone_number)
    EditText et_phone_number;              //手机号

    @Bind(R.id.login_et_password)
    EditText et_password;           //密码

    @Bind(R.id.login_cb_remember)
    CheckBox cb_remember;          //记住密码

    @Bind(R.id.login_tv_forget)
    TextView tv_forget;         //忘记密码

    @Bind(R.id.login_tv_login)
    TextView tv_login;          //登录

    private NetworkXUtils xUtils;
    private CustomProgressDialog dialog;    //登录弹窗提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        dialog = CustomProgressDialog.createDialog(this);
        dialog.setMessage("登录中...");
        dialog.setCanceledOnTouchOutside(false);

        xUtils = NetworkXUtils.getInstance(this);

        tv_login.setOnClickListener(this);

        //回显记住的账号密码
        boolean isRememberPassword = preferences.getBoolean("isRememberPassword", false);
        if (isRememberPassword) {
            et_phone_number.setText(preferences.getString("phoneNumber", ""));
            et_password.setText(preferences.getString("password", ""));
            cb_remember.setChecked(true);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /**登录*/
            case R.id.login_tv_login:
                login();
                break;

        }
    }

    private void login() {

        String userName = et_phone_number.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (StrUtil.isEmpty(userName)) {
            Utils.showToast(LoginActivity.this, "用户名不能为空");
            return;
        }
        if (StrUtil.isEmpty(password)) {
            Utils.showToast(LoginActivity.this, "密码不能为空");
            return;
        }

        boolean isRemember = cb_remember.isChecked();
        if (isRemember) {
            preferences.setString("phoneNumber", userName);
            preferences.setString("password", password);
            preferences.setBoolean("isRememberPassword", true);
        } else {
            preferences.setString("phoneNumber", "");
            preferences.setString("password", "");
            preferences.setBoolean("isRememberPassword", false);
        }

        VCParams params = new VCParams("partnerUserInfo/login");

        String loginDev = android.os.Build.MODEL;
        params.addBodyParameter("userName", userName);
        params.addBodyParameter("password", password);
        params.addBodyParameter("loginDevice", loginDev);
        dialog.show();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TbLog.i("/LoginActivity:", s);
                dialog.dismiss();

                try {
                    JSONObject json = new JSONObject(s);
                    if(json.getString("success").equals("true")) {
                        String token = json.getString("token");
                        VCApplipcation.getInstance().setToken(token, true);      //保存token

                        String userInfo = json.getString("userInfo");
                        AccountUser user = GsonUtils.createGson().fromJson(userInfo, AccountUser.class);

                        String operatorId = user.getId() + "";
                        VCApplipcation.getInstance().setOperatorId(operatorId, true); //保存operatorId
                        saveUserInfo(user);                                         //保存用户信息

                        if (token != null) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));  //进入主页
                            Utils.showToast(LoginActivity.this, "登录成功!");
                            finish();
                        } else {
                            Utils.showToast(LoginActivity.this, "用户名或密码错误");
                        }
                    } else {
                        Utils.showToast(LoginActivity.this, json.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                TbLog.i("/LoginActivity:", throwable.getMessage());
                dialog.dismiss();

                Utils.showToast(LoginActivity.this, "登录异常,请稍后重试");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    //保存用户参数
    public void saveUserInfo(AccountUser user) {
        preferences.setString("operatorId", user.getId() + "");
        preferences.setString("username", user.getUsername());
        preferences.setString("realName", user.getRealName());
        preferences.setString("email", user.getEmail());
        preferences.setString("avatar", user.getAvatarUrl());
        TbLog.i("登录界面,", "用户数据保存成功");
    }

    /**
     * 双击退出程序
     */
    private boolean is2CallBack = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (!is2CallBack) {
                Utils.showToast(this, "再按一次退出程序");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        is2CallBack = false;
                    }
                }, 1000);
                is2CallBack = true;
            } else {
                closeAllActivity();
            }
        }
        return true;
    }
}
