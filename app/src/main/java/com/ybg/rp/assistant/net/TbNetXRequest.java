package com.ybg.rp.assistant.net;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.ybg.rp.assistant.utils.TbLog;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * XUTILS 3 公共请求父类
 * 包            名:      com.ybg.rp.assistant.net
 * 类            名:      TbNetXRequest
 * 修 改 记 录:     // 修改历史记录，包括修改日期、修改者及修改内容
 * 版 权 所 有:     版权所有(C)2010-2015
 * 公             司:
 *
 * @author ybg
 * @version V1.0
 * @date 2016/1/8
 */
public class TbNetXRequest extends Application {

    private static TbNetXRequest tbNetXRequest;
    private Context mContext;
    private Map<String, List<Callback.Cancelable>> cancelableList = new Hashtable<String, List<Callback.Cancelable>>();


    public synchronized static TbNetXRequest getInstance(Context mContext) {
        if (null == tbNetXRequest) {
            tbNetXRequest = new TbNetXRequest(mContext);
        }
        return tbNetXRequest;
    }

    private TbNetXRequest(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * POST请求
     * 默认超时时间-15000毫秒
     * 默认编码格式-UTF-8
     *
     * @param params   请求参数
     * @param callback 回调
     * @return
     */
    public Callback.Cancelable post(final RequestParams params, final Callback.CommonCallback<JSONObject> callback) {
        if (null == params || null == callback) return null;
        Callback.Cancelable cancelable = x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                String msg;
                if (e instanceof ConnectException) {
                    msg = HttpPrompt.CONNECTEXCEPTION;
                } else if (e instanceof ConnectTimeoutException) {
                    msg = HttpPrompt.CONNECTEXCEPTION;
                } else if (e instanceof UnknownHostException) {
                    msg = HttpPrompt.UNKNOWNHOSTEXCEPTION;
                } else if (e instanceof SocketException) {
                    msg = HttpPrompt.SOCKETEXCEPTION;
                } else if (e instanceof SocketTimeoutException) {
                    msg = HttpPrompt.SOCKETTIMEOUTEXCEPTION;
                } else if (e instanceof NullPointerException) {
                    msg = HttpPrompt.NULLPOINTEREXCEPTION;
                }else {
                    if (e == null || TextUtils.isEmpty(e.getMessage())) {
                        msg = HttpPrompt.NULLMESSAGEEXCEPTION;
                    } else {
                        msg = e.getMessage();
                    }
                }
                if (msg.equals("The target server failed to respond")) {
                    msg = HttpPrompt.SOCKETEXCEPTION;
                }
                e = new Throwable(msg);
                callback.onError(e, isOnCallback);
                TbLog.e(e.getMessage() + "---" + isOnCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                TbLog.e("取消请求-" + params.getUri());
                callback.onCancelled(cex);
            }

            @Override
            public void onFinished() {
                callback.onFinished();
            }
        });
        addCancelable(mContext, cancelable);
        return cancelable;
    }


    /**
     * 请求控制
     *
     * @param tag        标识(String,或者ENTITY)
     * @param cancelable 请求
     */
    public void addCancelable(Object tag, Callback.Cancelable cancelable) {
        if (null == tag || null == cancelable) return;
        String objName = tag.getClass().getSimpleName();
        if (objName.equals("String")) {
            objName = tag.toString();
        }
        List<Callback.Cancelable> mList = cancelableList.get(objName);
        if (null == mList || mList.size() <= 0) {
            mList = new ArrayList<Callback.Cancelable>();
        }
        mList.add(cancelable);
        cancelableList.put(objName, mList);
    }

    /**
     * 取消请求
     *
     * @param tag 标识(String,或者ENTITY)
     */
    public void exitCancelable(Object tag) {
        if (null == tag) return;
        String objName = tag.getClass().getSimpleName();
        if (objName.equals("String")) {
            objName = tag.toString();
        }
        List<Callback.Cancelable> mList = cancelableList.get(objName);
        if (null != mList && mList.size() > 0) {
            for (Callback.Cancelable c : mList) {
                if (null != c && !c.isCancelled()) {
                    c.cancel();
                }
            }
            cancelableList.remove(objName);
        }
    }

    /**
     * 取消请求
     * 取消当前mContext 请求
     */
    public void exitCancelable() {
        if (null == mContext) return;
        String objName = mContext.getClass().getSimpleName();

        List<Callback.Cancelable> mList = cancelableList.get(objName);
        if (null != mList && mList.size() > 0) {
            for (Callback.Cancelable c : mList) {
                if (null != c && !c.isCancelled()) {
                    c.cancel();
                    TbLog.e("取消成功-" + objName);
                }
            }
            cancelableList.remove(objName);
        }
    }


    /**
     * 关闭全部请求
     */
    public void exitCancelableAll() {
        if (null == cancelableList || cancelableList.size() <= 0) return;
        for (String key : cancelableList.keySet()) {
            exitCancelable(key);
        }
    }


    /* 清理内存 */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}
