package com.ybg.rp.assistant.activity.myself;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.ExitActivity;
import com.ybg.rp.assistant.activity.login.LoginActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.base.BaseActivity;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.AppPreferences;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;
import com.ybg.rp.assistant.view.CustomImgeView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.qqtheme.framework.picker.AddressPicker;

/**
 * 用户详情
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.myself
 * @修改记录:
 *
 * @date 2015/12/3 0003
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.user_detail_ll_head)
    LinearLayout ll_head;           //线性头像区域

    @Bind(R.id.user_detail_iv_head)
    CustomImgeView iv_head;         //用户头像

    @Bind(R.id.user_detail_ll_area)
    LinearLayout ll_area;       //地区点击

    @Bind(R.id.user_detail_tv_area)
    TextView tv_area;               //地区

    @Bind(R.id.user_detail_tv_phone)
    TextView tv_phone;              //手机号

    @Bind(R.id.user_detail_tv_name)
    TextView tv_name;               //名字

    @Bind(R.id.user_detail_tv_certificateNo)
    TextView tv_certificateNo;        //证件号码

    @Bind(R.id.user_detail_tv_exit)
    TextView tv_exit;         //退出登录

    @Bind(R.id.user_detail_tv_last_time)
    TextView tv_last_time;      //上次登录时间

    @Bind(R.id.user_detail_tv_login_dev)
    TextView tv_login_dev;       //登录设备


    private NetworkXUtils xUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        xUtils = NetworkXUtils.getInstance(this);

        initView();

        ll_head.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
        ll_area.setOnClickListener(this);
    }
    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "用户详情", null, null);
    }

    /**
     * 加载网络最新用户信息
     */
    @Override
    protected void onResume() {
        super.onResume();

        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /**头像*/
            case R.id.user_detail_ll_head:
                startActivity(new Intent(UserDetailActivity.this, UserHeadActivity.class));
                break;

            /**退出登录*/
            case R.id.user_detail_tv_exit:
                VCApplipcation.getInstance().clearLoginParams();
                AppPreferences.getInstance().setString("ui", "");
                ExitActivity.getInstance().exit();
                Intent intent = new Intent();
                intent.setClass(UserDetailActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            /**选择省市区*/
            case R.id.user_detail_ll_area:
                //selectCity();
                break;
        }
    }

    //省市区选择
    private void selectCity() {
        ArrayList<AddressPicker.Province> data = new ArrayList<AddressPicker.Province>();
        String json = Utils.readText(this, "city.json");
        data.addAll(JSON.parseArray(json, AddressPicker.Province.class));
        AddressPicker picker = new AddressPicker(this, data);
        //picker.setHideProvince(true);
        //默认选择
        picker.setSelectedItem("广东省", "深圳市", "南山区");
        picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(String province, String city, String county) {
                tv_area.setText(province + "-" + city + "-" + county);
            }
        });
        picker.show();
    }

    private void initData() {
        VCParams params = new VCParams("app/custUserInfo/queryMyInfo");
        params.addBodyParameter("operatorId", VCApplipcation.getInstance().getOperatorId());
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("--UserDetail/queryMyInfo:", "Response:" + result.toString());
                try {
                    Picasso.with(UserDetailActivity.this)
                            .load(Config.URL_FID + "preview/" + result.getString("img"))
                            .placeholder(R.mipmap.default_head)          //默认图片
                            .error(R.mipmap.default_head)                //加载错误后的图片
                            .into(iv_head);

                    if (result.getString("isSuperAdmin").equals("0")) {    //非超级操作员,暂时只获取姓名+号码
                        String name = result.getString("name");
                        String phone = result.getString("phone");
                        tv_name.setText(name);
                        tv_phone.setText(phone);
                    } else {
                        String name = result.getString("name");
                        String phone = result.getString("phone");
                        String certificateNo = result.getString("certificateNo");
                        String province = result.getString("province");
                        //隐藏部分证件号码
                        String newCertificateNo = certificateNo.substring(0, 4) + "********" + certificateNo.substring(12);
                        tv_name.setText(name);
                        tv_phone.setText(phone);
                        tv_certificateNo.setText(newCertificateNo);
                        tv_area.setText(province);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String msg) {

            }
        });
    }
}
