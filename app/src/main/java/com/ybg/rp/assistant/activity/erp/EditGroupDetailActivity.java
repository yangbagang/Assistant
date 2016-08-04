package com.ybg.rp.assistant.activity.erp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.TbActivity;
import com.ybg.rp.assistant.bean.GoodsInfo;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.Base64;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.GsonUtils;
import com.ybg.rp.assistant.utils.StrUtil;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 编辑组合商品    详情
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.erp
 * @修改记录:
 *
 * @date 2016/3/16 0016
 */
public class EditGroupDetailActivity extends TbActivity implements View.OnClickListener {

    private final int C_REQUEST_CODE_CAMERA = 1007;
    private final int C_REQUEST_CODE_GALLERY = 1008;
    private final int C_REQUEST_CAMERA = 156;

    @Bind(R.id.editgroupdetail_iv_left)
    ImageView iv_left;

    @Bind(R.id.editgroupdetail_finish)
    TextView mEditgroupdetailFinish;
    @Bind(R.id.editgroupdetail_rl_finish)
    RelativeLayout rl_finish;               //完成

    @Bind(R.id.editgroupdetail_tv_category)
    TextView tv_category;                       //分类
    @Bind(R.id.editgroupdetail_ll_category)
    LinearLayout ll_category;

    @Bind(R.id.editgroupdetail_et_name)
    EditText et_name;
    @Bind(R.id.editgroupdetail_et_standard)
    EditText et_standard;                   //规格

    @Bind(R.id.editgroupdetail_tv_brand)
    TextView tv_brand;
    @Bind(R.id.editgroupdetail_ll_brand)
    LinearLayout ll_brand;                  //品牌

    @Bind(R.id.editgroupdetail_et_price)
    EditText et_price;

    @Bind(R.id.editgroupdetail_ll_uploadpic)
    LinearLayout ll_uploadpic;
    @Bind(R.id.editgroupdetail_iv_pic)
    ImageView iv_pic;

    private NetworkXUtils xUtils;

    private Bitmap goodsPic;    //裁剪的图片
    private String fid;     //从文件服务器获取的fid

    private List<GoodsInfo> goodsDatas;     //组合中的 小商品数据

    private String mGid;    //查询出的组合商品的gid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_detail);
        ButterKnife.bind(this);
        xUtils = NetworkXUtils.getInstance(this);

        goodsDatas = new ArrayList<>();

        iv_left.setOnClickListener(this);
        rl_finish.setOnClickListener(this);
        ll_uploadpic.setOnClickListener(this);
        ll_category.setOnClickListener(this);
        iv_pic.setOnClickListener(this);

