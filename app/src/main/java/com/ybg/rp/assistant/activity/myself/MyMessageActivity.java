package com.ybg.rp.assistant.activity.myself;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.activity.fragment.FragmentFactory;
import com.ybg.rp.assistant.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的消息 /站内消息
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.myself
 * @修改记录:
 *
 * @date 2015/12/18 0018
 */
public class MyMessageActivity extends BaseActivity {

    @Bind(R.id.message_viewpager)
    ViewPager mViewpager;

    @Bind(R.id.message_tabs)
    PagerSlidingTabStrip mTabs;     //viewPager的标签


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);

        initView();

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(pagerAdapter);
        mTabs.setIndicatorHeight(10);            //指示器的高度
        mTabs.setIndicatorColor(getResources().getColor(R.color.green));     //指示器的颜色
        mTabs.setUnderlineHeight(0);         //标签的下划线  不设置
        mTabs.setShouldExpand(true);         //每个标签均分
        mTabs.setTextSize(30);
        mTabs.setViewPager(mViewpager);

        mViewpager.setPageTransformer(true,new ZoomOutPageTransformer());
    }

    /**
     * 标题栏初始化
     */
    private void initView() {
        LinearLayout ll_view_head = (LinearLayout) findViewById(R.id.base_title);
        setTitle(ll_view_head, "站内消息", null, null);
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] titles = {"消息中心", "站内公告"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.getFragment(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer
    {
        private static final float	MIN_SCALE	= 0.85f;
        private static final float	MIN_ALPHA	= 0.5f;

        public void transformPage(View view, float position)
        {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1)
            { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            }
            else if (position <= 1)
            { // [-1,1]
                // Modify the default slide transition to shrink the page as
                // well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0)
                {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                }
                else
                {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            }
            else
            { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
