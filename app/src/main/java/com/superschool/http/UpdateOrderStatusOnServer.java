package com.superschool.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by XIAOHAO on 2017/6/14.
 */

public class UpdateOrderStatusOnServer {
    /**
     * 上传文件时定义数据分隔符(可以随意更改)
     **/
    private static String boundary = "thisIsBoundary";
    /**
     * 上传文件时配合分隔符使用
     **/
    private static String twoHyphens = "--";
    /**
     * 上传文件时结尾标识
     **/
    private static String end = "\r\n";

    public static String getStringStream(String urlPath, String orderType, String orderID, String status) throws Exception {


        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");// 提交模式
        // conn.setConnectTimeout(10000);//连接超时 单位毫秒
        // conn.setReadTimeout(2000);//读取超时 单位毫秒
        conn.setDoOutput(true);// 是否输入参数
        conn.setDoInput(true);
        StringBuffer params = new StringBuffer();
        params.append("orderType=" + orderType);
        params.append("&orderID=" + orderID);
        params.append("&status=" + status);

        byte[] bypes = params.toString().getBytes();


       // DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
       conn.getOutputStream().write(bypes);

      /*  writeStringParams(dos, "orderType", orderType);
        writeStringParams(dos, "orderID", orderID);
        writeStringParams(dos, "status", status);
*/
        //获取输入
        InputStream inStream = conn.getInputStream();
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

    public static void writeStringParams(DataOutputStream dos, String name,
                                         String value) throws Exception {
        dos.writeBytes(twoHyphens + boundary + end);
        dos.writeBytes("Content-Disposition:form-data;name=\"" + name + "\""
                + end);
        dos.writeBytes(end);
        dos.write(value.getBytes("UTF-8"));
        dos.writeBytes(end);
    }
}
