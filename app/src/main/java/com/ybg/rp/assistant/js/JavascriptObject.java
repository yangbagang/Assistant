package com.ybg.rp.assistant.js;

import android.webkit.JavascriptInterface;

import com.ybg.rp.assistant.app.VCApplipcation;
import com.ybg.rp.assistant.utils.Config;

/**
 * Created by yangbagang on 16/7/17.
 */
public class JavascriptObject {

    @JavascriptInterface
    public String getToken() {
        return VCApplipcation.getInstance().getToken();
    }

    @JavascriptInterface
    public String getOperatorId() {
        return VCApplipcation.getInstance().getOperatorId();
    }

    @JavascriptInterface
    public String getServerAddress() {
        return Config.URL_COMM;
    }

    @JavascriptInterface
    public double getLongitude() {
        return VCApplipcation.getInstance().getLongitude();
    }

    @JavascriptInterface
    public double getLatitude() {
        return VCApplipcation.getInstance().getLatitude();
    }

}
