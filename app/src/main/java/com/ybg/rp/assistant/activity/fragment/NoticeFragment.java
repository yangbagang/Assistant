package com.ybg.rp.assistant.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.utils.Config;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 通知fragment
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.fragment
 * @修改记录:
 *
 * @date 2016/1/4 0004
 */
public class NoticeFragment extends Fragment {

    @Bind(R.id.notice_iv)
    ImageView mNoticeIv;

    @Bind(R.id.notice_list)
    ListView listView;

    private String[] images;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_notice, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        images = Config.IMAGES;
        //Picasso工具加载网络图片测试
        String url = "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png";

        //Picasso.with(getActivity()).load(url).into(mNoticeIv);  //普通加载

        Picasso.with(getActivity())
                .load(url)
                .placeholder(R.mipmap.ic_stub)     //默认图片
                .error(R.mipmap.ic_error)                //加载错误后的图片
                .into(mNoticeIv);
        //测试listView
        listView.setAdapter(new PictureAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class PictureAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_picture, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
                //
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //设置数据
            holder.mPictureText.setText("item"+(position+1));
            Picasso.with(getActivity())
                    .load(images[position])
                    .placeholder(R.mipmap.ic_stub)          //默认图片
                    .error(R.mipmap.ic_error)                //加载错误后的图片
                    .into(holder.mPictureImage);

            return convertView;
        }

    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_picture.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.picture_image)
        ImageView mPictureImage;
        @Bind(R.id.picture_text)
        TextView mPictureText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
