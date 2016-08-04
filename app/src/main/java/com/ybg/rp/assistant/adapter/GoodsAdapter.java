package com.ybg.rp.assistant.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.bean.GoodsInfo;
import com.ybg.rp.assistant.utils.StrUtil;
import com.ybg.rp.assistant.view.CustomImgeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.adapter
 * @修改记录:
 *
 * @date 2016/2/23 0023
 */
public class GoodsAdapter extends BaseAdapter {

    private Context mContext;
    private List<GoodsInfo> goodsDatas;
    private int systemFlag;

    //设置systemFlag标记,==1的话就是查询系统的商品信息,不显示出price
    public GoodsAdapter(Context context, List<GoodsInfo> goodsDatas, int systemFlag) {
        this.mContext = context;
        this.goodsDatas = goodsDatas;
        this.systemFlag = systemFlag;
    }

    public GoodsAdapter(Context context, List<GoodsInfo> goodsDatas) {
        this.mContext = context;
        this.goodsDatas = goodsDatas;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param goodsDatas
     */
    public void updateListView(List<GoodsInfo> goodsDatas) {
        this.goodsDatas = goodsDatas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (goodsDatas.size() > 0) {
            return goodsDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return goodsDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_goods_info, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置数据
        GoodsInfo goodsInfo = goodsDatas.get(position);
        if (systemFlag == 1) {
            holder.tv_price.setVisibility(View.INVISIBLE);  //设置systemFlag标记,==1的话就是查询系统的商品信息,不显示出price
        } else {
            holder.tv_price.setVisibility(View.VISIBLE);
        }

        if (systemFlag == 10) {                              //设置systemFlag标记,==10,显示出添加的图标,other不显示,用于添加组合商品
            holder.iv_addgroup.setVisibility(View.VISIBLE);
        } else {
            holder.iv_addgroup.setVisibility(View.GONE);
        }

        holder.tv_name.setText(goodsInfo.getGoodsName());
        if(!StrUtil.isEmpty(goodsInfo.getBrand())) {
            holder.tv_brand.setText("品牌: " + goodsInfo.getBrand());
        }
        if(!StrUtil.isEmpty(goodsInfo.getGoodsDesc())) {
            holder.tv_goodsDesc.setText("规格: " + goodsInfo.getGoodsDesc());
        }

        holder.tv_price.setText("¥ " + goodsInfo.getStandardPrice());

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tv_catalog.setVisibility(View.VISIBLE);
            holder.tv_catalog.setText(goodsInfo.getGoodsInitials());
        } else {
            holder.tv_catalog.setVisibility(View.GONE);
        }

        //加载 头像的url:　http://192.168.2.62/FileServer/file/preview/fid
        String url = goodsInfo.getGoodsPic();
        if (!StrUtil.isEmpty(url)) {
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.icon_default_pic)          //默认图片
                    .error(R.mipmap.ic_error)                //加载错误后的图片
                    .into(holder.icon_pic);             //加载图片url
        } else {
            Picasso.with(mContext)
                    //.load("http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/032000_0_0.png")
                    .load(R.mipmap.icon_default_pic)
                    .placeholder(R.mipmap.icon_default_pic)          //默认图片
                    .error(R.mipmap.ic_error)                //加载错误后的图片
                    .into(holder.icon_pic);
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.item_goodsinfo_icon)
        CustomImgeView icon_pic;
        @Bind(R.id.item_goodsinfo_tv_name)
        TextView tv_name;
        @Bind(R.id.item_goodsinfo_tv_brand)
        TextView tv_brand;
        @Bind(R.id.item_goodsinfo_tv_standard)
        TextView tv_goodsDesc;      //规格
        @Bind(R.id.item_goodsinfo_tv_price)
        TextView tv_price;
        @Bind(R.id.item_goodsinfo_catalog)
        TextView tv_catalog;        //标题栏A-Z
        @Bind(R.id.item_goodsinfo_iv_addgroup)
        ImageView iv_addgroup;      //添加到组合

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return goodsDatas.get(position).getGoodsInitials().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = goodsDatas.get(i).getGoodsInitials();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

}
