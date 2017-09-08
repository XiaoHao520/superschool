package com.superschool.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/6/14.
 */

public class UploadDataInsertInToServer {
    public static String InsertInToServer(Map<String,String>datas,String urlPath) throws IOException {
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");// 提交模式
        // conn.setConnectTimeout(10000);//连接超时 单位毫秒
        // conn.setReadTimeout(2000);//读取超时 单位毫秒
        conn.setDoOutput(true);// 是否输入参数

        StringBuffer params = new StringBuffer();
        for (Map.Entry<String, String> entry : datas.entrySet()) {
            params.append(entry.getKey()+"=" + entry.getValue()+"&");
        }
        params.deleteCharAt(params.lastIndexOf("&"));
        byte[] bypes = params.toString().getBytes();
        conn.getOutputStream().write(bypes);// 输入参数
        InputStream inStream=conn.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inStream, "utf-8"));
        StringBuffer buffer = new StringBuffer();
        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
		/* 释放资源 */
        bufferedReader.close();
        inStream.close();
        return buffer.toString();
    }
}
