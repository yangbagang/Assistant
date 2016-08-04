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

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 售卖机设置
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.vending
 * @修改记录:
 *
 * @date 2015/12/11 0011
 */
public class WebVendingSetActivity extends BaseWebActivity {

    private static final int ERROR = 500;

    @Bind(R.id.base_wb_webview)
    WebView wb_webview;             //网页

    @Bind(R.id.base_pb_progress)
    ProgressBar pb_progress;        //加载进度条

    @Bind(R.id.base_ll_fail)
    LinearLayout ll_fail;           //加载失败显示的图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {


        //加载需要显示的网页
        String token = VCApplipcation.getInstance().getToken();
        String url = Config.URL_COMM + "app/vendMachineApp/vendMachineManagement?token=" + token;
        wb_webview.loadUrl(url);
        wb_webview.setFocusable(true);
        wb_webview.setFocusableInTouchMode(true);

        //响应状态码,弊端相当于2次访问
        //responseState(url);

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

                TbLog.i("--WebVendingSetActivity:", url);
                pb_progress.setVisibility(View.VISIBLE);

                String back = url.substring(url.length() - 4, url.length());
                TbLog.i("--WebVendingSetActivity--url后4位--", back);
                if ("back".equals(back)) {
                    finish();
                }

                //超时处理
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {

                    }
                };

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
                ll_fail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_fail.setVisibility(View.INVISIBLE);
                        wb_webview.reload();
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
                    new AlertDialog.Builder(WebVendingSetActivity.this)
                            .setTitle("提示!")
                            .setMessage(message)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(WebVendingSetActivity.this, LoginActivity.class));
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

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            int what = msg.what;
//            if(what == ERROR) {
//                //500错误
//            }
//        }
//    };

//    private void responseState(final String url) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpGet get = new HttpGet(url);
//                HttpClient client = new DefaultHttpClient();
//                HttpResponse httpResponse = null;
//                try {
//                    httpResponse = client.execute(get);
//                    int code = httpResponse.getStatusLine().getStatusCode();
//                    AbLogUtil.i("---WebGoodsManageActivity:", "响应状态码:" + code);
//                    if (code == 500 || code == 404) {
//                        Message message = Message.obtain();
//                        message.what = ERROR;
//                        handler.sendMessage(message);
//                    } else if (code == 200) {
//                        String data = Utils.decodeInputStream(httpResponse.getEntity().getContent());
//                        Message message = Message.obtain();
//                        message.what = 120;
//                        handler.sendMessage(message);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
//    }

}
