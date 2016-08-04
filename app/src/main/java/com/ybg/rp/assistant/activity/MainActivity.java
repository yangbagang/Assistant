package com.ybg.rp.assistant.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.fragment.VendingFragment;
import com.ybg.rp.assistant.saoma.CaptureActivity;
import com.ybg.rp.assistant.utils.AppPreferences;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends TbActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 129;

    @Bind(R.id.main_tv_title)
    TextView main_tv_title;         //标题

    @Bind(R.id.main_iv_right)
    ImageView main_iv_right;        //右侧图标

    @Bind(R.id.fl_contain)
    FrameLayout fl_contain;

    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        saveHead();

        main_iv_right.setOnClickListener(this);

        VendingFragment mVendingFragment = new VendingFragment();
        getFragmentManager().beginTransaction().replace(R.id.fl_contain, mVendingFragment).commit();
    }

    private void saveHead() {
        //判断保存头像的路径是否存在,创建文件夹
        File file = new File(Config.PATH);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();// 创建文件夹
            TbLog.i("MainActivity:", "创建头像文件夹");
        } else {
            TbLog.i("MainActivity:", "文件夹存在,不创建");
        }

        //用户没设置头像,给一张默认的图片
        File f = new File(Config.PATH, "vendinghead.jpg");
        if (!f.exists()) {
            saveBitmap();
        } else {
            //f.delete();
            TbLog.i("MainActivity:", "删除图片文件");
        }
    }

    /**
     * 主页底部按钮点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /**扫码登录自动售卖机*/
            case R.id.main_iv_right:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Utils.checkPermissions(this, REQUEST_CAMERA, Manifest.permission.CAMERA)) {
                        //有权限就直接打开相机,无权限, Utils.checkPermissions就会去申请权限-->就走onRequestPermissionsResult
                        startActivity(new Intent(this, CaptureActivity.class));
                    }
                } else {
                        startActivity(new Intent(this, CaptureActivity.class));
                }
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                startActivity(new Intent(this, CaptureActivity.class));
            } else {
                // Permission Denied
                Toast.makeText(this, "无相机权限!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 双击退出程序
     */
    private boolean is2CallBack = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (!is2CallBack) {
                Utils.showToast(this, "再按一次退出助手APP");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        is2CallBack = false;
                    }
                }, 1000);
                is2CallBack = true;
            } else {
                closeAllActivity();
            }
        }
        return true;
    }


    /**
     * 放一张用户默认图片
     */
    public void saveBitmap() {
        Log.e("MainActivity:", "设置默认头像");
        bm = BitmapFactory.decodeResource(this.getResources(), R.mipmap.default_head);
        File f = new File(Config.PATH, "vendinghead.jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("TAG", "保存了默认头像图片");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
