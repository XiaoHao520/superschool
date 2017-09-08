package com.superschool.Data;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.superschool.databases.FirstPageDatas;
import com.superschool.databases.InitDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/4/19.
 */

public class LocationData implements AMapLocationListener {
    private AMapLocationClient aMapLocationClient=null;
    private AMapLocationClientOption option=null;
    private Map<Object,Object> map;
    InitDatabase initDatabase;
    String user;
    Context context;
    public LocationData(Context context, String user) {
        this.user=user;
        this.context=context;

        aMapLocationClient=new AMapLocationClient(context);
        option=new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setInterval(2000);
        aMapLocationClient.setLocationOption(option);
        aMapLocationClient.setLocationListener(this);
        aMapLocationClient.startLocation();


    }
    private Map<Object,Object>getLocationData(){


        return map;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if(aMapLocation!=null){
            if(aMapLocation.getErrorCode()==0){

                try {
                    makeData(aMapLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            aMapLocationClient.stopLocation();
        }
    }
    private void makeData(final AMapLocation aMapLocation) throws Exception {

        final String city=(aMapLocation.getCity()).split("市")[0];

        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                System.out.println("-----------------------------------------------------makeData");
                map=new HashMap<>();
                double latitude=aMapLocation.getLatitude();
                double longitude=aMapLocation.getLongitude();
                String interest=aMapLocation.getPoiName();
                final String[] myWether = {null};
                myWether[0] =String.valueOf(msg.obj);

                map.put("latitude",latitude);
                map.put(longitude,longitude);
                map.put("interest",interest);
                map.put("myWether", myWether[0]);
                Object []obj=new Object[]{user, user, interest, myWether[0], latitude,longitude};
                initDatabase=new InitDatabase(context);
                FirstPageDatas firstPageDatas=new FirstPageDatas(initDatabase);
              firstPageDatas.insertOrUpdateUserData(user,obj);
           }
        };
        new Thread(new MyHandler(handler,city)).start();


    }
    private class MyHandler implements Runnable {
        private Handler mHandler;
        String city;
        public MyHandler(Handler handler,String city){
            this.mHandler=handler;
            this.city=city;
        }


        @Override
        public void run() {
            Map<String,String>map=new HashMap<String, String>();
            Wether wether=new Wether(city);
            try {
                map=wether.getWether();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message message=new Message();
            message.obj=map.get("temperature1") + "℃";
            mHandler.sendMessage(message);
        }
    }
}

