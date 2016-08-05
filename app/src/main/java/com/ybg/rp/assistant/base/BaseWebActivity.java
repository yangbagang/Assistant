package com.ybg.rp.assistant.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.TbActivity;
import com.ybg.rp.assistant.activity.login.LoginActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.js.JavascriptObject;
import com.ybg.rp.assistant.utils.TbLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.base
 * @修改记录:
 *
 * @date 2016/3/21 0021
 */
public class BaseWebActivity extends TbActivity {

    WebView wb_webview;             //网页
    LinearLayout ll_fail;           //加载失败显示的图片
    ProgressBar pb_progress;        //加载进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_web);

        wb_webview = (WebView) findViewById(R.id.base_wb_webview);
        ll_fail = (LinearLayout) findViewById(R.id.base_ll_fail);
        pb_progress = (ProgressBar) findViewById(R.id.base_pb_progress);
        initWebSet();
    }

    /**
     * 网页内容设置
     */
    private void initWebSet() {
        wb_webview = (WebView) findViewById(R.id.base_wb_webview);
        WebSettings webSettings = wb_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);//设置可以访问文件
        webSettings.setBuiltInZoomControls(false);//禁止缩放
        webSettings.setSavePassword(true);//是否保存网页密码
        webSettings.setSaveFormData(true);//是否保存表单form
        webSettings.setPluginState(WebSettings.PluginState.ON);//允许插件
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//启用地理定位
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        //        webSettings.setUseWideViewPort(true);// 调整到适合webview大小
        //        webSettings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        //        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
//         webSettings.setBlockNetworkImage(true);

        //设置缓存
        webSettings.setAppCacheEnabled(true);       //开启 Application Caches 功能
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);     //设置缓存模式
        //webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings.setDomStorageEnabled(true);     //开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);       //开启
        String cacheDirPath = getFilesDir().getAbsolutePath();   ///data/data/com.ybg.retailp.operatorapp/files/webcache
        webSettings.setDatabasePath(cacheDirPath);  //设置数据库缓存路径
        webSettings.setAppCachePath(cacheDirPath);  //设置  Application Caches 缓存目录
        TbLog.i("---BaseWebActivity:", "---加载了设置");
    }

    /**
     * 清除webView缓存
     */
    public void clearWebViewCache() {

        //清理Webview缓存数据库
        try {
            // boolean b = deleteDatabase("webview.db");
            //            deleteDatabase("webviewCache.db");
            TbLog.i("webview缓存数据库", "11");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件目录
        File appCacheDir = new File(getFilesDir().getAbsolutePath());
        TbLog.i("----", "appCacheDir path=" + appCacheDir.getAbsolutePath());
        //  /data/data/com.ybg.retailp.operatorapp/files

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");
        TbLog.i("----", "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());
        //  /data/data/com.ybg.retailp.operatorapp/cache/webviewCache


        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            TbLog.i("webviewCacheDir缓存文件大小:", webviewCacheDir.length() + "");
            //deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            TbLog.i("appCacheDir缓存文件大小:", appCacheDir.length() + "");
            //deleteFile(appCacheDir);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //clearWebViewCache();
    }

    private int mFilelen;

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {

        TbLog.i("----", "delete file path=" + file.getAbsolutePath());

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    try {
                        //文件大小
                        FileInputStream fis = new FileInputStream(files[i]);
                        mFilelen = fis.available();
                        //AbLogUtil.i("----------缓存文件的大小", mFilelen + "----");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            TbLog.i("----", "delete file no exists " + file.getAbsolutePath());
        }
    }

    protected void loadUrl(final double longitude, final double latitude, final String url) {
        TbLog.i("---BaseWebActivity传给服务的经纬度:", "经度:" + longitude + ",纬度:" + latitude);
        //加载的网页url
        VCApplipcation.getInstance().setLongitude(longitude);
        VCApplipcation.getInstance().setLatitude(latitude);
        wb_webview.loadUrl(url);
        wb_webview.setFocusable(true);
        wb_webview.setFocusableInTouchMode(true);
        wb_webview.addJavascriptInterface(new JavascriptObject(), "appObject");

        wb_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("url1=" + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("url2=" + url);
                pb_progress.setVisibility(View.VISIBLE);
                //返回结束本页面
                TbLog.i("---BaseWebActivity:", url);
                if ("back.html".equals(url)) {
                    onBackPressed();
                }
                if ("close.html".equals(url)) {
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
                TbLog.i("--错误码-BaseWebActivity:", errorCode + "");
                TbLog.i("--错误url-BaseWebActivity:", failingUrl);
                ll_fail.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.INVISIBLE);
                wb_webview.setVisibility(View.INVISIBLE);
                ll_fail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_fail.setVisibility(View.INVISIBLE);
                        loadUrl(longitude, latitude, url);
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

                //token改变重新登录
                if (message.contains("账号安全")) {
                    new AlertDialog.Builder(BaseWebActivity.this)
                            .setTitle("提示!")
                            .setMessage(message)
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            startActivity(new Intent(BaseWebActivity.this, LoginActivity.class));
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
