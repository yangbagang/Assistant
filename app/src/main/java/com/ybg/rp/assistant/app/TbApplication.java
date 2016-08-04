package com.ybg.rp.assistant.app;

import android.app.Application;
import android.content.Context;

import com.ybg.rp.assistant.utils.ImageLoadUtil;

import org.xutils.x;

/**
 * 包            名:      com.ybg.rp.assistant.app
 * 类            名:      TbApplication
 * 修 改 记 录:     // 修改历史记录，包括修改日期、修改者及修改内容
 * 版 权 所 有:     版权所有(C)2010-2015
 * 公             司:
 *
 * @author ybg
 * @version V1.0
 * @date 2016/1/4
 */
public class TbApplication extends Application {

    public Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        ImageLoadUtil.initImageLoader(mContext);
        x.Ext.init(this);
//        x.Ext.setDebug(true); // 是否输出debug日志
    }


}
