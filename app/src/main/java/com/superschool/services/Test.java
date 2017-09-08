package com.superschool.services;

import com.superschool.entity.User;
import com.superschool.init.JMessageSdk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by XIAOHAO on 2017/6/13.
 */

public class Test {
     public static void main(String[]args) throws IOException {
         User user=new User();
         user.setDoWhat("login");
         user.setUserID("xiaohao");
         user.setPassword("122222");
         userLoginOrRegisterService(user);
     }
    public static int userLoginOrRegisterService(User user) throws IOException {
        HttpURLConnection conn;
        JMessageSdk jMessageSdk = new JMessageSdk(user);
        int rsCode = 0;
        if (user.getDoWhat().equals("login")) {
            StringBuffer sb = new StringBuffer();
            sb.append("username=" + user.getUserID() + "&" + "password=" + user.getPassword());
            URL url = new URL("http://ganxiaohao.gicp.net/loginAction.action");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            byte[] bytes = sb.toString().getBytes();
            conn.getOutputStream().write(bytes);
            rsCode = conn.getResponseCode();
        } else if (user.getDoWhat().equals("register")) {
            StringBuffer sb = new StringBuffer();
            sb.append("email=" + user.getEmail());
            sb.append("&");
            sb.append("tel=" + user.getTel());
            sb.append("&");
            sb.append("password=" + user.getPassword());

            URL url = new URL("http://ganxiaohao.gicp.net/registerAction.action");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            byte[] bytes = sb.toString().getBytes();
            conn.getOutputStream().write(bytes);
            rsCode = conn.getResponseCode();
            if (rsCode == 200) {
                if (jMessageSdk.jMessageRegist()) {
                    rsCode = 200;
                }
            }
        }
        return rsCode;
    }
}

