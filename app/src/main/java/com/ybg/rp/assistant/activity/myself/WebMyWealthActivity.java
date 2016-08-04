package com.ybg.rp.assistant.activity.myself;

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
import butterknife.ButterKnife;

/**
 * 我的财富
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.myself
 * @修改记录:
 *
 * @date 2015/11/27 0027
 */
public class WebMyWealthActivity extends BaseWebActivity {

    @Bind(R.id.base_wb_webview)
    WebView wb_webview;             //网页

    @Bind(R.id.base_pb_progress)
    ProgressBar pb_progress;        //加载进度条

    @Bind(R.id.base_ll_fail)
    LinearLayout ll_fail;           //加载失败显示的图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        //加载网页
        String token = VCApplipcation.getInstance().getToken();
        String operatorId = VCApplipcation.getInstance().getOperatorId();
        String url = Config.URL_COMM + "app/acctPersonAsset/myAsset?token=" + token + "&operatorId=" + operatorId;
        wb_webview.loadUrl(url);

        wb_webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                pb_progress.setVisibility(View.VISIBLE);

                TbLog.i("---WebMyWealthActivity:", url);
                String back = url.substring(url.length() - 4, url.length());
                if ("back".equals(back)) {
                    wb_webview.stopLoading();
                    finish();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                pb_progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

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
                TbLog.i("---WebMyWealthActivity:", url);
                TbLog.i("---WebMyWealthActivity:", message);

                if ("为了您的账号安全，请重新登陆！".equals(message)) {
                    new AlertDialog.Builder(WebMyWealthActivity.this)
                            .setTitle("提示!")
                            .setMessage(message)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(WebMyWealthActivity.this, LoginActivity.class));
                                    VCApplipcation.getInstance().clearLoginParams();
                                    finish();
                                }
                            })
                            .create()
                            .show();

                    result.confirm();
                }
                return true;
            }

        });
    }

}
