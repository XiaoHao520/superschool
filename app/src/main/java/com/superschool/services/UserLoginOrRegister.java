package com.superschool.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.superschool.entity.User;
import com.superschool.init.JMessageSdk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by XIAOHAO on 2017/5/26.
 */

public class UserLoginOrRegister implements UserLoginOrRegisterService {
    Context context;
  private static boolean flag;
    public UserLoginOrRegister(Context context) {
        this.context = context;
    }


    @Override
    public int userLoginOrRegisterService(User user) throws IOException {
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

    @Override
    public User checkHasUserexisted() throws IOException {
        User user=new User();
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        if (userID != null&&!userID.equals("vistor")) {
            String password = sharedPreferences.getString("password", null);
            user.setUserID(userID);
            user.setPassword(password);
            return user;
        }
        return null;
    }
}
