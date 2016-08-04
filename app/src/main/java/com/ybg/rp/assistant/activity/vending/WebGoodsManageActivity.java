package com.ybg.rp.assistant.activity.vending;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.login.LoginActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.base.BaseWebActivity;
import com.ybg.rp.assistant.js.JavascriptObject;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.TbLog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 货品管理
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.vending
 * @修改记录:
 *
 * @date 2015/12/21 0021
 */
public class WebGoodsManageActivity extends BaseWebActivity {

    @Bind(R.id.base_wb_webview)
    WebView wb_webview;             //网页

    @Bind(R.id.base_pb_progress)
    ProgressBar pb_progress;        //加载进度条

    @Bind(R.id.base_ll_fail)
    LinearLayout ll_fail;           //加载失败显示的图片


    private LocationManager locationManager;
    private double longitude;
    private double latitude;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();    //百度地图监听

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        onLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mLocationClient.start();
    }

    /**6.0权限处理*/
    final public static int REQUEST_CODE_ASK_LOCATION = 156;
    public void onLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_LOCATION);
            } else {
                initLocation();
            }
        } else {
            //方法
            initLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if(requestCode==REQUEST_CODE_ASK_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                initLocation();
            } else {
                // Permission Denied
                Toast.makeText(this, "无定位权限!!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        //int span=1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        //option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //百度地图api监听
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            initData(longitude, latitude);   //加载网页数据

            //以下信息暂时没用到
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            TbLog.i("BaiduLocationApiDem", sb.toString());
        }
    }

    private void initData(final double longitude, final double latitude) {
        TbLog.i("---WebGoodsManageActivity传给服务的经纬度:", "经度:" + longitude + ",纬度:" + latitude);
        //加载的网页url
        String token = VCApplipcation.getInstance().getToken();
        VCApplipcation.getInstance().setLongitude(longitude);
        VCApplipcation.getInstance().setLatitude(latitude);
        wb_webview.loadUrl("file:///android_asset/goodsManage.html");
        wb_webview.setFocusable(true);
        wb_webview.setFocusableInTouchMode(true);
        wb_webview.addJavascriptInterface(new JavascriptObject(), "appObject");

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
                //返回结束本页面
                TbLog.i("---WebGoodsManageActivity:", url);
                String back = url.substring(url.length() - 4, url.length());
                if (url.contains("back")) {
                    finish();
                }
                if (url.contains("finish")) {
                    finish();
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb_progress.setVisibility(View.INVISIBLE);
                //mLocationClient.stop();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                TbLog.i("--错误码-WebGoodsManageActivity:", errorCode + "");
                TbLog.i("--错误url-WebGoodsManageActivity:", failingUrl);
                ll_fail.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.INVISIBLE);
                wb_webview.setVisibility(View.INVISIBLE);
                ll_fail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ll_fail.setVisibility(View.INVISIBLE);
                        initData(longitude, latitude);
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
                    new AlertDialog.Builder(WebGoodsManageActivity.this)
                            .setTitle("提示!")
                            .setMessage(message)
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            startActivity(new Intent(WebGoodsManageActivity.this, LoginActivity.class));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

}
