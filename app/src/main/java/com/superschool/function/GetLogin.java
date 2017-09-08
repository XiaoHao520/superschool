package com.superschool.function;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by XIAOHAO on 2017/3/29.
 */

public class GetLogin {
    private String username;
    private String password;

    public GetLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getLogin() throws IOException {

        StringBuffer sb=new StringBuffer();
        sb.append("username="+username+"&"+"password="+password);
        URL url=new URL("http://10.12.7.255:8080/loginAction.action");
        HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoOutput(true);
        byte[]bytes=sb.toString().getBytes();
        httpURLConnection.getOutputStream().write(bytes);
        int rsCode=httpURLConnection.getResponseCode();
        return rsCode;
    }
}
