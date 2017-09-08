package com.superschool.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.superschool.R;
import com.superschool.entity.BossInfo;
import com.superschool.services.BossServiceImpl;

public class ToBeBossActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Button submit;
    private ImageButton addPaperPhoto;
    private CheckBox agree;
    private Spinner typeOfBusiness;
    private Spinner typeOfPaper;
    private EditText numOfPaper;
    private EditText storeName;
    private EditText storeInfo;
    private TextView locationInfo;
    private static String latitude;
    private static String longitude;
    private CheckBox currentLocationAsStoreLocation;
    private static final String[] businessType = {"打印", "超市", "跑腿"};
    private ArrayAdapter<String> businessTypeAdapter;
    private String[] paperType = {"中国大陆居民身份证", "香港特别行政区居民身份证", "澳门特别行政区居民身份证"};
    private ArrayAdapter<String> paperTypeAdapter;
    private TextView pickPosition;
    private ImageView back;
    private static final int  GET_POSITION=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_be_boss);
        initView();
        initEvent();
    }

    private void initView() {
        back= (ImageView) findViewById(R.id.back);
        submit = (Button) findViewById(R.id.submitBusinessApply);
        addPaperPhoto = (ImageButton) findViewById(R.id.addPaperPhoto);
        agree = (CheckBox) findViewById(R.id.agree);
        typeOfBusiness = (Spinner) findViewById(R.id.typeOfBusiness);
        typeOfPaper = (Spinner) findViewById(R.id.typeOfPaper);
        numOfPaper = (EditText) findViewById(R.id.numOfPaper);
        businessTypeAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, businessType);
        typeOfBusiness.setAdapter(businessTypeAdapter);
        typeOfBusiness.setVisibility(View.VISIBLE);
        paperTypeAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, paperType);
        typeOfPaper.setAdapter(paperTypeAdapter);
        typeOfPaper.setVisibility(View.VISIBLE);
        typeOfBusiness.setEnabled(false);
        typeOfPaper.setEnabled(false);
        submit.setEnabled(false);
        addPaperPhoto.setEnabled(false);
        storeName = (EditText) findViewById(R.id.storeName);
        storeInfo = (EditText) findViewById(R.id.storeInfo);
        locationInfo = (TextView) findViewById(R.id.locationInfo);
        currentLocationAsStoreLocation = (CheckBox) findViewById(R.id.currentLocationAsStoreLocation);
        storeName.setEnabled(false);
        storeInfo.setEnabled(false);
        currentLocationAsStoreLocation.setEnabled(false);
        numOfPaper.setEnabled(false);
        pickPosition= (TextView) findViewById(R.id.pickPosition);
        pickPosition.setEnabled(false);


    }

    private void initEvent() {
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
        addPaperPhoto.setOnClickListener(this);
        agree.setOnCheckedChangeListener(this);
        pickPosition.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitBusinessApply: {
                System.out.println("submit");
                final BossInfo boss = new BossInfo();
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                String userID = sharedPreferences.getString("userID", null);
                SharedPreferences local = getSharedPreferences("local", MODE_PRIVATE);
                String latitude = local.getString("latitude", null);
                String longitude = local.getString("longitude", null);
                String school = local.getString("school", null);
                boss.setStoreLatitude(latitude);
                boss.setStoreLongitude(longitude);
                boss.setBossUserID(userID);
                boss.setBossIdType(typeOfPaper.getSelectedItem().toString());
                boss.setBossId(numOfPaper.getText().toString());
                boss.setStoreLatitude(latitude);
                boss.setStoreLongitude(longitude);
                switch (typeOfBusiness.getSelectedItem().toString()) {
                    case "打印": {
                        boss.setStoreType("print");
                        break;
                    }
                    case "超市": {
                        boss.setStoreType("store");
                        break;
                    }
                    case "跑腿":{
                        boss.setStoreType("Legwork");
                        break;
                    }
                }
                boss.setStoreName(storeName.getText().toString());
                boss.setStoreInfo(storeInfo.getText().toString());
                boss.setStoreID(userID);
                boss.setSchool(school);
                final BossServiceImpl bossService = new BossServiceImpl();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bossService.applyBoss(boss);
                    }
                }).start();

                break;
            }
            case R.id.addPaperPhoto: {

                break;
            }
            case R.id.submit: {

                break;
            }
            case R.id.pickPosition:{
                Intent intent=new Intent(ToBeBossActivity.this,MapActivity.class);
                startActivityForResult(intent,GET_POSITION);
                break;
            }
            case R.id.back:{
                ToBeBossActivity.this.finish();
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (agree.isChecked()) {
            pickPosition.setEnabled(true);
            typeOfBusiness.setEnabled(true);
            typeOfPaper.setEnabled(true);
            submit.setEnabled(true);
            addPaperPhoto.setEnabled(true);
            storeName.setEnabled(true);
            storeInfo.setEnabled(true);
            currentLocationAsStoreLocation.setEnabled(true);
            numOfPaper.setEnabled(true);
        } else {
            typeOfBusiness.setEnabled(false);
            typeOfPaper.setEnabled(false);
            submit.setEnabled(false);
            addPaperPhoto.setEnabled(false);
            storeName.setEnabled(false);
            storeInfo.setEnabled(false);
            currentLocationAsStoreLocation.setEnabled(false);
            numOfPaper.setEnabled(false);
            pickPosition.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println( resultCode);
        if(resultCode==GET_POSITION){
              latitude=data.getStringExtra("latitude");
              longitude=data.getStringExtra("longitude");
              locationInfo.setText("当前经纬度信息：\n"+"纬度："+latitude+"\n"+"经度："+longitude);
        }
    }
}
