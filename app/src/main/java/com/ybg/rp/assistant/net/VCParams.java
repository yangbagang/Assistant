package com.ybg.rp.assistant.net;

import com.ybg.rp.assistant.utils.Config;
import com.ybg.rp.assistant.utils.TbLog;

import org.xutils.http.RequestParams;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.net
 * @修改记录:
 *
 * @date 2016/3/21 0021
 */
public class VCParams extends RequestParams {

    public VCParams(String url) {
        super(Config.URL_COMM + url);
        TbLog.i(Config.URL_COMM + url);
    }
}
