package com.ybg.rp.assistant.activity.erp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.TbActivity;
import com.ybg.rp.assistant.adapter.GoodsAdapter;
import com.ybg.rp.assistant.bean.GoodsInfo;
import com.ybg.rp.assistant.net.NetworkXUtils;
import com.ybg.rp.assistant.net.VCParams;
import com.ybg.rp.assistant.side.CharacterParser;
import com.ybg.rp.assistant.side.ClearEditText;
import com.ybg.rp.assistant.side.PinyingComparator;
import com.ybg.rp.assistant.side.SideBar;
import com.ybg.rp.assistant.utils.GsonUtils;
import com.ybg.rp.assistant.utils.TbLog;
import com.ybg.rp.assistant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 商品管理
 * 查询商户自己的商品
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.erp
 * @修改记录:
 *
 * @date 2016/3/8 0008
 */
public class ShangpinManageActivity extends TbActivity implements View.OnClickListener {

    public static final String SIA_KEY = "sia_key";


    @Bind(R.id.shangpin_iv_left)
    ImageView iv_left;
    @Bind(R.id.shangpin_iv_shaixuan)
    ImageView iv_shaixuan;
    @Bind(R.id.shangpin_iv_add_goods)
    ImageView iv_add_goods;

    @Bind(R.id.shangpin_dialog_flag)
    TextView dialog_flag;

    @Bind(R.id.shangpin_search)
    ClearEditText search;

    @Bind(R.id.shangpin_iv_scan)
    ImageView iv_scan;

    @Bind(R.id.shangpin_listview)
    ListView listview;

    @Bind(R.id.shangpin_tv_dialog)
    TextView tv_dialog;

    @Bind(R.id.shangpin_sidebar)
    SideBar sidebar;

    @Bind(R.id.shangpin_pb_progress)
    ProgressBar pb_progress;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyingComparator pinyingComparator;
    private PopupWindow mPopWindow;

