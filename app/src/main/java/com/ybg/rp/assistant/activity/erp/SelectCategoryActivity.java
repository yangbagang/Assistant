package com.ybg.rp.assistant.activity.erp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.adapter.BigCategoryAdapter;
import com.ybg.rp.assistant.adapter.SmallCategoryAdapter;
import com.ybg.rp.assistant.base.BaseActivity;
import com.ybg.rp.assistant.bean.GoodsBigCategory;
import com.ybg.rp.assistant.bean.GoodsSmallCategory;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.utils.GsonUtils;
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
 * 选择商品的大小类别
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.erp
 * @修改记录:
 *
 * @date 2016/2/24 0024
 */
public class SelectCategoryActivity extends BaseActivity {


    @Bind(R.id.select_bigcategory_listview)
    ListView bigListview;

    @Bind(R.id.select_smallcategory_listview)
    ListView smallListview;

    private NetworkXUtils xUtils;
    private LinearLayout.LayoutParams mLayoutParams;

    private List<GoodsBigCategory> bigCategoryDatas;    //大类数据
    private List<GoodsSmallCategory> smallCategoryDatas;   //小类数据
    private BigCategoryAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        ButterKnife.bind(this);
        initView();
        xUtils = NetworkXUtils.getInstance(this);

        mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(this, 50));


        bigCategoryDatas = new ArrayList<GoodsBigCategory>();
        smallCategoryDatas = new ArrayList<GoodsSmallCategory>();

        queryBigCategory();
    }

    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "选择商品类别", null, null);
    }

    //加载大分类数据
    private void loadBigCategory() {
        mAdapter = new BigCategoryAdapter(this, bigCategoryDatas);
        bigListview.setAdapter(mAdapter);
        bigListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
                GoodsBigCategory goodsBigCategory = bigCategoryDatas.get(position);
                String bid = goodsBigCategory.getId();
                String bigCategory = goodsBigCategory.getCategory();    //大类名称
                querySmallCategory(bid, bigCategory);

            }
        });
    }

    //加载小分类数据
    private void loadSmallCategory(final String bigCategory) {
        SmallCategoryAdapter adapter = new SmallCategoryAdapter(this, smallCategoryDatas);
        smallListview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        smallListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsSmallCategory goodsSmallCategory = smallCategoryDatas.get(position);
                String smallCategory = goodsSmallCategory.getCategory();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("category", bigCategory + "-" + smallCategory);       //分类名称
                bundle.putString("sid", goodsSmallCategory.getId());      //小类别id
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 查询商品大类
     */
    private void queryBigCategory() {
        VCParams params = new VCParams("goodsManagementApp/queryBigCategory");
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("---SelectCategoryActivity/queryBigCategory:", result.toString());
                try {
                    Type type = new TypeToken<List<GoodsBigCategory>>() {
                    }.getType();
                    List<GoodsBigCategory> list = GsonUtils.createGson().fromJson(result.getString("dataList"), type);

                    bigCategoryDatas.addAll(list);
                    loadBigCategory();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("---SelectCategoryActivity/queryBigCategory:", s);
            }
        });
    }

    /**
     * 根据大类查询小类
     */
    private void querySmallCategory(String bid, final String bigCategory) {
        VCParams params = new VCParams("goodsManagementApp/querySmallCategory");
        params.addBodyParameter("bid", bid);
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("---SelectCategoryActivity/querySmallCategory:", result.toString());
                try {
                    Type type = new TypeToken<List<GoodsSmallCategory>>() {
                    }.getType();
                    List<GoodsSmallCategory> list = GsonUtils.createGson().fromJson(result.getString("dataList"), type);
                    smallCategoryDatas.clear();
                    smallCategoryDatas.addAll(list);
                    loadSmallCategory(bigCategory);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("---SelectCategoryActivity/querySmallCategory:", s);
            }
        });
    }
}
