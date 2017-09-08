package com.superschool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.superschool.R;

import java.io.Serializable;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;


public class MyPrintOrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private String appkey = "b2b2aa47bae76270d275879b";
    private TextView receiver;
    private TextView fromID;
    private TextView address;
    private TextView tel;
    private TextView now;
    private TextView remarks;
    private TextView bossID;
    private Button shipment;//发货按钮
    Map<String, String> datas;
    private Button downLoadFiles;
    private TextView orderID;
    private static final int ON_CLICK_ITEM_DOING = 565;
    private static final int ON_CLICK_DO_NOTHING = 44;
    private static final int ON_CLICK_SEND = 685;//发货
    private static int rscode=44;
    Intent intent;
    private ImageView back;
    private ImageView toChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_print_order_detail);
        JMessageClient.init(getApplicationContext());
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        downLoadFiles.setOnClickListener(this);
        back.setOnClickListener(this);
        toChat.setOnClickListener(this);
    }

    private void initData() {
         intent = getIntent();
        datas = (Map<String, String>) intent.getSerializableExtra("data");

        System.out.println(datas);
        fromID.setText(datas.get("fromID"));
        if (datas.get("now").equals("null")) {
            now.setText("当天送出");
        } else {
            now.setText("是");
        }
        address.setText(datas.get("address"));
        remarks.setText(datas.get("remarks"));
        tel.setText(datas.get("tel"));
        receiver.setText(datas.get("receiver"));
        orderID.setText(datas.get("orderID"));
       bossID.setText(datas.get("storeID"));

    }

    private void initView() {
        back= (ImageView) findViewById(R.id.back);
        receiver = (TextView) findViewById(R.id.receiver);
        fromID = (TextView) findViewById(R.id.fromID);
        address = (TextView) findViewById(R.id.address);
        tel = (TextView) findViewById(R.id.tel);
        now = (TextView) findViewById(R.id.now);
        remarks = (TextView) findViewById(R.id.remarks);
        shipment = (Button) findViewById(R.id.shipment);
        downLoadFiles = (Button) findViewById(R.id.downLoadFiles);
        orderID = (TextView) findViewById(R.id.orderID);
        bossID= (TextView) findViewById(R.id.bossID);
        toChat = (ImageView) findViewById(R.id.toChat);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:{
                intent.putExtra("datas", (Serializable) datas);
                this.setResult(rscode,intent);
                MyPrintOrderDetailActivity.this.finish();
                break;

            }
            case R.id.toChat:{
                Intent intent=new Intent(MyPrintOrderDetailActivity.this,ChatingActivity.class);
                intent.putExtra("chatingUserID",bossID.getText().toString());
                startActivity(intent);
                break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        intent.putExtra("datas", (Serializable) datas);
        this.setResult(rscode,intent);
        MyPrintOrderDetailActivity.this.finish();
    }
}
