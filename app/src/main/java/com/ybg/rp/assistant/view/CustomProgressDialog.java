package com.ybg.rp.assistant.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybg.rp.assistant.R;


/**
 * 自定义加载进度弹窗ProgressDialog
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.view
 * @修改记录:
 *
 * @date 2015/12/11 0011
 */
public class CustomProgressDialog extends Dialog {

    private static CustomProgressDialog dialog = null;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }


    public static CustomProgressDialog createDialog(Context context) {
        dialog = new CustomProgressDialog(context, R.style.custom_progressdialog_style);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return dialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (dialog == null) {
            return;
        }
        ImageView iv_loading = (ImageView) dialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animat = (AnimationDrawable) iv_loading.getBackground();
        animat.start();
    }

    public void setMessage(CharSequence message) {
        TextView tv_message = (TextView) dialog.findViewById(R.id.progress_tv_loadingmsg);
        if (tv_message != null) {
            tv_message.setText(message);
        }
        //return dialog;
    }
}
