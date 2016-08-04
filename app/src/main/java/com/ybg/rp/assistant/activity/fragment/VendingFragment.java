package com.ybg.rp.assistant.activity.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.vending.WebDataCenterActivity;
import com.ybg.rp.assistant.activity.vending.WebFeedBackActivity;
import com.ybg.rp.assistant.activity.vending.WebGoodsManageActivity;
import com.ybg.rp.assistant.activity.vending.WebOperateStateActivity;
import com.ybg.rp.assistant.activity.vending.WebSellDetailActivity;
import com.ybg.rp.assistant.activity.vending.WebSuggestReplenishActivity;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 售卖机页面
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.fragment
 * @修改记录:
 *
 * @date 2015/11/26 0026
 */
public class VendingFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.vending_ll_add_goods)
    LinearLayout ll_add_goods;          //建议补货

    @Bind(R.id.vending_ll_feedback)
    LinearLayout ll_feedback;            //故障反馈

    @Bind(R.id.vending_ll_operate)
    LinearLayout ll_operate;             //运营概况

    @Bind(R.id.vending_ll_goods_manage)
    LinearLayout ll_goods_manage;       //货品管理

    @Bind(R.id.vending_ll_sell_detail)
    LinearLayout ll_sell_detail;           //销售明细

    @Bind(R.id.vending_ll_data_center)
    LinearLayout ll_data_center;           //数据中心

    @Bind(R.id.vending_tv_add_goods_number)
    TextView tv_add_goods_number;           //建议补货台数

    @Bind(R.id.vending_tv_net_fail_number)
    TextView tv_net_fail_number;            //网络故障台数

    @Bind(R.id.vending_tv_operate_number)
    TextView tv_operate_number;             //轨道故障台数

    @Bind(R.id.vending_iv_add_goods)
    ImageView iv_add_goods;
    @Bind(R.id.vending_iv_feedback)
    ImageView iv_feedback;
    @Bind(R.id.vending_iv_operate)
    ImageView iv_operate;
    @Bind(R.id.vending_iv_goods_manage)
    ImageView iv_goods_manage;
    @Bind(R.id.vending_iv_data_center)
    ImageView iv_data_center;
    @Bind(R.id.vending_iv_sell_detail)
    ImageView iv_sell_detail;

    @Bind(R.id.vending_tv_add_goods)
    TextView tv_add_goods;
    @Bind(R.id.vending_tv_feedback)
    TextView tv_feedback;
    @Bind(R.id.vending_tv_operate)
    TextView tv_operate;
    @Bind(R.id.vending_tv_goods_manage)
    TextView tv_goods_manage;
    @Bind(R.id.vending_tv_data_center)
    TextView tv_data_center;
    @Bind(R.id.vending_tv_sell_detail)
    TextView tv_sell_detail;


    private NetworkXUtils xUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vending, null);
        ButterKnife.bind(this, view);
        xUtils = NetworkXUtils.getInstance(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        xUtils = NetworkXUtils.getInstance(getActivity());

        ll_goods_manage.setOnClickListener(this);
        ll_add_goods.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        ll_operate.setOnClickListener(this);
        ll_sell_detail.setOnClickListener(this);
        ll_data_center.setOnClickListener(this);

    }

    //时时获取数据
    @Override
    public void onResume() {
        super.onResume();

        //获取补货,故障,运营台数
        initData();
    }

    /**
     * 加载网络故障台数
     */
    private void initData() {
        VCParams params = new VCParams("vendMachineInfo/getNumOfMachineByToken");
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject json) throws InterruptedException {
                TbLog.i("---VendingFragment/countMachineFaultByIds--", json.toString());
                try {
                    String success = json.getString("success");
                    if ("true".equals(success)) {
                        String machineCount = json.getString("machineCount");  //运营台数
                        String replenishCount = json.getString("replenishCount");  //补货台数
                        String faultCount = json.getString("faultCount");  //故障台数
                        if (machineCount != null && replenishCount != null && faultCount != null) {
                            tv_add_goods_number.setText(replenishCount + "台");
                            tv_net_fail_number.setText(faultCount + "台");
                            tv_operate_number.setText(machineCount + "台");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("---VendingFragment/countMachineFaultByIds--", "--失败信息:" + s);
                Utils.showToast(getActivity(), s);
            }
        });

    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /**建议补货*/
            case R.id.vending_ll_add_goods:
                //startActivity(new Intent(getActivity(), WebSuggestReplenishActivity.class));
                break;

            /**故障反馈*/
            case R.id.vending_ll_feedback:
                //startActivity(new Intent(getActivity(), WebFeedBackActivity.class));
                break;

            /**运营概况*/
            case R.id.vending_ll_operate:
                //startActivity(new Intent(getActivity(), WebOperateStateActivity.class));
                break;

            /**数据中心*/
            case R.id.vending_ll_data_center:
                startActivity(new Intent(getActivity(), WebDataCenterActivity.class));
                break;

            /**货品管理*/
            case R.id.vending_ll_goods_manage:
                startActivity(new Intent(getActivity(), WebGoodsManageActivity.class));
                break;

            /**销售明细*/
            case R.id.vending_ll_sell_detail:
                startActivity(new Intent(getActivity(), WebSellDetailActivity.class));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
