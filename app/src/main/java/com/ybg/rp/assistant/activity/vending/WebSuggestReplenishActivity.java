package com.ybg.rp.assistant.activity.vending;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
 * 建议补货
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.vending
 * @修改记录:
 *
 * @date 2015/12/24 0024
 */
public class WebSuggestReplenishActivity extends BaseWebActivity {

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 检测屏幕的方向：纵向或横向
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //当前为竖屏
        }
        //检测实体键盘的状态：推出或者合上
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            //实体键盘处于推出状态
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            //实体键盘处于合上状态
        }
    }

    private void initData() {

        //加载需要显示的网页
        String token = VCApplipcation.getInstance().getToken();
        String url = Config.URL_COMM + "goodsManagementApp/queryReplenishmentInfo?token=" + token;
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
                TbLog.i("--WebSuggestReplenishActivity--开始加载:", url);
                pb_progress.setVisibility(View.VISIBLE);

                String back = url.substring(url.length() - 4, url.length());
                TbLog.i("--WebVendingSetActivity--url后4位--", back);
                if ("back".equals(back)) {
                    finish();
                }

                //设置横竖屏     //选择商品或者售卖机显示为横屏
                if (url.contains("managementGoods") || url.contains("chooseGoods")) {
                    if (WebSuggestReplenishActivity.this.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        WebSuggestReplenishActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //设为横屏
                        TbLog.i("设为横屏:", "----");
                    }
                } else {
                    WebSuggestReplenishActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设为竖屏
                    TbLog.i("设为竖屏:", "----");
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
                TbLog.i("-错误码--WebMyVendingSet:", errorCode + "");
                TbLog.i("-错误url--WebMyVendingSet:", failingUrl);
                ll_fail.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.INVISIBLE);
                wb_webview.setVisibility(View.INVISIBLE);
                wb_webview.loadUrl("file:///android_asset/fail.html");
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
                TbLog.i("---WebMyVendingSet:", url);
                TbLog.i("---WebMyVendingSet:", message);

                //token改变了重新登录
                if (message.contains("账号安全")) {
                    new AlertDialog.Builder(WebSuggestReplenishActivity.this)
                            .setTitle("提示!")
                            .setMessage(message)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(WebSuggestReplenishActivity.this, LoginActivity.class));
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
