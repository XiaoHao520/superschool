package com.superschool.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.superschool.R;
import com.superschool.adapter.MyOrderListAdapter;
import com.superschool.entity.XmlToListMap;
import com.superschool.http.QueryMyPersonalOrdersOnServer;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MyOrderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView myOrderListView;
   private static List<Map<String, String>> orderListMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_order);
        initView();
        initData();
        initEvent();

    }

    private void initEvent() {
        myOrderListView.setOnItemClickListener(this);
    }

    private void initView() {
        myOrderListView = (ListView) findViewById(R.id.myOrderListView);
        //MyOrderListAdapter myOrderAdapter=new MyOrderListAdapter()
    }

    private void initData() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MyOrderActivity.this);

        final AlertDialog dialog = builder.show();
        class MyHandler implements Runnable {
            Handler handler;
            String url;
            String fromID;

            public MyHandler(Handler handler, String url, String fromID) {
                this.handler = handler;
                this.url = url;
                this.fromID = fromID;
            }

            @Override
            public void run() {
                //下载订单信息
                String resultXML = null;
                try {
                    resultXML = QueryMyPersonalOrdersOnServer.getStringStream(url, fromID);
                    System.out.println(resultXML);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.obj = resultXML;
                handler.sendMessage(msg);
            }
        }


        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //获取订单信息;
                String resultXml = (String) msg.obj;
                if (resultXml != null) {

                    XmlToListMap xmlToListMap = new XmlToListMap(resultXml);
                    try {
                        orderListMap = xmlToListMap.toListMap();
                        if (orderListMap.size() > 0) {
                            MyOrderListAdapter adapter = new MyOrderListAdapter(orderListMap, MyOrderActivity.this);
                            myOrderListView.setAdapter(adapter);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            builder.setMessage("暂无订单信息").create().show();
                        }

                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        System.out.println(userID + "----------------------");
        String url = "http://ganxiaohao.gicp.net/getMyOrderAction.action";
        new Thread(new MyHandler(handler, url, userID)).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent=new Intent();
        intent.setClass(MyOrderActivity.this,MyPrintOrderDetailActivity.class);
        intent.putExtra("data", (Serializable) orderListMap.get(i));
        startActivity(intent);
    }
}
