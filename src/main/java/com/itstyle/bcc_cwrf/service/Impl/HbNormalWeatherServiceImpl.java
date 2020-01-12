package com.itstyle.bcc_cwrf.service.Impl;

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.service.HbNormalWeatherService;
import com.itstyle.bcc_cwrf.utils.HbPathUtils;
import com.itstyle.bcc_cwrf.utils.ParseShellUtils;
import com.itstyle.bcc_cwrf.utils.SplitYearUtils;
import com.itstyle.bcc_cwrf.web.HbNormalWeatherController;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author hy
 * @Description //气候回报数据 常规气候预测产品生成模块
 * @Date
 * @Param
 * @return }
 */
@Service
public class HbNormalWeatherServiceImpl implements HbNormalWeatherService {

    private final static Logger log = LoggerFactory.getLogger(HbNormalWeatherController.class);

    /**
     * 常规气候模块要素
     */
    // 气温要素类型
    private static final String[] weatherVarName = {"AT2M", "AT2M", "T2MAX", "T2MAX", "T2MIN", "T2MIN"};
    // 降水要素类型
    private static final String[] pravgVarName = {"PRAVG", "PRAVG"};
    // 10米风要素类型
    private static final String[] wsavgVarName = {"", "", "", "", "WSAVG", "WSAVG"};
    // 2米比湿要素类型
    private static final String[] otherVarName = {"", ""};
    // 月份数字转英文
    private static final String[] monthEngName = {"Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept"};
    // 季度数字转英文
    private static final String[] seasEngName = {"MAM", "JJA"};
    // 延伸期，月尺度，季尺度数组
    private static final String[] meanTypeArray = {"ysqmean", "monmean", "seasmean"};

    /**
     * obsDataPath\ modelDataPath 路径  使用时需要添加相关信息 区分延伸期，月尺度，季尺度
     */
    private static final String obs_base_path = "/GFPS8p/sw_BData/bcccsmProduct/obs/hb/";
    private static final String model_base_path = "/GFPS8p/sw_BData/bcccsmProduct/model/hb/";
    /**
     * 气温出图 第一张提交绘图
     * @param map
     * @return
     *
     * json参数
     *
     * {
     *     "typeName": 0,
     *     "meanType": 2,
     *     "mmDd": "0302",
     *     "hour": "00",
     *     "caseNum": "01",
     *     "obsDataName": "cn_obs",
     *     "month": "03",
     *     "seas": "0",
     *     "varName": "0",
     *     "year": "1993"
     * }
     */
    @Override
    public String weatherFirstPicture(Map<String, Object> map) {
        String resultPath = null;
        List<String> resultList = new ArrayList<String>();
        //类型  气温-0/降水-1/10米风-2/2米比湿-3
        String typeName = map.get("typeName") + "";
        //类别  延伸期-0/月尺度-1/季节尺度-2
        String meanType = map.get("meanType") + "";
        //起报month+day
        String monthAndDay = map.get("mmDd") + "";
        //起报时次 hour
        String hour = map.get("hour") + "";
        //case
        String caseNum = map.get("caseNum") + "";
        //观测资料 cfsr/eri/cn_obs站点
        String obsDataName = map.get("obsDataName") + "";
        //月尺度对应月份
        String month = map.get("month") + "";
        //季尺度季度
        String seas = map.get("seas") + "";
        //要素 分3种情况 需要根据类别 延伸期-0/月尺度-1/季节尺度-2区分
        //气温：平均气温-0/平均气温偏差-1/平均日最高气温-2/平均日最高气温偏差-3/平均日最低气温-4/平均日最低气温偏差-5
        //降水：平均降水-0/平均降水偏差-1
        //10米风： 纬向风速-0/经向风速-1/平均10米风速-2/纬向风速偏差-3/经向风速偏差-4/平均10米风速偏差-5/
        //2米比湿： 2米比湿-0/2米比湿偏差-1
        //平均气温对应的是AT2M，日最高气温对应的是T2MAX。日最低气温对应的是T2MIN，降水就是PRAVG，风速就是WSAVG
        String varName = map.get("varName") + "";
        String year = map.get("year") + "";
        //obsDataName名称转换
        if ("cn_obs".equals(obsDataName)){
            obsDataName = "CN05";
        }else if("cfsr".equals(obsDataName)){
            obsDataName = "";
        }else if("eri".equals(obsDataName)){
            obsDataName = "";
        }
        //具体路径拼接  需要区分  1.气温 降水 10米风 2米比湿    2.延伸期 月尺度 季节尺度
        //varName 是基数的话 代表偏差
        int varNameIndex = Integer.valueOf(varName);
        if ("0".equals(typeName)){
            //气温
            varName = weatherVarName[varNameIndex];
        }else if ("1".equals(typeName)){
            //降水
            varName = pravgVarName[varNameIndex];
        }else if ("2".equals(typeName)){
            //10米风
            varName = wsavgVarName[varNameIndex];
        }else if ("3".equals(typeName)){
            //2米比湿
            varName = otherVarName[varNameIndex];
        }
        resultList.add(varName);
        resultList.add(year);
        resultList.add(caseNum);
        resultList.add(monthAndDay + hour);
        resultList.add(obsDataName);
        int meanTypeIndex = Integer.valueOf(meanType);
        //区分是否是偏差
        String[] pictureName = {"ysqmean", "ysqmean_bias", "monmean", "monmean_bias", "seasmean", "seasmean_bias"};
        //图片添加字段的index
        int count = 0;
        if(varNameIndex % 2 == 0){
            resultPath = HbPathUtils.Hb_Normal_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
        }else {
            count = count + 1;
            resultPath = HbPathUtils.Hb_Normal_dev_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
        }
        if("0".equals(meanType)){
            resultList.add(4,pictureName[count]);
            resultPath = resultPath + pathName(resultList);
        }else if("1".equals(meanType)){
            int index = Integer.valueOf(month) - 3;
            count = count + 2;
            resultList.add(2, monthEngName[index]);
            resultList.add(5, pictureName[count]);
            resultPath = resultPath + pathName(resultList);
        }else if("2".equals(meanType)){
            int index = Integer.valueOf(seas);
            count = count + 4;
            resultList.add(2, seasEngName[index]);
            resultList.add(5, pictureName[count]);
            resultPath = resultPath + pathName(resultList);
        }
        return resultPath;
    }

