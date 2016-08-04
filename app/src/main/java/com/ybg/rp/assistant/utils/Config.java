package com.ybg.rp.assistant.utils;


/**
 * 包            名:      com.ybg.rp.assistant.util
 * 类            名:      Config
 * 修 改 记 录:     // 修改历史记录，包括修改日期、修改者及修改内容
 * 版 权 所 有:     版权所有(C)2010-2015
 * 公            司:
 *
 * @author ybg
 * @version V1.0
 * @date 2015/8/18
 */
public interface Config {

    //String URL_COMM = "http://183.57.41.230/Ph5Platform/";           //正式环境
    String URL_COMM = "http://192.168.12.101:8080/";
    String URL_FID = "http://183.57.41.230/FileServer/";           //上传预览下载头像地址

    String PATH = SimpleUtil.getSDCardPath() + "/vendingHead/";  //头像图片sd卡路径
    String GOODS_PIC ="goodsPic.png";

    String WEB_CACAHE_DIRNAME = "/webcache";       //webView缓存目录

    //请求超时
    int TIME_OUT = 1000 * 20;

    //成功
    String SUCCESS = "success";
    //失败
    String ERROR = "msg";
    String TRUE = "true";

    String PREFERENCE_FILE_NAME = "assistant_preference";

    /**
     * 短信发送倒计时 单位
     */
    long MSGINTERVAL = 1000;
    /**
     * 短信发送倒计时 时间
     */
    long MSGCOUNTDOWN = 60 * MSGINTERVAL;

    //网络图片地址
    String[] IMAGES = new String[]{
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/21/110000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/044000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/21/110000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/052000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/29/080000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/094000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/081000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/081000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/094000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/29/102000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/21/110000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/044000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/21/110000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/052000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/29/080000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/094000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/081000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/081000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/094000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/29/102000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/095000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/21/110000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2016/01/04/044000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/21/110000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/24/052000_0_0.png",
            "http://res.cloudinary.com/liuyuesha/image/fetch/http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/2015/12/29/080000_0_0.png"
    };
}
