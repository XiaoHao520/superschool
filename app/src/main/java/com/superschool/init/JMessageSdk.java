package com.superschool.init;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.superschool.entity.User;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by XIAOHAO on 2017/5/26.
 */

public class JMessageSdk {
    User user;
    Context context;
    private static boolean flag = false;

    public JMessageSdk(User user) {
        this.user = user;
    }

    public JMessageSdk(Context context) {
        this.context = context;
    }

    public JMessageSdk(User user, Context context) {
        this.user = user;
        this.context = context;
    }

    public void jMessagelogin() {
        JMessageClient.login(user.getUserID(), user.getPassword(), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                switch (i) {
                    case 0: {
                        Intent intent=new Intent();
                        intent.putExtra("isLogin",true);
                        intent.setAction("com.superschool.loginReceiver");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        System.out.println("广播 已经发送");
                        Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        });

    }

    public boolean jMessageRegist() {
        JMessageClient.register(user.getUserID(), user.getPassword(), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {

                switch (i) {
                    case 0: {
                        Toast.makeText(context, "注册成功", Toast.LENGTH_LONG).show();
                        flag = true;
                        break;
                    }
                    default: {
                        Toast.makeText(context, "注册失败", Toast.LENGTH_LONG).show();
                        flag = false;
                        break;
                    }
                }
            }
        });
        return flag;
    }
}
