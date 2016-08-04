package com.ybg.rp.assistant.activity.erp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.squareup.picasso.Picasso;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.TbActivity;
import com.ybg.rp.assistant.bean.GoodsDetail;
import com.ybg.rp.assistant.bean.GoodsInfo;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.Base64;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.GsonUtils;
import com.ybg.rp.assistant.utils.StrUtil;
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

/**
 * 编辑自己有的商品
 * 更新自己的商品
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.erp
 * @修改记录:
 *
 * @date 2016/2/25 0025
 */
public class EditCommodityDetailActivity extends TbActivity implements View.OnClickListener {

    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private final int REQUEST_CAMERA = 154;

    @Bind(R.id.editcommonditydetail_iv_left)
    ImageView iv_left;                      //结束

    @Bind(R.id.editcommonditydetail_finish)
    TextView mEditcommonditydetailFinish;
    @Bind(R.id.editcommonditydetail_rl_finish)
    RelativeLayout rl_finish;            //完成

    @Bind(R.id.editcommonditydetail_tv_category)
    TextView tv_category;
    @Bind(R.id.editcommonditydetail_ll_category)
    LinearLayout ll_category;       //分类.类别

    @Bind(R.id.editcommonditydetail_et_name)
    EditText et_name;       //名称

    @Bind(R.id.editcommonditydetail_et_standard)
    EditText et_standard;       //规格

    @Bind(R.id.editcommonditydetail_tv_brand)
    TextView tv_brand;          //品牌
    @Bind(R.id.editcommonditydetail_ll_brand)
    LinearLayout ll_brand;

    @Bind(R.id.editcommonditydetail_et_price)
    EditText et_price;      //价格

    @Bind(R.id.editcommonditydetail_ll_uploadpic)
    LinearLayout ll_uploadpic;      //上传图片
    @Bind(R.id.editcommonditydetail_iv_pic)
    ImageView iv_pic;       //商品图片

