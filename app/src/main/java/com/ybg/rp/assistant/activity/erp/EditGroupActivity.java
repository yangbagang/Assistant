package com.ybg.rp.assistant.activity.erp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.adapter.PopupGoodsAdapter;
import com.ybg.rp.assistant.base.BaseActivity;
import com.ybg.rp.assistant.bean.GoodsInfo;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.side.CharacterParser;
import com.ybg.rp.assistant.side.PinyingComparator;
import com.ybg.rp.assistant.side.SideBar;
import com.ybg.rp.assistant.utils.GsonUtils;
import com.ybg.rp.assistant.utils.StrUtil;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 编辑组合商品
 * 和新增组合逻辑一样
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.erp
 * @修改记录:
 *
 * @date 2016/3/17 0017
 */
public class EditGroupActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.addgroup_listview)
    ListView listview;

    @Bind(R.id.addgroup_tv_dialog)
    TextView tv_dialog;     //点击索引的提示

    @Bind(R.id.addgroup_sidebar)
    SideBar sidebar;
    @Bind(R.id.addgroup_pb_progress)
    ProgressBar pb_progress;
    @Bind(R.id.addgroup_icon)
    ImageView mAddgroupIcon;

    @Bind(R.id.addgroup_tv_count)
    TextView tv_count;          //选好商品的数量
    @Bind(R.id.addgroup_text)
    TextView mAddgroupText;

    @Bind(R.id.addgroup_tv_total_money)
    TextView tv_total_money;
    @Bind(R.id.addgroup_btn_finish)
    Button btn_finish;

    @Bind(R.id.addgroup_rl_detail)
    RelativeLayout rl_detail;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyingComparator pinyingComparator;

    private NetworkXUtils xUtils;
    private AddGoodsAdapter adapter;
    private PopupWindow mPopupWindow;

    private List<GoodsInfo> listDatas;

    private List<GoodsInfo> popListDatas;     //弹窗里面的listview数据
    private ListView popListview;

    private PopupGoodsAdapter popAdapter;    //弹窗数据的adapter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);
        initView();
        xUtils = NetworkXUtils.getInstance(this);
        listDatas = new ArrayList<GoodsInfo>();
        popListDatas = new ArrayList<GoodsInfo>();

        rl_detail.setOnClickListener(this);
        mAddgroupIcon.setOnClickListener(this);
        btn_finish.setOnClickListener(this);

        //获取传递过来的数据
        popListDatas = (List<GoodsInfo>) getIntent().getSerializableExtra("goodsDatas");

        double price = 0;
        int tvCount = 0;
        if(popListDatas.size()>0) {
            for (GoodsInfo info : popListDatas) {
                int goodsCount = info.getGoodsCount();
                double standardPrice = info.getStandardPrice() * goodsCount;
                price = price + standardPrice;
                tv_total_money.setText("¥ " + String.valueOf(price));
                tvCount = tvCount + goodsCount;
            }
            tv_count.setText(tvCount+"");
            tv_count.setVisibility(View.VISIBLE);
        } else {
            tv_total_money.setText("¥ " + 0);
            tv_count.setVisibility(View.INVISIBLE);
        }

        getData("");
    }

    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "编辑组合商品", null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //点击底部弹出增加商品的编辑dialog
            case R.id.addgroup_icon:
                isMenuShow = true;
                showDetailPopwindow();
                break;

            //选好了:
            case R.id.addgroup_btn_finish:
                //把集合popListDatas传过去
