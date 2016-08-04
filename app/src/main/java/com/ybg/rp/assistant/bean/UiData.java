package com.ybg.rp.assistant.bean;

import java.io.Serializable;

/**
 * 界面显示 项目
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.bean
 * @修改记录:
 *
 * @date 2016/3/25 0025
 */
public class UiData implements Serializable {

    private static final long serialVersionUID = -78945962678972345L;

    /**
     * authority : 1       权限: 0不可见,1可见
     * moudule : myInfo
     * menuName : 我的
     */

    private int authority;
    private String moudule;
    private String menuName;

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public void setMoudule(String moudule) {
        this.moudule = moudule;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getAuthority() {
        return authority;
    }

    public String getMoudule() {
        return moudule;
    }

    public String getMenuName() {
        return menuName;
    }

    @Override
    public String toString() {
        return "UiData{" +
                "authority=" + authority +
                ", moudule='" + moudule + '\'' +
                ", menuName='" + menuName + '\'' +
                '}';
    }
}
