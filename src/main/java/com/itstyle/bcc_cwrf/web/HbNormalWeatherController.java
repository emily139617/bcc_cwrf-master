package com.itstyle.bcc_cwrf.web;

import com.itstyle.bcc_cwrf.common.entity.Result;
import com.itstyle.bcc_cwrf.service.HbNormalWeatherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * @Author hy
 * @Description //气候回报数据 常规气候预测产品生成模块
 * @Date
 * @Param
 * @return }
 */

//bsub  -q q_x86_cn_cwrf -n 1
//        -shared -o ${shell}log_${yr}${day}${hr}c${cc}_${modelName}_${suffix}
//        ${shell}./shellTest.sh  ${yr} ${day} ${cc} ${shell} ${inputdata} ${outputdata} ${hr}
@Api(tags = "回报   常规气候预测产品生成模块")
@RestController
@RequestMapping("/hbNormal")
public class HbNormalWeatherController {
    private final static Logger log = LoggerFactory.getLogger(HbNormalWeatherController.class);
    private final static String errorMsg = "图片生成异常，请重试!";

    @Autowired
    private HbNormalWeatherService hbNormalWeatherService;

    /**
     * 由于常规气候预测产品生成模块  图片需要先生成 所以此接口仅用于查询数据
     * @param map
     * @return
     */
    @ApiOperation(value = "气候回报数据 常规气候预测产品生成模块")
    @PostMapping("/weather")
    public Result weatherPicture(@RequestBody Map<String, Object> map){
        log.info("获取的数据：{}", map);
//        目前常规模块cn_obs这个数据，我们只对比AT2M T2MAX T2MIN WSAVG PRAVG。
//        如果是cfsr和eri的话，会多一些AU10,AV10经向风纬向风，还有2米比湿AQ2m
//        平均气温对应的是AT2M，日最高气温对应的是T2MAX。日最低气温对应的是T2MIN，降水就是PRAVG，风速就是WSAVG
        String resultPath = hbNormalWeatherService.weatherFirstPicture(map);
        if(StringUtils.isNotBlank(resultPath)){
            return Result.ok(resultPath);
        }else{
            return Result.error(errorMsg);
        }
    }


    /**
     * 用于执行常规气候预测产品生成模块生成对应图片
     * @param map
     * @return
     */
    @ApiOperation(value = "气候回报数据 常规气候预测产品生成模块")
    @PostMapping("/execWeather")
    public Result execWeatherPicture(@RequestBody Map<String, Object> map){
        log.info("获取的数据：{}", map);
        List<String> resultPathList = hbNormalWeatherService.execWeatherFirstPicture(map);
        if(resultPathList.size() > 0){
            return Result.ok(resultPathList);
        }else{
            return Result.error(errorMsg);
        }
    }


    /**
     * 用于执行常规气候预测产品生成模块生成对应图片
     * @param map
     * @return
     */
    @ApiOperation(value = "气候回报数据 常规气候预测产品生成模块")
    @PostMapping("/batchExecWeather")
    public Result batchExecWeatherPicture(@RequestBody Map<String, Object> map){
        log.info("获取的数据：{}", map);
        List<Map<String, Object>> resultPathList = hbNormalWeatherService.batchExecWeatherFirstPicture(map);
        if(resultPathList.size() > 0){
            return Result.ok(resultPathList);
        }else{
            return Result.error(errorMsg);
        }
    }
}
