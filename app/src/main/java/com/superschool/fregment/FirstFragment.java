package com.superschool.fregment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.superschool.Data.Wether;
import com.superschool.R;
import com.superschool.activity.LoginActivity;
import com.superschool.activity.PrintUploadActivity;
import com.superschool.databases.InitDatabase;
import com.superschool.http.StudentStoreData;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by XIAOHAO on 2017/4/15.
 */

public class FirstFragment extends Fragment implements LocationSource, View.OnClickListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMapLocationListener, AMap.OnMapClickListener {
    private static final int LOGIN_CODE = 1;
    private static final int PRINT_CODE = 2;
    private TextView wetherText;
    private Button loginBtn;
    private Button locationBtn;
    private UiSettings uiSettings;
    private TextView locationText;
    private AMapLocationClient aMapLocationClient;
    private AMapLocationClientOption aMapLocationClientOption;
    private LatLng latLng;
    Context context;
    private InitDatabase initDatabase;
    private String username = "visitor";
    List<Map<String, String>> storeInfoList;
    //声明AMapLocationClient类对象
    Marker marker;
    private MapView mapView;
    private AMap aMap;

    private AMapLocationClient mapLocationClient = null;
    private LocationSource.OnLocationChangedListener mListener;
    private boolean isFirstLocation = true;
    View view;
    LoginReceiver loginReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f1_layout, container, false);
        context = getActivity();
        initView();
        initMap(savedInstanceState);
        initEvent();
        initLocationPrePare();
        try {
            initData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ensureUserLogined();

        System.out.println("**********************注册广播****************");
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.superschool.loginReceiver");
        Handler handler=new Handler(){
        };
        loginReceiver = new LoginReceiver(handler);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(loginReceiver, filter);
        System.out.println("***************************************");

        return view;
    }




    private void initData() throws IOException {

        SharedPreferences local = getActivity().getSharedPreferences("local", Context.MODE_PRIVATE);
        String wether = local.getString("wether", null);
        String location = local.getString("school", null);
        if (wether == null) {
            wetherText.setText("正在获取...");
            locationText.setText("正在获取...");
        } else {
            wetherText.setText(wether);
            locationText.setText(location);
            double latitude = Double.parseDouble(local.getString("latitude", null));
            double longitude = Double.parseDouble(local.getString("longitude", null));
            LatLng latLng = new LatLng(latitude, longitude);
            focusOn(latLng);

        }
    }

    private void insertLocalData(Map<String, String> localData) {
        SharedPreferences local = getActivity().getSharedPreferences("local", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = local.edit();
        editor.putString("wether", localData.get("wether"));
        editor.putString("latitude", localData.get("latitude"));
        editor.putString("longitude", localData.get("longitude"));
        editor.putString("school", localData.get("school"));
        editor.commit();
    }

    private void initLocationPrePare() {
        aMapLocationClient = new AMapLocationClient(getActivity());
        aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setInterval(2000);
        aMapLocationClientOption.setGpsFirst(true);
        aMapLocationClientOption.setNeedAddress(true);
        aMapLocationClientOption.setOnceLocation(true);
        aMapLocationClientOption.setWifiScan(true);
        aMapLocationClientOption.setLocationCacheEnable(true);
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        aMapLocationClient.setLocationListener(this);
        aMapLocationClient.startLocation();


    }

    private void initEvent() {
        loginBtn.setOnClickListener(this);
        //locationBtn.setOnClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setOnMapClickListener(this);

    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        UiSettings settings = aMap.getUiSettings();
        settings.setMyLocationButtonEnabled(false);

        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
    }

    private void initView() {
        locationText = (TextView) view.findViewById(R.id.locationText);
        wetherText = (TextView) view.findViewById(R.id.wetherText);
        loginBtn = (Button) view.findViewById(R.id.userLogin);
     /*   locationBtn = (Button) view.findViewById(R.id.getLocation);*/
        mapView = (MapView) view.findViewById(R.id.map);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           /* case R.id.getLocation: {

                aMapLocationClient.startLocation();


                break;
            }*/
            case R.id.userLogin: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivityForResult(intent, LOGIN_CODE);
                break;
            }

        }

    }

    @Override
    public boolean onMarkerClick(Marker argMarker) {
       if (marker.isInfoWindowShown()){
           marker.hideInfoWindow();
       }
        marker=argMarker;
        if(!marker.isInfoWindowShown()){
           marker.showInfoWindow();
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int requestCode=0;
        Map<String, String> map;
        Intent intent = new Intent();
        for (int i = 0; i < storeInfoList.size(); i++) {
            map = storeInfoList.get(i);


            if (marker.getTitle().equals(map.get("storename"))) {
                switch (map.get("type")) {
                    case "print": {
                        intent.setClass(getActivity(), PrintUploadActivity.class);

                        intent.putExtra("storeInfo", (Serializable) map);
                        requestCode=PRINT_CODE;
                        break;
                    }
                }
                break;
            }
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onMapClick(LatLng latLng) {
          if(marker.isInfoWindowShown()){
              marker.hideInfoWindow();
          }
    }


    class MyLocationHandler implements Runnable {
        private Handler myHandler;
        private String city;
        private String interestPlace;
        private LatLng latLng;

        public MyLocationHandler(Handler myHandler, String city, String interestPlace, LatLng latLng) {
            this.myHandler = myHandler;
            this.city = city;
            this.interestPlace = interestPlace;
            this.latLng = latLng;
        }

        @Override
        public void run() {
            try {
                Map<String, String> wetherMap = new Wether(city).getWether();
                Message message = new Message();
                String wether;
                int temperature1 = Integer.parseInt(wetherMap.get("temperature1"));
                int temperature2 = Integer.parseInt(wetherMap.get("temperature2"));
                if (temperature1 == temperature2) {
                    wether = temperature1 + "°C";

                } else if (temperature1 > temperature2) {
                    wether = temperature2 + "~" + temperature1 + "°C";
                } else {
                    wether = temperature1 + "~" + temperature2 + "°C";
                }
                Map<String, String> localData = new HashMap<String, String>();
                localData.put("wether", wether);
                localData.put("school", interestPlace);
                localData.put("latitude", String.valueOf(latLng.latitude));
                localData.put("longitude", String.valueOf(latLng.longitude));
                insertLocalData(localData);

                message.obj = wether;
                myHandler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    //位置改变后在定位
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {

                String interestPlace = aMapLocation.getAoiName();
                Toast.makeText(getContext(), interestPlace, Toast.LENGTH_LONG).show();

                final String city = aMapLocation.getCity().split("市")[0];
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        wetherText.setText((String) msg.obj);
                    }
                };
                new Thread(new MyLocationHandler(handler, city, interestPlace, new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()))).start();
                locationText.setText(interestPlace);
                try {

                    getSchoolStoreData(aMapLocation.getAoiName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                if (isFirstLocation) {
                    focusOn(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                } else {

                }
            }
        }
    }


    public void setMarkers(List<Map<String, String>> storeListMap) {

        for (int i = 0; i < storeListMap.size(); i++) {
            Map<String, String> store = storeListMap.get(i);
            double latitude = Double.parseDouble(store.get("latitude"));
            double longitude = Double.parseDouble(store.get("longitude"));
            LatLng latLng = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng).title(store.get("storename")).snippet(store.get("storeinfo"));
            markerOptions.visible(true);
            marker = aMap.addMarker(markerOptions);
            marker.setObject(store);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
       /* aMap.moveCamera(CameraUpdateFactory.zoomBy(17));
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));*/
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }


    private void getSchoolStoreData(final String school) throws Exception {
        //网络获取学生超市信息

        class MySchoolStoreDataHandler implements Runnable {
            Handler handler;
            Context context;

            public MySchoolStoreDataHandler(Context context, Handler handler) {
                this.context = context;
                this.handler = handler;
            }

            @Override
            public void run() {
                StudentStoreData storeData = new StudentStoreData(context);
                try {
                    Message message = new Message();
                    List<Map<String, String>> storeInfoList = storeData.getStudentStoreData(school);

                    System.out.println("storeInfoList:"+storeInfoList);
                    message.obj = storeInfoList;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                storeInfoList = (List<Map<String, String>>) msg.obj;


                //storeInfoList.
                setMarkers(storeInfoList);
            }
        };
        new Thread(new MySchoolStoreDataHandler(getContext(), handler)).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE) {
            if (data.getIntExtra("ok", 0) == 1) {
                loginBtn.setBackgroundResource(R.drawable.login);
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("userID",data.getStringExtra("userID"));
                editor.putString("password",data.getStringExtra("password"));
                editor.commit();
            }
        }
    }

    private void focusOn(LatLng latLng) {
        if (isFirstLocation) {
            aMap.moveCamera(CameraUpdateFactory.zoomBy(17));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            isFirstLocation = false;
        } else {

        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }




  public  class LoginReceiver extends BroadcastReceiver {
      Handler handler;
      public LoginReceiver(Handler handler) {
          this.handler = handler;
      }

      @Override
        public void onReceive(Context context, Intent intent) {
            boolean  isLogin=intent.getBooleanExtra("isLogin",false);
            System.out.println("收到登录通知  是否登陆=="+isLogin);
           loginBtn.setBackgroundResource(R.drawable.login);

      }
    }

    private void ensureUserLogined() {

        class MyHandler implements Runnable {
            Context context;
            Handler mHandler;
            public MyHandler(Context context, Handler mHandler) {
                this.context = context;
                this.mHandler = mHandler;
            }
            @Override
            public void run() {
                SharedPreferences sharedPreferences=context.getSharedPreferences("user",Context.MODE_PRIVATE);
                final String userID=sharedPreferences.getString("userID",null);
                final String password=sharedPreferences.getString("password",null);
                JMessageClient.getUserInfo(userID, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        if(i==0){
                            Message msg=new Message();
                            msg.obj=true;
                            mHandler.sendMessage(msg);
                        }else {
                            JMessageClient.login(userID, password, new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    Message msg=new Message();
                                    if(i==0){

                                        msg.obj=true;
                                    }else {
                                        msg.obj=false;
                                    }
                                    mHandler.sendMessage(msg);
                                }
                            });
                        }
                    }
                });
            }
        }
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                boolean flag = (boolean) msg.obj;
                if (flag) {
                    loginBtn.setBackgroundResource(R.drawable.login);
                }
            }
        };
        new Thread(new MyHandler(context, handler)).start();
    }

}