    private List<GoodsInfo> listDatas;
    private NetworkXUtils xUtils;
    private GoodsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shangpin_manage);
        ButterKnife.bind(this);

        //pb_progress.setVisibility(View.GONE);
        xUtils = NetworkXUtils.getInstance(this);
        listDatas = new ArrayList<GoodsInfo>();

        iv_left.setOnClickListener(this);
        iv_add_goods.setOnClickListener(this);
        iv_scan.setOnClickListener(this);

        //getData("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        pb_progress.setVisibility(View.VISIBLE);
        listDatas.clear();
        getData("");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shangpin_iv_left:
                finish();
                break;

            case R.id.shangpin_iv_add_goods:
                isMenuShow = true;
                showPopwindow();
                break;

            //扫码
            case R.id.shangpin_iv_scan:
                //startActivity(new Intent(this, CaptureActivity.class));
                break;
        }
    }

    private boolean isMenuShow = false;

    //弹窗选择新增商品
    private void showPopwindow() {
        View contenView = View.inflate(this, R.layout.popwindow_select_add, null);
        mPopWindow = new PopupWindow(contenView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);

        LinearLayout ll_add = (LinearLayout) contenView.findViewById(R.id.pop_ll_add_goods);
        LinearLayout ll_group = (LinearLayout) contenView.findViewById(R.id.pop_ll_add_group_goods);
        LinearLayout ll = (LinearLayout) contenView.findViewById(R.id.line);
        ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                startActivity(new Intent(ShangpinManageActivity.this, AddCommodityActivity.class));
            }
        });
        ll_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                startActivity(new Intent(ShangpinManageActivity.this, AddGroupActivity.class));
            }
        });
        mPopWindow.setContentView(contenView);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());

        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        ll.setFocusableInTouchMode(true);
        ll.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    if (!isMenuShow) {
                        mPopWindow.dismiss();
                    }
                    isMenuShow = false;
                }
                return false;
            }
        });

        mPopWindow.showAsDropDown(dialog_flag);
    }

    private void getData(String keyWord) {
        VCParams params = new VCParams("goodsManagementApp/queryGoodsByNameAndSn");
        params.addBodyParameter("keyWord", keyWord);
        //params.addBodyParameter("currPage", page + "");
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("---ShangpinActivity:", result.toString());
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
            public void onFailure(HttpException ex, String msg) {
                pb_progress.setVisibility(View.GONE);
                TbLog.i("---ShangpinActivity/失败信息:", msg);
                Utils.showToast(ShangpinManageActivity.this, msg);
            }
        });
    }

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyingComparator = new PinyingComparator();

        sidebar.setTextView(tv_dialog);

        //点击item进入编辑页面
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = listDatas.get(position);
                if (goodsInfo.getGroupId() != null) {
                    return;
                } else {
                    Intent intent = new Intent(ShangpinManageActivity.this, EditCommodityDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SIA_KEY, goodsInfo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        /**
         * item长按事件
         */
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = listDatas.get(position);
                TbLog.i("ShangpinManage/:"+goodsInfo.getGoodsPic());

                showEditDialog(goodsInfo);

                return true;
            }
        });

        listDatas = filledData(listDatas);
        // 根据a-z进行排序源数据
        Collections.sort(listDatas, pinyingComparator);

        adapter = new GoodsAdapter(this, listDatas);
        listview.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置右侧触摸监听
                sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

                    @Override
                    public void onTouchingLetterChanged(String s) {
                        //该字母首次出现的位置
                        if (adapter != null) {
                            int position = adapter.getPositionForSection(s.charAt(0));
                            if (position != -1) {
                                listview.setSelection(position);
                            }
                        }
                    }
                });

                //搜索栏监听
                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                        filterData(s.toString());
                    }
                });
            }
        }).start();

    }

    /**
     * listView长按弹窗  "编辑\删除"
     */
    private void showEditDialog(final GoodsInfo goodsInfo) {
        View view = View.inflate(this, R.layout.dialog_shangpin_edit, null);
        final Dialog dialog = new Dialog(this, R.style.upload_dialog);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout ll_edit = (LinearLayout) view.findViewById(R.id.dialog_shangpin_edit);
        LinearLayout ll_delete = (LinearLayout) view.findViewById(R.id.dialog_shangpin_delete);

        /**编辑*/
        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //传递数据到编辑页面
                if (goodsInfo.getGroupId() != null) {      //GroupId非空为组合商品,进入编辑组合商品
                    Intent intent = new Intent(ShangpinManageActivity.this, EditGroupDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SIA_KEY, goodsInfo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    dialog.dismiss();
                } else {
                    Intent intent = new Intent(ShangpinManageActivity.this, EditCommodityDetailActivity.class); //jin
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SIA_KEY, goodsInfo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        /**删除*/
        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSureDialog(goodsInfo);
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        Display d = getWindowManager().getDefaultDisplay();

        wl.width = (int) (d.getWidth() * 0.60);   //宽设为屏幕的60%
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 确认删除对话框
     */
    private void showSureDialog(final GoodsInfo goodsInfo) {
        View view = View.inflate(this, R.layout.dialog_shangpin_sure, null);
        final Dialog dialog = new Dialog(this, R.style.upload_dialog);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tv_sure = (TextView) view.findViewById(R.id.dialog_shangpin_tv_sure);
        TextView tv_cancel = (TextView) view.findViewById(R.id.dialog_shangpin_tv_cancel);
        TextView tv_goodsName = (TextView) view.findViewById(R.id.dialog_shangpin_tv_goodsName);

        //GroupId不为空就是组合商品
        if (goodsInfo.getGroupId() != null) {
            tv_goodsName.setText("“" + goodsInfo.getGoodsName() + "“" + "商品组合？");
        }

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /**删除商品*/
                deleteGoods(goodsInfo);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        Display d = getWindowManager().getDefaultDisplay();

        wl.width = (int) (d.getWidth() * 0.70);   //宽设为屏幕的70%
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 删除商品
     */
    private void deleteGoods(final GoodsInfo goodsInfo) {
        String gid = goodsInfo.getGid();
        VCParams params = new VCParams("goodsManagementApp/deleteGoodsInfo");
        params.addBodyParameter("gid", gid);
        xUtils.post(params, new NetworkXUtils.NetCallback() {
            @Override
            public void onSuccess(JSONObject result) throws InterruptedException {
                TbLog.i("---ShangpinManage/deleteGoodsInfo:", result.toString());
                Utils.showToast(ShangpinManageActivity.this, "删除成功");
                listDatas.remove(goodsInfo);    //删除数据
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException ex, String s) {
                TbLog.i("---ShangpinManage/deleteGoodsInfo/失败信息:", s);
                if (s.contains("不能删除")) {
                    showCannotDeleteDialog();
                } else {
                    Utils.showToast(ShangpinManageActivity.this, s);
                }
            }
        });

    }

    /**
     * 服务查询获取状态-->不可删除对话框
     */
    private void showCannotDeleteDialog() {
        View view = View.inflate(this, R.layout.dialog_shangpin_cannot_delete, null);
        final Dialog dialog = new Dialog(this, R.style.upload_dialog);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tv_ok = (TextView) view.findViewById(R.id.dialog_shangpin_tv_ok);

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        Display d = getWindowManager().getDefaultDisplay();

        wl.width = (int) (d.getWidth() * 0.70);   //宽设为屏幕的70%
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
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

        //搜索改变数据后的listView监听
        final List<GoodsInfo> finalFilterDateList = filterDateList;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = finalFilterDateList.get(position);
                if (goodsInfo.getGroupId() != null) {
                    return;
                } else {
                    Intent intent = new Intent(ShangpinManageActivity.this, EditCommodityDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SIA_KEY, goodsInfo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        /**
         * item长按事件
         */
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = finalFilterDateList.get(position);

                showEditDialog(goodsInfo);

                return true;
            }
        });

        adapter.updateListView(filterDateList);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPopWindow != null) {
            mPopWindow.dismiss();
            mPopWindow = null;
        }
    }
}