    /**
     * 实际出图执行方法  提交到服务器
     * @param map
     * @return
     *
     * json参数
     *
        {
            "typeName": 0,
            "meanType": 0,
            "mmDd": "0302",
            "hour": [
            "01",
            "06",
            "12",
            "18"
            ],
            "caseNum": [
            "01",
            "02",
            "15",
            "16"
            ],
            "obsDataName": "cn_obs",
            "month": "03",
            "seas": "0",
            "varName": "0",
            "startYear": "1993",
            "endYear": "2015"
        }
     0401的caseNum为01，02，06，15
     */
    @Override
    public List<String> execWeatherFirstPicture(Map<String, Object> map) {

        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        List<String> resultList = new ArrayList<>();
        String typeName = map.get("typeName") + "";
        //类别  延伸期-0/月尺度-1/季节尺度-2
        String meanType = map.get("meanType") + "";
        //起报month+day
        String monthAndDay = map.get("mmDd") + "";
        //起报时次 hour
        String hour = map.get("hour") + "";
        //case
        String caseNum = map.get("caseNum") + "";
        //观测资料 cfsr/eri/cn_obs站点
        String obsDataName = map.get("obsDataName") + "";
        //月尺度对应月份
        String month = map.get("month") + "";
        //季尺度季度
        String seas = map.get("seas") + "";
        String varName = map.get("varName") + "";
        String startYear = map.get("startYear") + "";
        String endYear = map.get("endYear") + "";
        String obsDataPath = null;
        String modelDataPath = null;
        String filterModelPath = null;
        String filterObsPath = null;
        //用于记录obsDataPath内路径名称
        String obsPathName = null;
        //obsDataName名称转换
        if ("cn_obs".equals(obsDataName)){
            obsDataName = "CN05";
            obsPathName = "CN_OBS";
        }else if("cfsr".equals(obsDataName)){
            obsDataName = "";
            obsPathName = "";
        }else if("eri".equals(obsDataName)){
            obsDataName = "";
            obsPathName = "";
        }
        //具体路径拼接  需要区分  1.气温 降水 10米风 2米比湿    2.延伸期 月尺度 季节尺度
        int varNameIndex = Integer.valueOf(varName);
        if ("0".equals(typeName)){
            //气温
            varName = weatherVarName[varNameIndex];
        }else if ("1".equals(typeName)){
            //降水
            varName = pravgVarName[varNameIndex];
        }else if ("2".equals(typeName)){
            //10米风
            varName = wsavgVarName[varNameIndex];
        }else if ("3".equals(typeName)){
            //2米比湿
            varName = otherVarName[varNameIndex];
        }

        obsDataPath = obs_base_path + obsPathName + "/";
        modelDataPath = model_base_path + monthAndDay + "/POST_C" + caseNum + "_GCMSST/" + monthAndDay + hour + "/";
        // 根据起报时次mmdd和hour区分出obsDataPath  modelDataPath
        if ("0302".equals(monthAndDay)){
            if("00".equals(hour)){
                obsDataPath = obsDataPath + "0302/";
            }else {
                obsDataPath = obsDataPath + "0303/";
            }
        }else if("0401".equals(monthAndDay)){
            if("00".equals(hour)){
                obsDataPath = obsDataPath + "0401/";
            }else {
                obsDataPath = obsDataPath + "0402/";
            }
        }
        int meanTypeIndex = Integer.valueOf(meanType);
        modelDataPath = modelDataPath + meanTypeArray[meanTypeIndex] + "/";
        obsDataPath = obsDataPath + meanTypeArray[meanTypeIndex] + "/";
        log.info("观测路径和模式路径{}, {}", modelDataPath, obsDataPath);
        filterModelPath = "\""  + SplitYearUtils.getFilterYear(Integer.valueOf(startYear), Integer.valueOf(endYear),
                modelDataPath + varName) + "\"";
        filterObsPath = "\""  + SplitYearUtils.getFilterYear(Integer.valueOf(startYear), Integer.valueOf(endYear),
                obsDataPath + varName) + "\"";

        String shellPath = null;
        String dataoutPath = null;
        String logPath = null;
        //区分是偏差还是不是偏差
        int count = 0;
        if(varNameIndex % 2 == 0){
            //说明是正常的，不是偏差
            shellPath = HbPathUtils.Hb_Normal_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
            dataoutPath = HbPathUtils.Hb_Normal_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
            logPath = HbPathUtils.Hb_Normal_Shell_Path + meanTypeArray[meanTypeIndex] + "/" + "log_" +  varName +
                    "_" + startYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;
        }else{
            count = count + 1;
            shellPath = HbPathUtils.Hb_Normal_dev_Shell_Path + meanTypeArray[meanTypeIndex] + "/";
            dataoutPath = HbPathUtils.Hb_Normal_dev_Picture_Path + meanTypeArray[meanTypeIndex] + "/";
            logPath = HbPathUtils.Hb_Normal_dev_Shell_Path + meanTypeArray[meanTypeIndex] + "/" + "log_" +  varName +
                    "_" + startYear + "_" + caseNum + "_" + monthAndDay + "_" + hour + "_" + obsPathName;


        }
        // 拼接数据 提交至服务器执行
        StringBuffer sb = new StringBuffer();
        sb.append(shellPath + "./shellTest.sh ").append(startYear + " ").append(endYear + " ").append(caseNum + " ").append(hour + " ").append(monthAndDay + " ")
                .append(obsDataName + " ").append(modelDataPath + " ").append(obsDataPath + " ").append(varName + " ")
                .append(filterModelPath + " ").append(filterObsPath + " ").append(shellPath + " ").append(dataoutPath + " ");
        sb.append("> " + logPath);
        System.out.println(sb);
        Result result = ParseShellUtils.parse(sb.toString());
        // 图片生成结束后，要验证是否生成成功
        //存放未生成图片的信息
        List<Map<String, Object>> picResultList = new ArrayList<>();
        Integer start_year = Integer.valueOf(startYear);
        Integer end_year = Integer.valueOf(endYear);
        //图片添加字段的index count为图片后缀index
        String[] pictureNameArray = {"ysqmean", "ysqmean_bias", "monmean", "monmean_bias", "seasmean", "seasmean_bias"};
        if("0".equals(meanType)){
            count = count;
        }else if("1".equals(meanType)){
            count = count + 2;
        }else if("2".equals(meanType)){
            count = count + 4;
        }
        List<String> pathList = new ArrayList<>();
        pathList.add(varName);
        pathList.add(caseNum);
        pathList.add(monthAndDay + hour);
        pathList.add(pictureNameArray[count]);
        pathList.add(obsDataName);
        for(int i = start_year; i<=end_year; i++){
            if(i > start_year){
                // 先删除之前的年份  换成新的年份插入
                pathList.remove(1);
            }
            pathList.add(1, String.valueOf(i));
            String pictureName = dataoutPath + pathName(pathList);
            log.info("pictureName{}", pictureName);
            File file = new File(pictureName);
            if(!file.exists()){
                Map<String, Object> picMap = new HashMap<>();
                picMap.put("year", i);
                picMap.put("varName", varName);
                picMap.put("caseNum", caseNum);
                picMap.put("mmddhour", monthAndDay + hour);
                picMap.put("dataName", obsDataName);
                picResultList.add(picMap);
                System.out.println("no============");
            }else {
                resultList.add(pictureName);
            }
        }
        //说明存在年份没有生成对应图片
        if(picResultList.size() > 0){
            resultList.add(picResultList.toString());
        }
        lock.unlock();
        return resultList;
    }

