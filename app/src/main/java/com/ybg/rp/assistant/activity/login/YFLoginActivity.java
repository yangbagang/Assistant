package com.ybg.rp.assistant.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.TbActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.ToastUtil;
import com.ybg.rp.assistant.view.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.login
 * @修改记录:
 *
 * @date 2016/4/15 0015
 */
public class YFLoginActivity extends TbActivity implements View.OnClickListener {

    @Bind(R.id.yfLogin_btn_login)
    Button mYfLoginBtnLogin;        //登录

    @Bind(R.id.yfLogin_btn_cancel_login)
    Button btn_cancel_login;        //返回扫码页面

    @Bind(R.id.yfLogin_ll_close)
    LinearLayout mYfLoginClose;

    private NetworkXUtils xUtils;
    private String vmCode;

    private CustomProgressDialog dialog;    //登录弹窗提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yf_login);
        ButterKnife.bind(this);
        xUtils = NetworkXUtils.getInstance(this);

        dialog = CustomProgressDialog.createDialog(this);
        dialog.setMessage("授权中");
        dialog.setCanceledOnTouchOutside(false);

        Bundle extras = getIntent().getExtras();
        vmCode = extras.getString("result");
        TbLog.i("--------机器编号:" + vmCode);

        //eimi = Utils.generateMacWithColon(eimi);

        mYfLoginBtnLogin.setOnClickListener(this);
        btn_cancel_login.setOnClickListener(this);
        mYfLoginClose.setOnClickListener(this);

        if (vmCode != null) {
            scanSuccess();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**登录*/
            case R.id.yfLogin_btn_login:
                if (vmCode != null) {
                    login(vmCode);
                }
                break;

            /**返回扫码页面*/
            case R.id.yfLogin_btn_cancel_login:
                finish();
                break;

            case R.id.yfLogin_ll_close:
                ExitSomeActivity.getInstance().exit();
                finish();
                break;
        }
    }

    private void login(String vmCode) {
        RequestParams params = new VCParams("vendMachineInfo/authQRCode");
        params.addBodyParameter("token", VCApplipcation.getInstance().getToken());
        params.addBodyParameter("vmCode", vmCode);
        TbLog.i("---token="+ VCApplipcation.getInstance().getToken() + "---&vmCode=" + vmCode);
        dialog.show();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                dialog.dismiss();
                TbLog.i("---YFLoginActivity:", s);
                try {
                    JSONObject json = new JSONObject(s);
                    if ("true".equals(json.getString("success"))) {
                        ToastUtil.showToast(YFLoginActivity.this, "授权成功");
                        ExitSomeActivity.getInstance().exit();
                        finish();
                    } else {
                        ToastUtil.showToast(YFLoginActivity.this, json.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
                ToastUtil.showToast(mActivity, "服务异常");
                TbLog.i("---YFLoginActivity:", throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 扫码成功调用
     */
    private void scanSuccess() {
        RequestParams params = new VCParams("vendMachineInfo/checkQRCode");
        params.addBodyParameter("token", VCApplipcation.getInstance().getToken());
        params.addBodyParameter("vmCode", vmCode);
        dialog.show();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                dialog.dismiss();
                TbLog.i("-----YFLogin/qcodeYs : " + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if ("true".equals(json.getString("success"))) {
                        ToastUtil.showToast(YFLoginActivity.this, "扫描成功");
                    } else {
                        ToastUtil.showToast(YFLoginActivity.this, json.getString("success"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
                ToastUtil.showToast(mActivity, "服务异常");
                TbLog.i("-----YFLogin/qcodeYs : " + throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
