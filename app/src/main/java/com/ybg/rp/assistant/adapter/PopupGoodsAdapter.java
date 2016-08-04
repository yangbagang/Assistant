package com.ybg.rp.assistant.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.bean.GoodsInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.adapter
 * @修改记录:
 *
 * @date 2016/3/10 0010
 */
public class PopupGoodsAdapter extends BaseAdapter {

    private Context mContext;
    private List<GoodsInfo> datas;

    private AddCountListener mAddCountListener;
    private MinusCountListener mMinusCountListener;

    public void setOnAddCountListeren(AddCountListener listener) {
        this.mAddCountListener = listener;
    }

    public void setOnMinusCountListener(MinusCountListener listener) {
        this.mMinusCountListener = listener;
    }

    public PopupGoodsAdapter(Context context, List<GoodsInfo> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size() > 0 ? datas.size() : 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        PopViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_pop_add_group, null);
            holder = new PopViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (PopViewHolder) convertView.getTag();
        }


        //设置数据
        GoodsInfo goodsInfo = datas.get(position);

        holder.tv_name.setText(goodsInfo.getGoodsName());
        holder.tv_price.setText(goodsInfo.getStandardPrice() + "");
        holder.tv_goods_count.setText(goodsInfo.getGoodsCount() + "");

        //对外暴露 添加 点击事件
        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddCountListener.onAddCount(v, position);
            }
        });

        //对外暴露 减去 点击事件
        holder.iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMinusCountListener.onMinusCount(v, position);
            }
        });

        return convertView;
    }

    static class PopViewHolder {
        @Bind(R.id.itempop_tv_name)
        TextView tv_name;
        @Bind(R.id.itempop_tv_price)
        TextView tv_price;
        @Bind(R.id.itempop_iv_minus)
        ImageView iv_minus;
        @Bind(R.id.itempop_tv_goods_count)
        TextView tv_goods_count;    //商品数量
        @Bind(R.id.itempop_iv_add)
        ImageView iv_add;

        PopViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface AddCountListener {
        void onAddCount(View view, int position);
    }

    public interface MinusCountListener {
        void onMinusCount(View view, int position);
    }
}