//                Intent intent = new Intent(this, AddGroupDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("listDatas", (Serializable) popListDatas);
//                intent.putExtras(bundle);
//                startActivity(intent);

                //把集合popListDatas传过去
                Intent intent  =new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("editGoodsData", (Serializable) popListDatas);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
                break;

            //popWindow的点击事件
            //关闭
            case R.id.pop_iv_close:
                mPopupWindow.dismiss();
                break;

            //清空
            case R.id.pop_ll_delete:
                popListDatas.clear();
                tv_count.setVisibility(View.INVISIBLE);
                tv_total_money.setText("¥ " + 0);
                tv_count.setText(0 + "");

                if (popAdapter != null) {
                    popAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private boolean isMenuShow = false;
    /**
     * 添加商品详情pop window
     */
    private void showDetailPopwindow() {
        View view = View.inflate(this, R.layout.pop_add_group, null);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setContentView(view);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOutsideTouchable(true);
        //mPopupWindow.setAnimationStyle(R.style.Animation_Popup);
        ImageView iv_close = (ImageView) view.findViewById(R.id.pop_iv_close);          //关闭
        LinearLayout ll_delete = (LinearLayout) view.findViewById(R.id.pop_ll_delete);  //清空
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.relative);

        popListview = (ListView) view.findViewById(R.id.pop_listview);      //popWindow里面的listview

        popAdapter = new PopupGoodsAdapter(this, popListDatas);

        /**
         * 添加 选中商品的个数
         */
        popAdapter.setOnAddCountListeren(new PopupGoodsAdapter.AddCountListener() {
            @Override
            public void onAddCount(View view, int position) {
                GoodsInfo goodsInfo = popListDatas.get(position);
                int count = goodsInfo.getGoodsCount();
                count++;
                goodsInfo.setGoodsCount(count);

                //计算pop里面的总金额
                double price = 0;
                int tvCount = 0;
                for (GoodsInfo info : popListDatas) {
                    int goodsCount = info.getGoodsCount();
                    double standardPrice = info.getStandardPrice() * goodsCount;
                    price = price + standardPrice;
                    tv_total_money.setText("¥ " + String.valueOf(price));
                    tvCount = tvCount + goodsCount;
                }

                //显示选好的商品的数量,红色小图标和商品数量
                if (popListDatas.size() > 0) {
                    tv_count.setVisibility(View.VISIBLE);
                    tv_count.setText(tvCount + "");
                } else {
                    tv_count.setVisibility(View.INVISIBLE);
                }

                popAdapter.notifyDataSetChanged();
            }
        });

        /**
         * 减去 选中商品的个数
         */
        popAdapter.setOnMinusCountListener(new PopupGoodsAdapter.MinusCountListener() {
            @Override
            public void onMinusCount(View view, int position) {
                GoodsInfo goodsInfo = popListDatas.get(position);
                int count = goodsInfo.getGoodsCount();
                count--;
                if (count > 0) {
                    goodsInfo.setGoodsCount(count);
                } else {
                    popListDatas.remove(goodsInfo);     //小于1就移除该项
                }

                //计算pop里面的总金额
                double price = 0;
                int tvCount = 0;
                if (popListDatas.size() > 0) {
                    for (GoodsInfo info : popListDatas) {
                        int goodsCount = info.getGoodsCount();
                        double standardPrice = info.getStandardPrice() * goodsCount;
                        price = price + standardPrice;
                        tv_total_money.setText("¥ " + String.valueOf(price));
                        tvCount = tvCount + goodsCount;
                    }
                } else {
                    tv_total_money.setText("¥ " + 0);
                }

                //显示选好的商品的数量,红色小图标
                if (popListDatas.size() > 0) {
                    tv_count.setVisibility(View.VISIBLE);
                    tv_count.setText(tvCount + "");
                } else {
                    tv_count.setVisibility(View.INVISIBLE);
                }

                popAdapter.notifyDataSetChanged();
            }
        });

        popListview.setAdapter(popAdapter);
        iv_close.setOnClickListener(this);
        ll_delete.setOnClickListener(this);

        mPopupWindow.setFocusable(true);
        rl.setFocusableInTouchMode(true);
        rl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    if (!isMenuShow) {
                        mPopupWindow.dismiss();
                    }
                    isMenuShow = false;
                }
                return false;
            }
        });

        mPopupWindow.showAtLocation(rl_detail, Gravity.BOTTOM, 0, rl_detail.getHeight());
    }

    /**
     * 获取商品数据
     *
     * @param keyWord
     */
    private void getData(String keyWord) {
        VCParams params = new VCParams("goodsManagementApp/queryGoodsByNameAndSn");
        params.addBodyParameter("keyWord", keyWord);
        //params.addBodyParameter("currPage", page + "");
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("---AddGroupActivity:", result.toString());
                pb_progress.setVisibility(View.GONE);
                try {
                    Type type = new TypeToken<List<GoodsInfo>>() {
                    }.getType();
                    List<GoodsInfo> list = GsonUtils.createGson().fromJson(result.getString("dataList"), type);
                    if (list != null) {
                        listDatas.addAll(list);
                        initViews();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                pb_progress.setVisibility(View.GONE);
                TbLog.i("---AddGroupActivity失败信息:", s);
                Utils.showToast(EditGroupActivity.this, s);
            }
        });
    }

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyingComparator = new PinyingComparator();

        sidebar.setTextView(tv_dialog);

        //设置右侧触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listview.setSelection(position);
                }
            }
        });

        listDatas = filledData(listDatas);
        // 根据a-z进行排序源数据
        Collections.sort(listDatas, pinyingComparator);

        adapter = new AddGoodsAdapter();
        listview.setAdapter(adapter);
    }

    private class AddGoodsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listDatas.size() > 0 ? listDatas.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return listDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder = null;
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(EditGroupActivity.this, R.layout.item_goods_info, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //设置数据
            final GoodsInfo goodsInfo = listDatas.get(position);

            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);

            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                holder.tv_catalog.setVisibility(View.VISIBLE);
                holder.tv_catalog.setText(goodsInfo.getGoodsInitials());
            } else {
                holder.tv_catalog.setVisibility(View.GONE);
            }

            //组合商品不能再次添加,隐藏添加按钮
            if(goodsInfo.getGroupId()!=null) {
                holder.iv_addgroup.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_addgroup.setVisibility(View.VISIBLE);
            }

            holder.tv_name.setText(goodsInfo.getGoodsName());
            holder.tv_brand.setText("品牌: " + goodsInfo.getBrand());
            holder.tv_goodsDesc.setText("规格: " + goodsInfo.getGoodsDesc());
            holder.tv_price.setText("¥ " + goodsInfo.getStandardPrice());
            //holder.tv_price.setText("¥ " + goodsInfo.getStandardPrice());

            //加载 头像的url:　http://192.168.2.62/FileServer/file/preview/fid
            String url = goodsInfo.getGoodsPic();
            if (!StrUtil.isEmpty(url)) {
                Picasso.with(EditGroupActivity.this)
                        .load(url)
                        .placeholder(R.mipmap.ic_stub)          //默认图片
                        .error(R.mipmap.ic_error)                //加载错误后的图片
                        .into(holder.icon_pic);             //加载图片url
            } else {
                Picasso.with(EditGroupActivity.this)
                        .load("http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png")
                        .placeholder(R.mipmap.ic_stub)          //默认图片
                        .error(R.mipmap.ic_error)                //加载错误后的图片
                        .into(holder.icon_pic);
            }

            //点击添加商品事件
            holder.iv_addgroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 重复点击添加逻辑
                    if (null == popListDatas || popListDatas.size() == 0) {
                        goodsInfo.setGoodsCount(1);
                        popListDatas.add(goodsInfo);
                    } else {
                        boolean isok = false;
                        for (int i = 0; i < popListDatas.size(); i++) {
                            GoodsInfo goodsInfo1 = popListDatas.get(i);
                            //如果gid相同就 数量+1
                            if (goodsInfo1.getGid().equals(goodsInfo.getGid())) {
                                goodsInfo1.setGoodsCount(goodsInfo1.getGoodsCount() + 1);
                                isok = true;
                            }
                        }
                        //有数据的情况下,gid不相同,还是添加进去
                        if (!isok) {
                            goodsInfo.setGoodsCount(1);
                            popListDatas.add(goodsInfo);
                        }
                    }
                    //计算总金额
                    double price = 0;
                    int tvCount = 0;
                    for (GoodsInfo goodsInfo : popListDatas) {
                        //AbLogUtil.i(AddGroupDetailActivity.class,"----"+ goodsInfo.toString());
                        double standardPrice = goodsInfo.getStandardPrice();
                        int goodsCount = goodsInfo.getGoodsCount();
                        price = price + (standardPrice * goodsCount);
                        tv_total_money.setText("¥ " + String.valueOf(price));
                        tvCount = tvCount + goodsCount;
                        //AbLogUtil.i("价格:", standardPrice + "");
                        //AbLogUtil.i("价格:", price + "");
                    }
                    //显示选好的商品的数量,红色小图标
                    if (popListDatas.size() > 0) {
                        tv_count.setVisibility(View.VISIBLE);
                        tv_count.setText(tvCount + "");
                    } else {
                        tv_count.setVisibility(View.INVISIBLE);
                    }
                }
            });

            return convertView;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
            return listDatas.get(position).getGoodsInitials().charAt(0);
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public int getPositionForSection(int section) {
            for (int i = 0; i < listDatas.size(); i++) {
                String sortStr = listDatas.get(i).getGoodsInitials();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }
    }

    static class ViewHolder {
        @Bind(R.id.item_goodsinfo_catalog)
        TextView tv_catalog;                //标题栏A-Z
        @Bind(R.id.item_goodsinfo_icon)
        ImageView icon_pic;
        @Bind(R.id.item_goodsinfo_tv_name)
        TextView tv_name;
        @Bind(R.id.item_goodsinfo_tv_brand)
        TextView tv_brand;
        @Bind(R.id.item_goodsinfo_tv_standard)
        TextView tv_goodsDesc;
        @Bind(R.id.item_goodsinfo_tv_price)
        TextView tv_price;
        @Bind(R.id.item_goodsinfo_iv_addgroup)
        ImageView iv_addgroup;          //添加到组合

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private List<GoodsInfo> filledData(List<GoodsInfo> date) {
        List<GoodsInfo> mSortList = new ArrayList<GoodsInfo>();

        for (int i = 0; i < date.size(); i++) {
            GoodsInfo goodsInfo = date.get(i);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(goodsInfo.getGoodsName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                goodsInfo.setGoodsInitials(sortString.toUpperCase());
            } else {
                goodsInfo.setGoodsInitials("#");
            }
            mSortList.add(goodsInfo);
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<GoodsInfo> filterDateList = new ArrayList<GoodsInfo>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = listDatas;
        } else {
            filterDateList.clear();
            for (GoodsInfo goodsInfo : listDatas) {
                String name = goodsInfo.getGoodsName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(goodsInfo);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyingComparator);
        adapter.notifyDataSetChanged();
    }
}
