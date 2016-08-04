package com.ybg.rp.assistant.activity.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;

/**
 * Fragment管理
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.fragment
 * @修改记录:
 *
 * @date 2016/1/4 0004
 */
public class FragmentFactory {

    private static SparseArrayCompat<Fragment> mCaches = new SparseArrayCompat<Fragment>();

    public static Fragment getFragment(int position)
    {
        Fragment fragment = mCaches.get(position);

        if (fragment != null) { return fragment; }

        switch (position)
        {
            case 0:
                //消息中心
                fragment = new MessageFragment();
                break;
            case 1:
                //站内公告
                fragment = new NoticeFragment();
                break;

        }
        // 存储
        mCaches.put(position, fragment);

        return fragment;
    }
}
