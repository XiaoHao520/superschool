package com.superschool.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

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
import com.superschool.R;


public class MapActivity extends Activity implements LocationSource, AMapLocationListener, AMap.OnInfoWindowClickListener, AMap.OnMarkerClickListener, AMap.OnMapClickListener {
    private UiSettings uiSettings;
    private MapView mapView;
    private AMapLocationClient aMapLocationClient;
    private AMapLocationClientOption aMapLocationClientOption;
    static boolean isFirstLocation = true;
    static AMap.OnMarkerDragListener markerDragListener;
    AMap aMap;
    Marker marker;
    Context context;
    private static final int GET_POSITION_OK = 10;
    private static final int GET_POSITION_NO = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        context = getApplicationContext();
        initView();
        initMap(savedInstanceState);
        initLocationPrePare();
        initEvent();


    }

    private void initEvent() {
// 绑定marker拖拽事件
        markerDragListener=new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                marker.hideInfoWindow();

            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                marker.setSnippet("当前经纬度："+marker.getPosition());
                marker.showInfoWindow();
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub

            }


        };
        aMap.setOnMarkerDragListener(markerDragListener);
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
        aMap.setOnMarkerClickListener(this);
    }

    private void initLocationPrePare() {
        aMapLocationClient = new AMapLocationClient(this);
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

    private void initView() {
        mapView = (MapView) findViewById(R.id.myMap);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {

            if (aMapLocation.getErrorCode() == 0) {
                String interestPlace = aMapLocation.getAoiName();
                final String city = aMapLocation.getCity().split("市")[0];
                System.out.println(city);
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                if (isFirstLocation) {
                    focusOn(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                    isFirstLocation = false;
                } else {

                }
            }
        }
    }

    private void focusOn(LatLng latLng) {
        if (isFirstLocation) {
            marker = aMap.addMarker(new MarkerOptions().position(latLng).title("我的位置").snippet("当前经纬度：" + latLng.toString()).visible(true));
            marker.setDraggable(true);
            aMap.moveCamera(CameraUpdateFactory.zoomBy(17));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            isFirstLocation = false;
        } else {

        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng latLng = marker.getPosition();
        final String latitude = String.valueOf(latLng.latitude);
        final String longitude = String.valueOf(latLng.longitude);
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("当前位置信息");
        builder.setMessage(marker.getPosition().toString());
        builder.setPositiveButton("使用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //回到注册老板界面
                Intent intent=MapActivity.this.getIntent();
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                isFirstLocation=true;
                MapActivity.this.setResult(GET_POSITION_OK,intent);
                MapActivity.this.finish();
            }
        });
        builder.setNegativeButton("重新选取", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    @Override
    public boolean onMarkerClick(Marker argMarker) {
        marker=argMarker;
        if(marker.isInfoWindowShown()){
            marker.hideInfoWindow();
        }else {
            marker.showInfoWindow();
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        marker.hideInfoWindow();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.setResult(GET_POSITION_NO);
        isFirstLocation=true;
        this.finish();
    }
}
