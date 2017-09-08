package com.superschool.jmessagesdk;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.superschool.entity.User;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by XIAOHAO on 2017/5/24.
 */

public class JMessageLogin {
    User user;
    Context context;
    private static boolean flag = false;
    public JMessageLogin(User user,Context context) {
        this.user = user;
        this.context=context;
    }
    public void login(){

        JMessageClient.setDebugMode(true);
        JMessageClient.init(context);

        JMessageClient.login(user.getUserID(), user.getPassword(), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                switch (i){
                    case 0:{   System.out.println("广播 已经发送");

                        Toast.makeText(context,"登录成功",Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:{
                        Toast.makeText(context,"登录失败",Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        });

    }
    public boolean regist(){
        JMessageClient.register(user.getUserID(), user.getPassword(), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
               switch (i){
                   case 0:{
                         Toast.makeText(context,"注册成功",Toast.LENGTH_LONG).show();
                        flag =true;
                       break;
                   }
                   default:{
                       Toast.makeText(context,"Jmessage注册失败",Toast.LENGTH_LONG).show();
                       flag =false;
                       break;
                   }
               }
            }
        });

        return flag;
    }
}
