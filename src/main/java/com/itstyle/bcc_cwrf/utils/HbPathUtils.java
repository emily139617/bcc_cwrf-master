package com.itstyle.bcc_cwrf.utils;
/**
 * @Description: 气候回报数据的路径
 * @Author hy
 * @Date 2019-12-25 10:25
 * @Version 1.0
 */

public class HbPathUtils {

    /**
     * sw_BData路径
     * /home/export/base/YVDI/sw_BData/GPFS8p/GlobalData
     * sw_BData      uznvYTnU
     */

    /**
     * 当前气候回报常规气候预测产品生成模块数据路径
     */
    // 1.延伸期图片路径
    public static final String Hb_Normal_Ysq_Path = "/home/export/online1/cwrf_pf/qj/CWRF_bcc/zj/changgui/hb/ysqmean/picture/";
    // 2.月尺度图片路径
    public static final String Hb_Normal_Mon_Path = "/home/export/online1/cwrf_pf/qj/CWRF_bcc/zj/changgui/hb/monmean/picture/";
    // 3.季节尺度图片路径
    public static final String Hb_Normal_Seas_Path = "/home/export/online1/cwrf_pf/qj/CWRF_bcc/zj/changgui/hb/seasmean/picture/";


    /**
     * 前气候回报常规气候预测产品生成模块数据脚本路径前缀 具体使用时添加延伸期/月尺度/季尺度类别
     */
    public static final String Hb_Normal_Shell_Path = "/home/export/online1/bcc_cwrf/cwrf/shellpath/changgui/hb/";


    /**
     * 偏差的路径
     * 前气候回报常规气候预测产品生成模块数据脚本路径前缀 具体使用时添加延伸期/月尺度/季尺度类别
     */
    public static final String Hb_Normal_dev_Shell_Path = "/home/export/online1/bcc_cwrf/cwrf/shellpath/changgui/hb/deviation/";

    /**
     * 前气候回报常规气候预测产品生成图片路径前缀 具体使用时添加延伸期/月尺度/季尺度类别
     */
    public static final String Hb_Normal_Picture_Path = "/home/export/online1/bcc_cwrf/cwrf/picture/changgui/hb/";

    /**
     * 偏差的路径
     * 前气候回报常规气候预测产品生成图片路径前缀 具体使用时添加延伸期/月尺度/季尺度类别
     */
    public static final String Hb_Normal_dev_Picture_Path = "/home/export/online1/bcc_cwrf/cwrf/picture/changgui/hb/deviation/";

}
