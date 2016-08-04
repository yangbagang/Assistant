package com.ybg.rp.assistant.side;


import com.ybg.rp.assistant.bean.GoodsInfo;

import java.util.Comparator;

/**
 * @author ybg
 * @包名: com.ybg.rp.assistant.side
 * @修改记录:
 *
 * @date 2016/3/7 0007
 */
public class PinyingComparator implements Comparator<GoodsInfo> {

    public int compare(GoodsInfo o1, GoodsInfo o2) {
        if (o1.getGoodsInitials().equals("@")
                || o2.getGoodsInitials().equals("#")) {
            return -1;
        } else if (o1.getGoodsInitials().equals("#")
                || o2.getGoodsInitials().equals("@")) {
            return 1;
        } else {
            return o1.getGoodsInitials().compareTo(o2.getGoodsInitials());
        }
    }

}