    private Bitmap goodsPic;
    private NetworkXUtils xUtils;
    private GoodsDetail goodsDetail;
    private String fid;
    private String newSid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_commodity_detail);
        ButterKnife.bind(this);
        xUtils = NetworkXUtils.getInstance(this);

        iv_left.setOnClickListener(this);
        rl_finish.setOnClickListener(this);
        ll_category.setOnClickListener(this);
        ll_uploadpic.setOnClickListener(this);
        iv_pic.setOnClickListener(this);

        GoodsInfo goodsInfo = (GoodsInfo) getIntent().getSerializableExtra(ShangpinManageActivity.SIA_KEY);
        if (goodsInfo != null) {
            String gid = goodsInfo.getGid();
            queryGoodsInfo(gid);
        }

    }

    //根据sid,查询出商品的信息
    private void queryGoodsInfo(String gid) {
        VCParams params = new VCParams("goodsManagementApp/queryGoodsInfoById");
        params.addBodyParameter("gid", gid);
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("Edit/queryGoodsInfoById:", result.toString());
                try {
                    goodsDetail = GsonUtils.createGson().fromJson(result.getString("dataList"), GoodsDetail.class);
                    if (goodsDetail != null) {
                        tv_category.setText(goodsDetail.getBigName() + "-" + goodsDetail.getSmallName());
                        et_name.setText(goodsDetail.getGoodsName());
                        et_standard.setText(goodsDetail.getGoodsDesc());
                        tv_brand.setText(goodsDetail.getBrand());
                        et_price.setText(goodsDetail.getStandardPrice() + "");

                        String goodsPic = goodsDetail.getGoodsPic();
                        Picasso.with(EditCommodityDetailActivity.this)
                                .load(goodsPic)
                                .placeholder(R.mipmap.icon_photo)
                                .into(iv_pic);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("Edit/queryGoodsInfoById 失败:", s);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editcommonditydetail_iv_left:
                finish();
                break;
            //点击完成
            case R.id.editcommonditydetail_rl_finish:
                updateGoodsInfo();
                break;
            //选择分类
            case R.id.editcommonditydetail_ll_category:
                Intent intent = new Intent(EditCommodityDetailActivity.this, SelectCategoryActivity.class);
                startActivityForResult(intent, 10011);
                break;
            case R.id.editcommonditydetail_iv_pic:
                uploadGoodsPictureDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10011 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String category = bundle.getString("category");
            newSid = bundle.getString("sid");

            TbLog.i("---EditDetail/新类别newSid: " + newSid);
            tv_category.setText(category);
        }
    }

    /**
     * 从文件服务器获取fid
     */
    private void getFid(File file) {
//        String filePath = Config.PATH + "goodsPic.png";
//        File file = new File(filePath);

        TbLog.i("开始上传");
        //将file转换为byte
        byte[] bytes = Utils.fileToByte(file);
        String base64 = Base64.encode(bytes);

        //获取fid
        RequestParams params = new RequestParams(Config.URL_FID + "uploadString");
        String folder = "vendingClientHeadImage";
        params.addBodyParameter("folder", folder);               //存放服务器上的文件夹
        params.addBodyParameter("fileName", file.getName());      //存放服务器上,图片名
        params.addBodyParameter("fileString", base64);          //base64字符串
        Log.d("EditDetail:", "64长度" + base64.length());
        TbLog.i("---folder:" + folder + "---fileName:" + file.getName() + "----base64长度:" + base64.length());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TbLog.i("---AddgroupDetail/uploadString", s);
                try {
                    JSONObject json = new JSONObject(s);
                    fid = json.getString("fid");
                    if (fid != null) {
                        Utils.showToast(EditCommodityDetailActivity.this, "图片处理成功");
                    }
                    TbLog.i("---------EditCommodityDetailActivity/uploadString:", fid);
                    //将从 文件服务器 获取到的fid传给goodsPic

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Utils.showToast(EditCommodityDetailActivity.this, "图片服务异常,稍后重试");
                TbLog.i("---EditCommodityDetailActivity/uploadString:", "失败信息:" + throwable.getMessage());
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
     * 更新---编辑商品信息
     */
    private void updateGoodsInfo() {
        if (goodsDetail == null) {
            Utils.showToast(this, "获取商品信息失败");
            return;
        }

        String goodsPic = null;
        String url = goodsDetail.getGoodsPic();

        if (!StrUtil.isEmpty(url) && fid == null) {
            try {
                //处理 编辑商品没有选择新照片的情况,就用原来的
                int index = url.lastIndexOf("/");
                String oldFid = url.substring(index);
                TbLog.i("---Edit/oldFid:", oldFid);
                goodsPic = oldFid;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            goodsPic = fid;
        }

        String sid = goodsDetail.getSid();
        if (!StrUtil.isEmpty(newSid)) {
            sid = newSid;
        }
        String goodsNo = goodsDetail.getGoodsNo();
        String goodsName = et_name.getText().toString().trim();
        String goodsDesc = et_standard.getText().toString().trim();
        //        String brand = goodsDetail.getBrand();
        String brand = tv_brand.getText().toString().trim();
        String price = et_price.getText().toString().trim();
        String gid = goodsDetail.getGid();
        TbLog.i("EditDetail/ :", sid + "----" + goodsNo + "---" + brand + "---" + gid);
        TbLog.i("--fid" + goodsPic);
        if (StrUtil.isEmpty(goodsName)) {
            Utils.showToast(this, "商品名不能为空");
            return;
        }
        if (StrUtil.isEmpty(goodsDesc)) {
            Utils.showToast(this, "商品规格不能为空");
            //return;
        }
        if (StrUtil.isEmpty(price)) {
            Utils.showToast(this, "商品售价不能为空");
            return;
        }
        if (StrUtil.isEmpty(goodsPic)) {
            Utils.showToast(this, "请上传图片");
            //return;
        }

        VCParams params = new VCParams("goodsManagementApp/updateGoodsInfo");
        params.addBodyParameter("sid", sid);
        params.addBodyParameter("goodsNo", goodsNo);
        params.addBodyParameter("goodsName", goodsName);
        params.addBodyParameter("goodsDesc", goodsDesc);
        params.addBodyParameter("goodsPic", goodsPic);
        params.addBodyParameter("brand", brand);
        params.addBodyParameter("price", price);
        params.addBodyParameter("gid", gid);

        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("Edit/updateGoodsInfo :", result.toString());
                Utils.showToast(EditCommodityDetailActivity.this, "更新成功");
                finish();
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("Edit/updateGoodsInfo 失败:", s);
                Utils.showToast(EditCommodityDetailActivity.this, s);
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

                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                dialog.dismiss();
            }
        });

        take_pictrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**相机拍照*/

                if (Build.VERSION.SDK_INT >= 23) {
                    if (Utils.checkPermissions(EditCommodityDetailActivity.this, REQUEST_CAMERA, Manifest.permission.CAMERA)) {
                        //有权限就直接打开相机,无权限, Utils.checkPermissions就会去申请权限-->就走onRequestPermissionsResult
                        GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                    }
                } else {
                    GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
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

    /**
     * 权限监听结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
            } else {
                // Permission Denied
                Toast.makeText(this, "无相机!!权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //获取图片监听
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                File file = new File(resultList.get(0).getPhotoPath());
                long length = file.length();
                TbLog.i("MyselfFragment/:" + length);

                Picasso.with(EditCommodityDetailActivity.this)
                        .load(file)
                        .into(iv_pic);
                getFid(file);
//                getFFF(file);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            //Toast.makeText(EditCommodityDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

}
