package com.ybg.rp.assistant.activity.erp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
 * 新增商品详情
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.erp
 * @修改记录:
 *
 * @date 2016/2/23 0023
 */
public class AddCommodityDetailActivity extends TbActivity implements View.OnClickListener {

    private final int A_REQUEST_CODE_CAMERA = 1002;
    private final int A_REQUEST_CODE_GALLERY = 1003;
    private final int A_REQUEST_CAMERA = 155;

    @Bind(R.id.addcommonditydetail_iv_left)
    ImageView iv_left;
    @Bind(R.id.addcommonditydetail_finish)
    TextView mAddcommonditydetailFinish;

    @Bind(R.id.addcommonditydetail_rl_finish)
    RelativeLayout rl_finish;       //完成

    @Bind(R.id.addcommonditydetail_tv_category)
    TextView tv_category;            //类别
    @Bind(R.id.addcommonditydetail_ll_category)
    LinearLayout ll_category;

    @Bind(R.id.addcommonditydetail_et_name)
    EditText et_name;                //商品名称

    @Bind(R.id.addcommonditydetail_et_standard)
    EditText et_standard;            //规格

    @Bind(R.id.addcommonditydetail_tv_brand)
    TextView tv_brand;                 //品牌
    @Bind(R.id.addcommonditydetail_ll_brand)
    LinearLayout ll_brand;

    @Bind(R.id.addcommonditydetail_et_price)
    EditText et_price;      //价格

    @Bind(R.id.addcommonditydetail_tv_add_goods)
    TextView tv_add_goods;      //继续添加

    @Bind(R.id.addcommonditydetail_ll_uploadpic)
    LinearLayout ll_loadpic;
    @Bind(R.id.addcommonditydetail_iv_pic)
    ImageView iv_pic;   //商品图片

