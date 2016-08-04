package com.ybg.rp.assistant.activity.vending;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.login.LoginActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.base.BaseWebActivity;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.TbLog;

import butterknife.Bind;

/**
 * PPBar管理
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.vending
 * @修改记录:
 *
 * @date 2016/1/12 0012
 */
public class WebPPbarManageActivity extends BaseWebActivity {

    @Bind(R.id.base_wb_webview)
    WebView wb_webview;             //网页

    @Bind(R.id.base_pb_progress)
    ProgressBar pb_progress;        //好友提示加载进度圈

    @Bind(R.id.base_ll_fail)
    LinearLayout ll_fail;           //加载失败显示的图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    /**
     * 加载网页
     */
    private void initData() {
        //加载需要显示的网页
        String token = VCApplipcation.getInstance().getToken();
        String url = Config.URL_COMM + "app/vendMachineApp/ppBarManagement?token=" + token;
        wb_webview.loadUrl(url);
        wb_webview.setFocusable(true);
        wb_webview.setFocusableInTouchMode(true);

        //设置Web视图
        wb_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                TbLog.i("--WebPPbarManageActivity:", url);
                pb_progress.setVisibility(View.VISIBLE);

                String back = url.substring(url.length() - 4, url.length());
                TbLog.i("s----------", back);
                if ("back".equals(back)) {
                    finish();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                pb_progress.setVisibility(View.INVISIBLE);
            }

            //捕捉错误页面
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                TbLog.i("WebPPbarManageActivity:", "错误码:" + errorCode);
                TbLog.i("WebPPbarManageActivity:", "错误码:" + failingUrl);
                TbLog.i("WebPPbarManageActivity:", "错误码:" + description);
                ll_fail.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.INVISIBLE);
                wb_webview.setVisibility(View.INVISIBLE);
                ll_fail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_fail.setVisibility(View.INVISIBLE);
                        initData();
                        wb_webview.setVisibility(View.VISIBLE);
                    }
                });
            }

        });

        wb_webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
                TbLog.i("---WebPPbarManageActivity:", url);
                TbLog.i("---WebPPbarManageActivity:", message);

                //token改变重新登录
                if (message.contains("账号安全")) {
                    new AlertDialog.Builder(WebPPbarManageActivity.this)
                            .setTitle("提示!")
                            .setMessage(message)
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            startActivity(new Intent(WebPPbarManageActivity.this, LoginActivity.class));
                                            VCApplipcation.getInstance().clearLoginParams();
                                            finish();
                                        }
                                    })
                            .create()
                            .show();

                    result.confirm();
                    return true;
                }
                return super.onJsAlert(view, url, message, result);
            }

        });
    }
}
