package com.ybg.rp.assistant.activity.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.myself.MoreActivity;
import com.ybg.rp.assistant.activity.myself.MyMessageActivity;
import com.ybg.rp.assistant.activity.myself.PasswordSetActivity;
import com.ybg.rp.assistant.activity.myself.UserDetailActivity;
import com.ybg.rp.assistant.activity.myself.WebMyWealthActivity;
import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.bean.UiData;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.AppPreferences;
import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.GsonUtils;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.view.CustomImgeView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.fragment
 * @修改记录:
 *
 * @date 2015/11/26 0026
 */
public class MyselfFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.myself_ll_user_detail)
    LinearLayout ll_user_detail;         //用户详情

    @Bind(R.id.myself_ll_mywealth)
    LinearLayout ll_mywealth;              //我的财富

    @Bind(R.id.myself_ll_password_set)
    LinearLayout ll_password_set;       //密码设置

    @Bind(R.id.myself_tv_name)
    TextView tv_name;
    @Bind(R.id.myself_tv_phone)
    TextView tv_phone;

    @Bind(R.id.myself_iv_head)
    CustomImgeView iv_head;       //用户头像

    //    @Bind(R.id.myself_iv_head)
    //    ImageView iv_head;       //用户头像

    @Bind(R.id.myself_ll_mymessage)
    LinearLayout ll_mymessage;      //我的消息

    @Bind(R.id.myself_ll_more)
    LinearLayout ll_more;     //更多

    @Bind(R.id.test_db)
    Button mTestDb;

    @Bind(R.id.myself_iv_mywealth)
    ImageView iv_mywealth;
    @Bind(R.id.myself_iv_password)
    ImageView iv_password;


    private NetworkXUtils xUtils;
    private String name;
    private String phone;
    private String fid;     //图片的fid

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        xUtils = NetworkXUtils.getInstance(getActivity());

        ll_user_detail.setOnClickListener(this);
        ll_mywealth.setOnClickListener(this);
        ll_password_set.setOnClickListener(this);
        ll_mymessage.setOnClickListener(this);
        ll_more.setOnClickListener(this);

        //获取ui 的json数据
        String json = preferences.getString("ui", "");
        TbLog.i("----ui数据json/:" + json);
        JSONObject result = null;
        try {
            result = new JSONObject(json);
            Type type = new TypeToken<List<UiData>>() {
            }.getType();
            List<UiData> uiDatas = GsonUtils.createGson().fromJson(result.getString("list"), type);
            reFreshUi(uiDatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //设置登录获取的数据,
        String name = preferences.getString("name", "");
        String phone = preferences.getString("phone", "");
        tv_name.setText(name);
        if (phone.equals("null")) {
            tv_phone.setText("");
        } else {
            tv_phone.setText(phone);
        }

    }

    /**
     * 修改显示的界面
     *
     * @param list
     */
    private void reFreshUi(List<UiData> list) {
        TbLog.i("---MyselfFragment/:" + "刷新ui");
        for (UiData uiData : list) {
            int authority = uiData.getAuthority();
            String menuName = uiData.getMenuName();
            switch (menuName) {
                case "我的财富":
                    if (authority == 1) {
                        ll_mywealth.setClickable(true);
                        iv_mywealth.setImageResource(R.mipmap.me_wealth);
                    } else {
                        ll_mywealth.setClickable(false);
                        iv_mywealth.setImageResource(R.mipmap.me_wealth_g);
                    }
                    break;
                case "密码设置":
                    if (authority == 1) {
                        ll_password_set.setClickable(true);
                        iv_password.setImageResource(R.mipmap.me_password);
                    } else {
                        ll_password_set.setClickable(false);
                        iv_password.setImageResource(R.mipmap.me_password_g);
                    }
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 获取用户信息
     */
    private void initData() {
        VCParams params = new VCParams("app/custUserInfo/queryMyInfo");
        params.addBodyParameter("operatorId", VCApplipcation.getInstance().getOperatorId());
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("--MyselfFragment/queryMyInfo:", "Response:" + result.toString());
                try {
                    name = result.getString("name");
                    phone = result.getString("phone");
                    fid = result.getString("img");
                    tv_name.setText(name);
                    if (phone.equals("null")) {
                        tv_phone.setText("");
                    } else {
                        tv_phone.setText(phone);
                    }
                    //保存fid
                    preferences.setString("fid", fid);
                    //加载 头像的url:　http://192.168.2.62/FileServer/file/preview/fid
                    Picasso.with(getActivity())
                            .load(Config.URL_FID + "preview/" + fid)
                            .placeholder(R.mipmap.default_head)          //默认图片
                            .error(R.mipmap.default_head)                //加载错误后的图片
                            .into(iv_head);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String msg) {
                TbLog.i("--MyselfFragment/queryMyInfo:", "----信息失败:" + msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /**用户详情*/
            case R.id.myself_ll_user_detail:
                startActivity(new Intent(getActivity(), UserDetailActivity.class));
                break;

            /**我的财富*/
            case R.id.myself_ll_mywealth:
                startActivity(new Intent(getActivity(), WebMyWealthActivity.class));
                break;

            /**密码设置*/
            case R.id.myself_ll_password_set:
                startActivity(new Intent(getActivity(), PasswordSetActivity.class));
                break;

            /**我的消息*/
            case R.id.myself_ll_mymessage:
                startActivity(new Intent(getActivity(), MyMessageActivity.class));
                break;

            /**更多*/
            case R.id.myself_ll_more:
                startActivity(new Intent(getActivity(), MoreActivity.class));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private AppPreferences preferences = AppPreferences.getInstance();
}
