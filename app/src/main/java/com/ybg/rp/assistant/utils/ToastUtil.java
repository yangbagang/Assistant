package com.ybg.rp.assistant.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ybg.rp.assistant.R;

/**
 * 名称：ToastUtil.java
 * 描述：Toast工具类.
 *
 * 包            名:      com.ybg.rp.assistant.utils
 * 类            名:      ToastUtil
 * 修 改 记 录:     // 修改历史记录，包括修改日期、修改者及修改内容
 * 版 权 所 有:     版权所有(C)2010-2015
 * 公            司:
 *
 * @author yuyucheng
 * @version V1.0
 * @date 2016/1/18 0018
 */
public class ToastUtil {
    /** 上下文. */
    private static Context mContext = null;

    /** 显示Toast. */
    public static final int SHOW_TOAST = 0;

    /**
     * 主要Handler类，在线程中可用
     * what：0.提示文本信息
     */
    private static Handler baseHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    showToast(mContext,msg.getData().getString("TEXT"));
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 描述：Toast提示文本.
     * @param text  文本
     */
    public static void showToastLong(Context context,String text) {
        mContext = context;
        if(!StrUtil.isEmpty(text)){
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 描述：Toast提示文本.
     * @param text  文本
     */
    public static void showToast(Context context,String text) {
        mContext = context;
        if(!StrUtil.isEmpty(text)){
            Toast.makeText(context,text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 自定义中间吐司
     * @param context   context
     * @param text      信息
     */
    public static  void  showCenterToast(Context context,String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
        TextView infoView = (TextView) view.findViewById(R.id.view_Toast_text);
        infoView.setText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    /**
     * 描述：Toast提示文本.
     * @param resId  文本的资源ID
     */
    public static void showToast(Context context,int resId) {
        mContext = context;
        Toast.makeText(context,""+context.getResources().getText(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 描述：在线程中提示文本信息.
     * @param resId 要提示的字符串资源ID，消息what值为0,
     */
    public static void showToastInThread(Context context,int resId) {
        mContext = context;
        Message msg = baseHandler.obtainMessage(SHOW_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("TEXT", context.getResources().getString(resId));
        msg.setData(bundle);
        baseHandler.sendMessage(msg);
    }

    /**
     * 描述：在线程中提示文本信息.
     * @param  text 消息
     */
    public static void showToastInThread(Context context,String text) {
        mContext = context;
        Message msg = baseHandler.obtainMessage(SHOW_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("TEXT", text);
        msg.setData(bundle);
        baseHandler.sendMessage(msg);
    }

}
