package com.ybg.rp.assistant.activity.erp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.TbActivity;
import com.ybg.rp.assistant.adapter.GoodsAdapter;
import com.ybg.rp.assistant.bean.GoodsInfo;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.side.ClearEditText;
import com.ybg.rp.assistant.utils.GsonUtils;
import com.ybg.rp.assistant.utils.StrUtil;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 新增商品
 * <p/>
 * 查询的是系统的商品,尽量让商户选择系统的商品
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.erp
 * @修改记录:
 *
 * @date 2016/2/23 0023
 */
public class AddCommodityActivity extends TbActivity implements View.OnClickListener {

    public static final String ADD_SERIA_KEY = "seria_key";


    @Bind(R.id.addcommondity_iv_left)
    ImageView iv_left;

    @Bind(R.id.addcommondity_search)
    ClearEditText search;            //搜索

    @Bind(R.id.addcommondity_ll_nodata)
    LinearLayout ll_nodata;          //无数据

    @Bind(R.id.addcommondity_listview)
    ListView listview;

    @Bind(R.id.addcommondity_tv_add_goods)
    TextView tv_add_goods;           //去添加

    @Bind(R.id.addcommondity_ll_havedata)
    LinearLayout ll_havedata;
    @Bind(R.id.addcommondity_iv_scan)
    ImageView iv_scan;

    private List<GoodsInfo> listDatas;      //数据
    private NetworkXUtils xUtils;
    private int start = 1;          // 初始加载的页数
    private String scan;            //搜索框中的text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity);
        ButterKnife.bind(this);
        xUtils = NetworkXUtils.getInstance(this);

        iv_left.setOnClickListener(this);
        tv_add_goods.setOnClickListener(this);
        iv_scan.setOnClickListener(this);

        listDatas = new ArrayList<GoodsInfo>();

        search.setOnKeyListener(keyListener);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (StrUtil.isEmpty(str)) {
                    tv_add_goods.setVisibility(View.GONE);
                    ll_havedata.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.GONE);
                    listDatas.clear();
                    GoodsAdapter adapter = new GoodsAdapter(AddCommodityActivity.this, listDatas, 1);
                    listview.setAdapter(adapter);
                    Utils.setListViewHeight(listview);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addcommondity_iv_left:
                finish();
                break;

            //去添加--到添加的详情
            case R.id.addcommondity_tv_add_goods:
                startActivity(new Intent(this, AddCommodityDetailActivity.class));
                break;

            //扫码
            case R.id.addcommondity_iv_scan:
                //startActivity(new Intent(this, CaptureActivity.class));
                break;
        }
    }

    /**
     * 查询系统商品
     * 获取网络数据
     */
    private void getData(int page, String keyWord, final boolean isRefresh) {
        VCParams params = new VCParams("goodsManagementApp/querySystemGoodsByNameAndSn");
        params.addBodyParameter("keyWord", keyWord);
        //params.addBodyParameter("currPage", page + "");

        TbLog.i("---Add/querySystemGoodsByNameAndSn/keyWord:", keyWord);
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("---Add/querySystemGoodsByNameAndSn:", result.toString());

                try {
                    Type type = new TypeToken<List<GoodsInfo>>() {
                    }.getType();
                    List<GoodsInfo> list = GsonUtils.createGson().fromJson(result.getString("dataList"), type);

                    if (list.size() > 0) {
                        listDatas.addAll(list);
                        initData();
                    } else {
                        Utils.showToast(AddCommodityActivity.this, "暂无更多数据");
                    }

                    if (listDatas.size() <= 0) {
                        ll_havedata.setVisibility(View.GONE);
                        ll_nodata.setVisibility(View.VISIBLE);
                        tv_add_goods.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                    } else {
                        ll_havedata.setVisibility(View.VISIBLE);
                        tv_add_goods.setVisibility(View.VISIBLE);
                        ll_nodata.setVisibility(View.GONE);
                        listview.setVisibility(View.VISIBLE);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("---Add/querySystemGoodsByNameAndSn 失败信息 :", s);
                Utils.showToast(AddCommodityActivity.this, s);
            }
        });
    }

    private void initData() {
        GoodsAdapter adapter = new GoodsAdapter(this, listDatas, 1);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = listDatas.get(position);
                TbLog.i("ADD/查询的系统商品", goodsInfo.toString());
                Intent intent = new Intent(AddCommodityActivity.this, AddCommodityDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ADD_SERIA_KEY, goodsInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent,101);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //点击完成本页面结束
        if(requestCode == 101 && resultCode == RESULT_OK) {
            finish();
        }
    }

    //搜索监听
    private View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                /*隐藏软键盘*/
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                TbLog.i("AddCommodity:", "搜索");
                scan = search.getText().toString();
                //getData(1, scan, false);
                searchFor(scan);
                return true;
            }
            return false;
        }
    };

    //搜索功能,从服务器获取数据
    private void searchFor(String scan) {
        getData(1, scan, false);
        GoodsAdapter adapter = new GoodsAdapter(this, listDatas, 1);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listDatas.clear();
    }
}
