package com.ybg.rp.assistant.net;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ybg.rp.assistant.activity.login.LoginActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.StrUtil;
import com.ybg.rp.assistant.utils.TbLog;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.net
 * @修改记录:
 *
 * @date 2016/3/21 0021
 */
public class NetworkXUtils {

    /**通用请求父类*/
    private TbNetXRequest tbNetXRequest;
    private static NetworkXUtils utils;
    private Context mContext;

    private NetworkXUtils(Context context) {
        this.mContext=context;
        tbNetXRequest = TbNetXRequest.getInstance(mContext);
    }

    public synchronized static NetworkXUtils getInstance(Context mContext) {
        if (null == utils) {
            utils = new NetworkXUtils(mContext);
        }
        return utils;
    }

    /**
     * post 网络请求
     *
     */
    public Callback.Cancelable post(RequestParams params, Callback.CommonCallback<JSONObject> callback) {
        if (null == params || null == callback) return null;
        params.setHeader("x-requested-with", "XMLHttpRequest");
        //params.addBodyParameter("", "");
        //params.addBodyParameter("", "");
        return tbNetXRequest.post(params, callback);
    }

    /**
     * post 网络请求
     * @param params
     * @param callback
     */
    public void post(RequestParams params, final NetCallback callback){

        params.addBodyParameter("token", VCApplipcation.getInstance().getToken());   //带有token的请求
        params.setHeader("x-requested-with", "XMLHttpRequest");
        tbNetXRequest.post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                //TbLog.i("- NetworkUtils  result：" + result.toString());
                try {
                    if (!StrUtil.isEmpty(result.toString())) {
                        if (Config.TRUE.equals(result.getString(Config.SUCCESS))) {
                            callback.onSuccess(result);
                        } else {
                            String msg = result.getString("msg");
                            if (!StrUtil.isEmpty(msg)) {
                                if (msg.contains("请重新")) {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //重新登录
                                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                        }
                                    }, 700);

                                }
                                callback.onFailure(new HttpException(500, msg), msg);
                            }

                            // 将失败错误信息传递回去
                            //callback.onFailure(new HttpException(500,result.getString(Config.ERROR)), result.getString(Config.ERROR));
                        }
                    } else{
                        callback.onFailure(new HttpException(500,"请求数据错误"), "请求数据错误");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                TbLog.i("--onError--" + ex.getMessage());
                callback.onFailure(new HttpException(500, ex.getMessage()), ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                TbLog.i("--onCancelled--");
            }

            @Override
            public void onFinished() {
                TbLog.i("--onFinished--");
            }
        });
    }

    public TbNetXRequest getTbNetXRequest() {
        return tbNetXRequest;
    }

    public interface NetCallback{
        void onSuccess(JSONObject result) throws InterruptedException;
        void onFailure(HttpException ex, String msg);
    }
}
