package com.ybg.rp.assistant.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.utils.TbLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 消息fragment
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.fragment
 * @修改记录:
 *
 * @date 2016/1/4 0004
 */
public class MessageFragment extends Fragment {

    @Bind(R.id.page_listview)
    ListView listView;

    @Bind(R.id.page_swipe)
    SwipeRefreshLayout swipeRefresh;

    private List<String> datas;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_message, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
    }

    private void initData() {

        //模拟数据
        datas = new ArrayList<String>();
        for (int i = 1; i < 5; ++i) {
            String name = "收到的消息" + i;
            datas.add(name);
        }

        final MessageAdapter messageAdapter = new MessageAdapter();
        listView.setAdapter(messageAdapter);

        swipeRefresh.setColorSchemeResources(R.color.red, R.color.gray, R.color.top_green, R.color.yellow);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 2; ++i) {
                            String content = "刷新的消息" + i;
                            datas.add(content);
                            TbLog.i("刷新消息", "" + i);
                        }
                        messageAdapter.notifyDataSetChanged();
                        if(swipeRefresh!=null) {
                            swipeRefresh.setRefreshing(false);
                        }
                    }
                }, 2300);
            }
        });
    }

    private class MessageAdapter extends BaseAdapter {

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
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_message, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
                //找控件
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //设置数据
            holder.mMessageTvContent.setText(datas.get(position));

            return convertView;
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_message.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.message_iv_flag)
        ImageView mMessageIvFlag;
        @Bind(R.id.message_tv_time)
        TextView mMessageTvTime;
        @Bind(R.id.message_tv_content)
        TextView mMessageTvContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        swipeRefresh.setRefreshing(false);
        ButterKnife.unbind(this);
    }
}
