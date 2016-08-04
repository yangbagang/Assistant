package com.ybg.rp.assistant.app;

import android.content.Context;

import com.ybg.rp.assistant.R;
import com.ybg.rp.assistant.utils.AppPreferences;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;


/**
 * 包            名:      com.ybg.rp.assistant.app
 * 类            名:      SCGApplipation
 * 修 改 记 录:     // 修改历史记录，包括修改日期、修改者及修改内容
 * 版 权 所 有:     版权所有(C)2010-2015
 * 公            司:
 *
 * @author ybg
 * @version V1.0
 * @date 2015/8/18
 */
public class VCApplipcation extends TbApplication {

    private AppPreferences preferences = AppPreferences.getInstance();

    private Context mContext;
    private String token;       //token
    private String operatorId;    //操作员ID
    private static VCApplipcation vcApplipcation;

    private double longitude = 0;
    private double latitude = 0;

    public static VCApplipcation getInstance() {
        if (null == vcApplipcation)
            vcApplipcation = new VCApplipcation();
        return vcApplipcation;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        vcApplipcation = this;
        mContext = getApplicationContext();

        //ThemeConfig theme2 = ThemeConfig.CYAN;

        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(getResources().getColor(R.color.colorPrimaryDark))
                .build();

        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(false)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(false)
                //.setCropSquare(true)        //方形裁剪
                .setEnablePreview(false)
                //.setForceCrop(true)     //强制裁剪
                .build();
        CoreConfig coreConfig = new CoreConfig.Builder(this, new PicassoImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(new PicassoPauseOnScrollListener(false, true))
                .build();
        GalleryFinal.init(coreConfig);

    }

    /**
     * 登录返回的Token
     */
    public void setToken(String token, boolean isSave) {
        if (isSave) {
            try {
                preferences.setString("TOKEN", token);
                preferences.setBoolean("KEY_IS_LOGIN", true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.token = token;
    }

    /**
     * 返回登录的Token
     */
    public String getToken() {
        if (token != null) {
            return token;
        } else {
            return preferences.getString("TOKEN", null);
        }
    }

    /**
     * 保存和设置操作员ID
     *
     * @param operatorId
     * @param isSave
     */
    public void setOperatorId(String operatorId, boolean isSave) {
        if (isSave) {
            preferences.setString("OPERATOR_ID", operatorId);
        }
        this.operatorId = operatorId;
    }

    public String getOperatorId() {
        if (operatorId != null) {
            return operatorId;
        } else {
            return preferences.getString("OPERATOR_ID", null);
        }
    }

    /**
     * 退出登录,清空参数
     */
    public void clearLoginParams() {
        preferences.setString("TOKEN", null);
        preferences.setString("OPERATOR_ID", "");
        preferences.setString("username", "");
        preferences.setString("realName", "");
        preferences.setString("email", "");
        preferences.setString("avatar", "");
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
