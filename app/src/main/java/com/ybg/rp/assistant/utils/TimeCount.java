package com.ybg.rp.assistant.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.widget.Button;

import com.ybg.rp.assistant.R;


/**
 * 验证码 计时器
 *
 * @包名: com.ybg.rp.assistant.util
 * @修改记录:
 *
 * @date 2015/12/25 0025
 */
public class TimeCount extends CountDownTimer {

    private Button btn;
    private Drawable dra;
    private Context mContext;

    /**
     *
     * @param millisInFuture    倒计时总时间
     * @param countDownInterval     单位时间
     * @param context
     * @param btn      控件
     */
    public TimeCount(long millisInFuture, long countDownInterval, Context context, Button btn) {
        super(millisInFuture, countDownInterval);
        this.mContext = context;
        this.btn = btn;
        dra = btn.getBackground();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        btn.setClickable(false);
        btn.setBackgroundResource(R.drawable.border_round_gray);
        btn.setTextColor(mContext.getResources().getColor(R.color.white));
        int lon = (int) (millisUntilFinished / Config.MSGINTERVAL);
        btn.setText(String.format(mContext.getResources().getString(R.string.message_send_can), lon));
    }

    @Override
    public void onFinish() {
        btn.setText(mContext.getResources().getString(R.string.message_re_verifCode));
        if (null != dra) {
            btn.setBackgroundDrawable(dra);
        }
        btn.setTextColor(mContext.getResources().getColor(R.color.white));
        btn.setClickable(true);
    }
}
