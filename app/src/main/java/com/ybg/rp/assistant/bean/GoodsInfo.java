package com.ybg.rp.assistant.bean;

import java.io.Serializable;

/**
 * 商品信息
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.bean
 * @修改记录:
 *
 * @date 2016/2/23 0023
 */
public class GoodsInfo implements Serializable {

    private static final long serialVersionUID = -78945645678972345L;


    /**
     * standardPrice : 5.0
     * goodsPic :
     * gid : 1
     * brand : 益达
     * goodsNo : 12312
     * goodsDesc : 330ml
     * goodsName : 测试商品-益达
     */
    private String goodsInitials;  //排序序号
    private String groupId;         //组合商品标记，非空就是组合商品

    private double standardPrice;   //售价
    private String goodsPic;      //图片url
    private String gid;         //商品的唯一标识id
    private String brand;
    private String goodsNo;
    private String goodsDesc;       //规格
    private String goodsName;

    private int goodsCount;     //用于弹窗 增加删除的数量,额外添加的字段,服务返回没有这个

    public int getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(int goodsCount) {
        this.goodsCount = goodsCount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGoodsInitials() {
        return goodsInitials;
    }

    public void setGoodsInitials(String goodsInitials) {
        this.goodsInitials = goodsInitials;
    }

    public void setStandardPrice(double standardPrice) {
        this.standardPrice = standardPrice;
    }

    public void setGoodsPic(String goodsPic) {
        this.goodsPic = goodsPic;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public double getStandardPrice() {
        return standardPrice;
    }

    public String getGoodsPic() {
        return goodsPic;
    }

    public String getGid() {
        return gid;
    }

    public String getBrand() {
        return brand;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public String getGoodsName() {
        return goodsName;
    }

    @Override
    public String toString() {
        return "GoodsInfo{" +
                "goodsInitials='" + goodsInitials + '\'' +
                ", groupId='" + groupId + '\'' +
                ", standardPrice=" + standardPrice +
                ", goodsPic='" + goodsPic + '\'' +
                ", gid='" + gid + '\'' +
                ", brand='" + brand + '\'' +
                ", goodsNo='" + goodsNo + '\'' +
                ", goodsDesc='" + goodsDesc + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsCount=" + goodsCount +
                '}';
    }
}
