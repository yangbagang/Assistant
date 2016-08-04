package com.ybg.rp.assistant.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ybg.rp.assistant.R;


/**
 * 平台页面
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.activity.fragment
 * @修改记录:
 *
 * @date 2015/11/26 0026
 */
public class PlatFormFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_platform, null);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
