package com.ybg.rp.assistant.base;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.TbActivity;
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

    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wb_webview.canGoBack()) {
            wb_webview.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        finish();//结束退出程序
        return false;
    }
}
