package com.ybg.rp.assistant.activity.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.erp.ShangpinManageActivity;
import com.ybg.rp.assistant.bean.UiData;
import com.ybg.rp.assistant.utils.AppPreferences;
import com.ybg.rp.assistant.utils.GsonUtils;
import com.ybg.rp.assistant.utils.TbLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ERP 页面
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.fragment
 * @修改记录:
 *
 * @date 2015/11/26 0026
 */
public class ERPFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.erp_ll_goods_manage)
    LinearLayout ll_goods_manage;      //商品管理

    @Bind(R.id.erp_ll_category_set)
    LinearLayout ll_category_set;       //货品类别设置

    @Bind(R.id.erp_iv_goods_manage)
    ImageView iv_goods_manage;
    @Bind(R.id.erp_tv_goods_manage)
    TextView tv_goods_manage;

    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_erp, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ll_goods_manage.setOnClickListener(this);
        ll_category_set.setOnClickListener(this);

//        dialog = new ProgressDialog(getActivity());
//        dialog.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE);
//        dialog.setMessage("加载中...");
//        dialog.show();

        //获取数据,更具数据 显示,隐藏 界面
//        List<UiData> list = null;
//        try {
//            Bundle bundle = getArguments();
//            list = (List<UiData>) bundle.getSerializable("uiDatas");
//            if (list.size() > 0) {
//                reFreshUi(list);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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

    }

    /**
     * 修改显示的界面
     *
     * @param list
     */
    private void reFreshUi(List<UiData> list) {
        TbLog.i("---ERPFragment/:" + "刷新ui");
        for (UiData uiData : list) {
            int authority = uiData.getAuthority();
            String menuName = uiData.getMenuName();
            switch (menuName) {
                case "商品管理":
                    if (authority == 1) {
                        ll_goods_manage.setClickable(true);
                        iv_goods_manage.setImageResource(R.mipmap.icon_goods_manage);
                        tv_goods_manage.setTextColor(getResources().getColor(R.color.default_text));
                    } else {
                        ll_goods_manage.setClickable(false);
                        iv_goods_manage.setImageResource(R.mipmap.icon_goods_manage_g);
                        tv_goods_manage.setTextColor(getResources().getColor(R.color.no_permission));

                    }
                    break;
            }
        }
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /**商品管理*/
            case R.id.erp_ll_goods_manage:
                startActivity(new Intent(getActivity(), ShangpinManageActivity.class));
                break;

            /**货品类别设置*/
            case R.id.erp_ll_category_set:
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