    @Override
    public List<Map<String, Object>> batchExecWeatherFirstPicture(Map<String, Object> map) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String typeName = map.get("typeName") + "";
        //类别  延伸期-0/月尺度-1/季节尺度-2
        String meanType = map.get("meanType") + "";
        //起报month+day
        String monthAndDay = map.get("mmDd") + "";
        //起报时次 hour
        List<String> hourList = (List<String>) map.get("hour");
        //case
        List<String> caseNumList = (List<String>) map.get("caseNum");
        //观测资料 cfsr/eri/cn_obs站点
        String obsDataName = map.get("obsDataName") + "";
        //月尺度对应月份
        String month = map.get("month") + "";
        //季尺度季度
        String seas = map.get("seas") + "";
        String varName = map.get("varName") + "";
        String startYear = map.get("startYear") + "";
        String endYear = map.get("endYear") + "";
        for(String hour : hourList){
            for(String caseNum : caseNumList){
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("typeName", typeName);
                paramMap.put("meanType", meanType);
                paramMap.put("mmDd", monthAndDay);
                paramMap.put("hour", hour);
                paramMap.put("caseNum", caseNum);
                paramMap.put("obsDataName", obsDataName);
                paramMap.put("month", month);
                paramMap.put("seas", seas);
                paramMap.put("varName", varName);
                paramMap.put("startYear", startYear);
                paramMap.put("endYear", endYear);
                List<String> list = execWeatherFirstPicture(paramMap);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("hour", hour);
                resultMap.put("caseNum", caseNum);
                resultMap.put("result", list);
                resultList.add(resultMap);
            }
        }

        return resultList;
    }


    /**
     * 气温出图 第二张提交绘图
     * @param map
     * @return
     */
    @Override
    public String weatherSecondPicture(Map<String, Object> map) {
        return null;
    }

    /**
     * 气温出图 第三张提交绘图
     * @param map
     * @return
     */
    @Override
    public String weatherThirdPicture(Map<String, Object> map) {
        return null;
    }


    /**
     * 生成的图片名称拼接
     * @param pathList
     * @return
     */
    private String pathName(List<String> pathList){
        StringBuffer sb = new StringBuffer();
        for (String i: pathList) {
            sb.append("_").append(i);
        }
        return sb.toString().replaceFirst("_", "") + ".png";
    }
}
