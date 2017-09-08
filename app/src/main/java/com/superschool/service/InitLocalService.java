package com.superschool.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.superschool.databases.InitDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/6/11.
 */



public class InitLocalService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("开始本地服务");
        initOrderInfo();

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void initOrderInfo() {
        //这里 我首先要判断当前用户是开什么店铺的。
        /*
        * 1.首先去查print表，是否有该用户，并且返回结果
        * 先假设只有打印
        * */
        System.out.println("初始化本地服务");

        Map<String,String> map=new HashMap<String, String>();
        SharedPreferences shared=this.getSharedPreferences("user",MODE_PRIVATE);
        String userID=shared.getString("userID",null);
        InitDatabase database=new InitDatabase(this);
        String sql="select read from printorder where bossID=? and read=?";
        String[] obj=new String[]{userID,"no"};
        Cursor cursor=database.query(sql, obj);
        if(cursor!=null){
            String noRead=String.valueOf(cursor.getCount());
            map.put("noRead",noRead);
            map.put("orderType","printOrder");
            Intent sendBroadcastIntent = new Intent();
            sendBroadcastIntent.putExtra("orderInfo", (Serializable) map);
            sendBroadcastIntent.setAction("com.superschool.UPDATE_UI");
            LocalBroadcastManager.getInstance(this).sendBroadcast(sendBroadcastIntent);
            System.out.println("广播 已经发送");
        }else {
            System.out.println("没有开过店");
        }



    }


}