        GoodsInfo goodsInfo = (GoodsInfo) getIntent().getSerializableExtra(ShangpinManageActivity.SIA_KEY);
        if (goodsInfo != null) {
            String gid = goodsInfo.getGid();
            getGroupGoodsById(gid);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editgroupdetail_iv_left:
                finish();
                break;

            //上传图片
            case R.id.editgroupdetail_iv_pic:
                uploadGoodsPictureDialog();
                break;

            //分类选择小商品
            case R.id.editgroupdetail_ll_category:
                Intent intent = new Intent(this, EditGroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("goodsDatas", (Serializable) goodsDatas);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1309);
                break;

            //完成,处理数据并上传到服务
            case R.id.editgroupdetail_rl_finish:
                updateGroupGoods();
                break;
        }
    }


    /**
     * 更新商品数据
     */
    private void updateGroupGoods() {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();      //要转为json串的对象

        double goodsTotalPrice = 0;     //计算总金额
        for (GoodsInfo goodsInfo : goodsDatas) {
            JSONObject object2 = new JSONObject();
            try {
                object2.put("goodsId", goodsInfo.getGid());
                object2.put("num", goodsInfo.getGoodsCount());
                object2.put("price", goodsInfo.getStandardPrice());
                array.put(object2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            double standardPrice = goodsInfo.getStandardPrice();
            goodsTotalPrice = goodsTotalPrice + standardPrice;      //选好商品的总金额
        }
        try {
            object.put("GoodsPo", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = object.toString();
        TbLog.i("---EditGroupDetail/json:", json);


        String goodsName = et_name.getText().toString().trim();
        String goodsDesc = et_standard.getText().toString().trim();
        String totalPrice = et_price.getText().toString().trim();   //填写的 自定义的价格
        String goodsPic = fid;
        String brand = tv_brand.getText().toString().trim();
        double beTotalPrice = goodsTotalPrice;          //计算的选好的商品的总价
        int goodsNum = goodsDatas.size();

        VCParams params = new VCParams("goodsManagementApp/updateGroupGoods");
        params.addBodyParameter("gid", mGid);
        params.addBodyParameter("gson", json);
        params.addBodyParameter("goodsName", goodsName);
        params.addBodyParameter("goodsDesc", goodsDesc);
        params.addBodyParameter("goodsPic", goodsPic);
        params.addBodyParameter("brand", brand);
        params.addBodyParameter("totalPrice", totalPrice);      //自定义价格
        params.addBodyParameter("beTotalPrice", beTotalPrice + "");
        params.addBodyParameter("goodsNum", goodsNum + "");

        if (StrUtil.isEmpty(goodsName)) {
            Utils.showToast(this, "商品名称不能为空");
            return;
        }
        if (StrUtil.isEmpty(goodsDesc)) {
            Utils.showToast(this, "商品规格不能为空");
            //return;
        }
        if (StrUtil.isEmpty(totalPrice)) {
            Utils.showToast(this, "商品售价不能为空");
            return;
        }

        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("---EditGroupDetail/:", result.toString());
                Utils.showToast(EditGroupDetailActivity.this, "修改成功");
                finish();
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("---EditGroupDetail/失败信息:", s);
                Utils.showToast(EditGroupDetailActivity.this, s);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        tv_category.setText(goodsDatas.size() + "件");

    }

    /**
     * 根据传过来的gid查询组合商品的详情
     *
     * @param gid
     */
    private void getGroupGoodsById(String gid) {
        VCParams params = new VCParams("goodsManagementApp/getGroupGoodsById");
        params.addBodyParameter("gid", gid);
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("---EditGroupDetail/getGroupById:", result.toString());
                try {
                    Type type = new TypeToken<List<GoodsInfo>>() {
                    }.getType();
                    List<GoodsInfo> list = GsonUtils.createGson().fromJson(result.getString("gson"), type);
                    goodsDatas.addAll(list);

                    mGid = result.getString("gid");
                    String goodsName = result.getString("goodsName");     //商品名
                    String goodsDesc = result.getString("goodsDesc");     //规格
                    String brand = result.getString("brand");             //品牌
                    String goodsPic = result.getString("goodsPic");             //图片url
                    String standardPrice = result.getString("standardPrice");
                    tv_category.setText(list.size() + "件");
                    et_name.setText(goodsName);
                    et_standard.setText(goodsDesc);
                    tv_brand.setText(brand);
                    et_price.setText(standardPrice);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("---EditGroupDetail/getGroupById:", s);
                Utils.showToast(EditGroupDetailActivity.this, s);
            }
        });
    }

    /**
     * 上传图片弹窗
     */
    private void uploadGoodsPictureDialog() {
        //初始化自定义的DiaLog  view
        View view = getLayoutInflater().inflate(R.layout.pop_upload_picture, null);
        final Dialog dialog = new Dialog(this, R.style.upload_dialog);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView take_pictrue = (TextView) view.findViewById(R.id.open_tv_take_pictrue);
        TextView from_pictrue = (TextView) view.findViewById(R.id.open_tv_from_pictrue);
        TextView tv_cancel = (TextView) view.findViewById(R.id.open_tv_cancel);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        //x y 让dialog出现在底部
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        //让控件可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        /**点击事件*/
        from_pictrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**从相册获取*/
//                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
//                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent1, 1);

                GalleryFinal.openGallerySingle(C_REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                dialog.dismiss();
            }
        });

        take_pictrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**相机拍照*/
//                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Config.PATH, Config.GOODS_PIC)));
//                startActivityForResult(intent2, 2);

                if (Build.VERSION.SDK_INT >= 23) {
                    if (Utils.checkPermissions(EditGroupDetailActivity.this, C_REQUEST_CAMERA, Manifest.permission.CAMERA)) {
                        //有权限就直接打开相机,无权限, Utils.checkPermissions就会去申请权限-->就走onRequestPermissionsResult
                        GalleryFinal.openCamera(C_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                    }
                } else {
                    GalleryFinal.openCamera(C_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                }
                dialog.dismiss();

            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //获取图片监听
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                File file = new File(resultList.get(0).getPhotoPath());
                long length = file.length();
                TbLog.i("MyselfFragment/:"+length);

                Picasso.with(EditGroupDetailActivity.this)
                        .load(file)
                        .into(iv_pic);
                getFid(file);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            //Toast.makeText(EditGroupDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    /**权限监听结果*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == C_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                GalleryFinal.openCamera(C_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
            } else {
                // Permission Denied
                Toast.makeText(this, "无相机!!权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**接收组合商品中的小商品数据*/
        if (requestCode == 1309 && resultCode == RESULT_OK) {
            goodsDatas = (List<GoodsInfo>) data.getSerializableExtra("editGoodsData");
            double totalPrice = 0;
            for (GoodsInfo info : goodsDatas) {
                double standardPrice = info.getStandardPrice();
                int goodsCount = info.getGoodsCount();
                totalPrice = totalPrice + (standardPrice * goodsCount);
            }
            et_price.setText(null);
            et_price.setHint("组合参考价¥" + totalPrice);
        }

//        /**相册获取图片--裁剪图片*/
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            cropPhoto(data.getData());
//        }
//        /**相机拍照的图片*/
//        if (requestCode == 2 && resultCode == RESULT_OK) {
//            File temp = new File(Config.PATH + Config.GOODS_PIC);
//            cropPhoto(Uri.fromFile(temp));//裁剪图片
//        }
//
//        /**拿到裁剪后的data */
//        if (requestCode == 3 && data != null) {
//            Bundle extras = data.getExtras();
//            goodsPic = extras.getParcelable("data");
//            if (goodsPic != null) {
//                /**保存并显示*/
//                Utils.setPicToSDCard(goodsPic, Config.PATH, Config.GOODS_PIC);
//                iv_pic.setImageBitmap(goodsPic);    //用ImageView显示出来
//                /**上传服务器代码*/
//                //获取fid
//                getFid();
//            }
//        }
    }

    /**
     * 从文件服务器获取fid
     */
    private void getFid(File file) {
//        String filePath = Config.PATH + "goodsPic.png";
//        File file = new File(filePath);

        //将file转换为byte
        byte[] bytes = Utils.fileToByte(file);
        String base64 = Base64.encode(bytes);

        //获取fid
        RequestParams params = new RequestParams(Config.URL_FID + "uploadString");
        String folder = "vendingClientHeadImage";
        params.addBodyParameter("folder", folder);               //存放服务器上的文件夹
        params.addBodyParameter("fileName", file.getName());      //存放服务器上,图片名
        params.addBodyParameter("fileString", base64);          //base64字符串
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TbLog.i("---AddgroupDetail/uploadString", s);
                try {
                    JSONObject json = new JSONObject(s);
                    fid = json.getString("fid");
                    if (fid != null) {
                        Utils.showToast(EditGroupDetailActivity.this, "图片处理成功");
                    }
                    TbLog.i("---------AddgroupDetail/uploadString:", fid);
                    //将从 文件服务器 获取到的fid传给goodsPic

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Utils.showToast(EditGroupDetailActivity.this, "图片服务异常,稍后重试");
                TbLog.i("---AddgroupDetail/uploadString:", "失败信息:" + throwable.getMessage());
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
     * 调用系统的裁剪
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
}
