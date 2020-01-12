package com.itstyle.bcc_cwrf.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 读取服务器上txt文件工具类
 * @Author hy
 * @Date 2019-12-26 10:19
 * @Version 1.0
 */

public class ReadServiceTxtUtils {
    //获取txt里面的结果，用List返回
    public static List<String> readTxt(File file) throws IOException {
        String s = null;
        InputStreamReader in = new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader br = new BufferedReader(in);
        List<String> stringList= new ArrayList<>();
        while ((s=br.readLine())!=null){
            stringList.add(s.replaceAll("\"","").replaceAll(" ",""));
        }
        return stringList;
    }
}
