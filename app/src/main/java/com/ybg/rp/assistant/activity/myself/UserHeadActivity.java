package com.ybg.rp.assistant.activity.myself;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.base.BaseActivity;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.Base64;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 用户头像
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.myself
 * @修改记录:
 *
 * @date 2015/12/9 0009
 */
public class UserHeadActivity extends BaseActivity implements View.OnClickListener {

    private final int HEAD_REQUEST_CODE_CAMERA = 1000;
    private final int HEAD_REQUEST_CODE_GALLERY = 1001;
    private final int HEAD_REQUEST_CAMERA = 152;


    @Bind(R.id.user_head_ll_from_photos)
    LinearLayout ll_from_photos;             //相册选择

    @Bind(R.id.user_head_ll_take_picture)
    LinearLayout ll_take_picture;            //拍照

    @Bind(R.id.user_head_iv_head_icon)
    CircleImageView iv_head_icon;           //头像

    private NetworkXUtils xUtils;
    private Bitmap headBitmap;        //头像Bitmap
    private String fid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_head);
        ButterKnife.bind(this);

        initView();

        xUtils = NetworkXUtils.getInstance(this);

        initView();
        ll_from_photos.setOnClickListener(this);
        ll_take_picture.setOnClickListener(this);
    }

    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "用户头像", null, null);
        //从Sd中找头像，转换成Bitmap
        Bitmap bt = BitmapFactory.decodeFile(Config.PATH + "vendinghead.jpg");
        if (bt != null) {
            Drawable drawable = new BitmapDrawable(bt);     //转换成drawable
            iv_head_icon.setImageDrawable(drawable);
        } else {
            //从服务器获取图片
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /**从相册选择*/
            case R.id.user_head_ll_from_photos:
                GalleryFinal.openGallerySingle(HEAD_REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                break;

            /** 相机拍照*/
            case R.id.user_head_ll_take_picture:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Utils.checkPermissions(this, HEAD_REQUEST_CAMERA, Manifest.permission.CAMERA)) {
                        //有权限就直接打开相机,无权限, Utils.checkPermissions就会去申请权限-->就走onRequestPermissionsResult
                        GalleryFinal.openCamera(HEAD_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                    }
                } else {
                    GalleryFinal.openCamera(HEAD_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == HEAD_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                GalleryFinal.openCamera(HEAD_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
            } else {
                // Permission Denied
                Toast.makeText(this, "无相机!!权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 获取图片监听
     */
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                File file = new File(resultList.get(0).getPhotoPath());
                long length = file.length();
                TbLog.i("-----UserHead/裁剪完的图片length :" + length);

                Picasso.with(UserHeadActivity.this)
                        .load(file)
                        .into(iv_head_icon);
                uploadPicture(file);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            //Toast.makeText(UserHeadActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 上传照片到  文件服务器,
     * 获取到fid , 再将fid上传到服务器
     */
    private void uploadPicture(File file) {
        //        String filePath = Config.PATH + "vendinghead.jpg";
        //        File file = new File(filePath);
        //Utils.compressBmpToFile(head, file); //

        //将file转换为byte
        byte[] bytes = Utils.fileToByte(file);
        String base64 = Base64.encode(bytes);

        TbLog.i("---上传前的base64长度--/uploadString", base64.length() + "");

        //头像限制100Kb
        //        if (base64.length() > 1024 * 100) {
        //            Utils.showToast(this, "图片过大,请重新选择!");
        //            return;
        //        }
        //获取fid
        RequestParams params = new RequestParams(Config.URL_FID + "uploadString");
        String folder = "vendingClientHeadImage";
        params.addBodyParameter("folder", folder);               //存放服务器上的文件夹
        params.addBodyParameter("fileName", file.getName());      //存放服务器上,图片名
        params.addBodyParameter("fileString", base64);          //base64字符串
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TbLog.i("---UserHeadActivity/uploadString", s);
                try {
                    JSONObject json = new JSONObject(s);
                    fid = json.getString("fid");
                    TbLog.i("--uploadString获取的fid/---"+fid);
                    //将从 文件服务器 获取到的fid上传到服务器
                    uploadFid();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                TbLog.i("---UserHeadActivity/uploadString:", "失败信息:" + throwable.getMessage());
                Utils.showToast(UserHeadActivity.this, "头像上传失败,存放本地");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 上传图片获取的fid
     */
    private void uploadFid() {
        VCParams params = new VCParams("app/custUserInfo/updatePhotos");
        String opratorId = VCApplipcation.getInstance().getOperatorId();

        params.addBodyParameter("operatorId", opratorId);
        params.addBodyParameter("fid", fid);
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                Utils.showToast(UserHeadActivity.this, "头像上传成功");
                TbLog.i("---UserHeadActivity/updatePhotos:", result.toString());
            }

            @Override
            public void onFailure(HttpException ex, String msg) {
                TbLog.i("---UserHeadActivity/updatePhotos:", "失败信息:" + msg);
                Utils.showToast(UserHeadActivity.this, msg);
            }
        });
    }

}