    private NetworkXUtils xUtils;
    private GoodsDetail goodsDetail;
    private String sidNew;
    private Bitmap goodsPicture;        //商品图片
    private String fid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity_detail);
        ButterKnife.bind(this);
        xUtils = NetworkXUtils.getInstance(this);

        //tv_brand.setText("风云");

        iv_left.setOnClickListener(this);
        rl_finish.setOnClickListener(this);
        ll_category.setOnClickListener(this);
        ll_loadpic.setOnClickListener(this);
        tv_add_goods.setOnClickListener(this);
        iv_pic.setOnClickListener(this);

        GoodsInfo goodsInfo = (GoodsInfo) getIntent().getSerializableExtra(AddCommodityActivity.ADD_SERIA_KEY);
        if (goodsInfo != null) {
            TbLog.i("ADD/获取的商品信息", goodsInfo.toString());
            String gid = goodsInfo.getGid();
            querySystemGoodsInfo(gid);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addcommonditydetail_iv_left:
                finish();
                break;
            //完成
            case R.id.addcommonditydetail_rl_finish:
                if (goodsDetail != null) {
                    addGoodsInfo(1, 1);    //设置标记, 1 为从系统商品信息点击进入,其他为不经过系统商品信息直接添加
                } else {
                    addGoodsInfo(2, 1);
                }
                break;
            //继续添加
            case R.id.addcommonditydetail_tv_add_goods:
                if (goodsDetail != null) {
                    addGoodsInfo(1, 0);    //设置标记, 1 为从系统商品信息点击进入,其他为不经过系统商品信息直接添加
                } else {
                    addGoodsInfo(2, 0);
                }
                break;
            //点击选择分类
            case R.id.addcommonditydetail_ll_category:
                Intent intent = new Intent(AddCommodityDetailActivity.this, SelectCategoryActivity.class);
                startActivityForResult(intent, 10012);
                break;

            //上传图片弹窗
            case R.id.addcommonditydetail_iv_pic:
                uploadGoodsPictureDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10012 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String category = bundle.getString("category");
            sidNew = bundle.getString("sid");
            tv_category.setText(category);
        }
    }

    /**
     * 根据传过来的 gid 查询系统的商品详细信息
     *
     * @param gid
     */
    private void querySystemGoodsInfo(String gid) {
        VCParams params = new VCParams("goodsManagementApp/querySystemGoodsInfoById");
        params.addBodyParameter("gid", gid);
        TbLog.i("AddDetail/querySystemGoodsInfoById: gid", gid);

        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("AddDetail/querySystemGoodsInfoById: ", result.toString());
                try {
                    goodsDetail = GsonUtils.createGson().fromJson(result.getString("dataList"), GoodsDetail.class);
                    if (goodsDetail != null) {
                        tv_category.setText(goodsDetail.getBigName() + "-" + goodsDetail.getSmallName());
                        et_name.setText(goodsDetail.getGoodsName());
                        et_standard.setText(goodsDetail.getGoodsDesc());    //规格
                        tv_brand.setText(goodsDetail.getBrand());
                        et_price.setHint("参考价¥" + goodsDetail.getStandardPrice());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("AddDetail/querySystemGoodsInfoById: ", s);
            }
        });

    }

    /**
     * 从文件服务器获取fid
     */
    private void getFid(File file) {
        //String filePath = Config.PATH + "goodsPic.png";
        //File file = new File(filePath);

        //将file转换为byte
        byte[] bytes = Utils.fileToByte(file);
        String base64 = Base64.encode(bytes);

        //获取fid
        RequestParams params = new RequestParams(Config.URL_FID + "uploadString");
        TbLog.i("---AddCommodityDetailActivity/uploadString", Config.URL_FID + "uploadString");
        String folder = "vendingClientHeadImage";
        params.addBodyParameter("folder", folder);               //存放服务器上的文件夹
        params.addBodyParameter("fileName", file.getName());      //存放服务器上,图片名
        params.addBodyParameter("fileString", base64);          //base64字符串
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TbLog.i("---AddCommodityDetailActivity/uploadString", s);
                try {
                    JSONObject json = new JSONObject(s);
                    fid = json.getString("fid");
                    if (fid != null) {
                        Utils.showToast(AddCommodityDetailActivity.this, "图片处理成功");
                    }
                    TbLog.i("---------AddCommodityDetailActivity/uploadString:", fid);
                    //将从 文件服务器 获取到的fid传给goodsPic

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Utils.showToast(AddCommodityDetailActivity.this, "图片服务异常,稍后重试");
                TbLog.i("---AddCommodityDetailActivity/uploadString:", "失败信息:" + throwable.getMessage());
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
     *
     * @param updateFlag    继续添加标记
     * @param finishFlag   完成标记
     */
    private void addGoodsInfo(int updateFlag, final int finishFlag) {

        String base64 = null;
        String sid = null;
        String goodsName = null;
        String goodsNo = null;
        String goodsDesc = null;
        String goodsPic = null;
        String brand = null;
        String price = null;

        //        if(fid!=null) {
        //            goodsPic = fid;     //goodsPic的参数为fid
        //        } else {
        //            goodsPic = "";
        //        }
        if (updateFlag == 1) {
            //标记1 为从系统商品进入添加

            try {
                String url = goodsDetail.getGoodsPic();//获取系统商品的url
                if (!StrUtil.isEmpty(url) && fid == null) {
                    int index = url.lastIndexOf("/");
                    String oldFid = url.substring(index);   //截取fid
                    TbLog.i("---Edit/oldFid:", oldFid);
                    goodsPic = oldFid;
                } else {
                    goodsPic = fid;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            sid = goodsDetail.getSid();
            goodsName = et_name.getText().toString().trim();
            goodsNo = "";
            goodsDesc = et_standard.getText().toString().trim();
            brand = tv_brand.getText().toString().trim();
            price = et_price.getText().toString().trim();

        } else {
            //直接添加,不经过系统商品
            goodsPic = fid;
            sid = sidNew;             //从选择分类获取到的选择的分类的sid
            goodsName = et_name.getText().toString().trim();
            goodsNo = "";
            goodsDesc = et_standard.getText().toString().trim();
            brand = tv_brand.getText().toString().trim();
            price = et_price.getText().toString().trim();
        }
        TbLog.i("AddDetail/ :", sid + "----" + goodsNo + "---" + brand + "---");

        if (StrUtil.isEmpty(tv_category.getText().toString().trim())) {
            Utils.showToast(this, "分类不能为空");
            return;
        }
        if (StrUtil.isEmpty(goodsName)) {
            Utils.showToast(this, "商品名不能为空");
            return;
        }
        if (StrUtil.isEmpty(goodsDesc)) {
            Utils.showToast(this, "规格不能为空");
            return;
        }
        if (StrUtil.isEmpty(tv_brand.getText().toString().trim())) {
            Utils.showToast(this, "品牌不能为空");
        }
        if (StrUtil.isEmpty(price)) {
            Utils.showToast(this, "售价不能为空");
            return;
        }
        if (StrUtil.isEmpty(goodsPic)) {
            Utils.showToast(this, "请上传图片");
            //return;
        }

        VCParams params = new VCParams("goodsManagementApp/addGoodsInfo");
        params.addBodyParameter("sid", sid);
        params.addBodyParameter("goodsName", goodsName);
        params.addBodyParameter("goodsNo", goodsNo);
        params.addBodyParameter("goodsDesc", goodsDesc);
        params.addBodyParameter("goodsPic", goodsPic);
        params.addBodyParameter("brand", brand);
        params.addBodyParameter("price", price);
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("AddDetail/ :", result.toString());
                Utils.showToast(AddCommodityDetailActivity.this, "添加成功");
                if (finishFlag == 1) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    finish();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("AddDetail/ :", s);
                Utils.showToast(AddCommodityDetailActivity.this, s);
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
                GalleryFinal.openGallerySingle(A_REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                dialog.dismiss();

            }
        });

        take_pictrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**相机拍照*/
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Utils.checkPermissions(AddCommodityDetailActivity.this, A_REQUEST_CAMERA, Manifest.permission.CAMERA)) {
                        //有权限就直接打开相机,无权限, Utils.checkPermissions就会去申请权限-->就走onRequestPermissionsResult
                        GalleryFinal.openCamera(A_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                    }
                } else {
                    GalleryFinal.openCamera(A_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                }
                dialog.dismiss();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**取消*/
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

                Picasso.with(AddCommodityDetailActivity.this)
                        .load(file)
                        .into(iv_pic);
                getFid(file);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            //Toast.makeText(AddCommodityDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == A_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                GalleryFinal.openCamera(A_REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
            } else {
                // Permission Denied
                Toast.makeText(this, "无相机!!权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
