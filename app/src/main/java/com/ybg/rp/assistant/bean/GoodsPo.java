package com.ybg.rp.assistant.bean;

/**
 * 组合商品中,单个商品的信息
 *
 * @author ybg
 * @包名: com.ybg.rp.assistant.bean
 * @修改记录:
 *
 * @date 2016/3/9 0009
 */
public class GoodsPo {


    /**
     * gid : 56
     * price : 2.0
     * goodsNum : 1
     * goodsPic : null
     * goodsName : 橙子
     */

    private String gid;
    private double price;
    private String goodsNum;
    private String goodsPic;
    private String goodsName;

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setGoodsNum(String goodsNum) {
        this.goodsNum = goodsNum;
    }

    public void setGoodsPic(String goodsPic) {
        this.goodsPic = goodsPic;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGid() {
        return gid;
    }

    public double getPrice() {
        return price;
    }

    public String getGoodsNum() {
        return goodsNum;
    }

    public Object getGoodsPic() {
        return goodsPic;
    }

    public String getGoodsName() {
        return goodsName;
    }

    @Override
    public String toString() {
        return "GoodsPo{" +
                "gid='" + gid + '\'' +
                ", price=" + price +
                ", goodsNum='" + goodsNum + '\'' +
                ", goodsPic='" + goodsPic + '\'' +
                ", goodsName='" + goodsName + '\'' +
                '}';
    }
}
