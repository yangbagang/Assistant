package com.ybg.rp.assistant.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.bean.GoodsSmallCategory;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 小分类数据
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.adapter
 * @修改记录:
 *
 * @date 2016/2/24 0024
 */
public class SmallCategoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<GoodsSmallCategory> datas;

    public SmallCategoryAdapter(Context context, List<GoodsSmallCategory> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if (datas.size() > 0) {
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_goods_smallcategory, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GoodsSmallCategory goodsSmallCategory = datas.get(position);
        holder.mItemSmallcategoryName.setText(goodsSmallCategory.getCategory());

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.item_smallcategory_name)
        TextView mItemSmallcategoryName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
