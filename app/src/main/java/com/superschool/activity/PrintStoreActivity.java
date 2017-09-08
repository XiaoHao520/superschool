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
import android.widget.ImageView;
import android.widget.ListView;

import com.superschool.R;
import com.superschool.adapter.OrderListAdapter;
import com.superschool.entity.XmlToListMap;
import com.superschool.http.QueryOrdersOnServer;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PrintStoreActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView orderList;
    List<Map<String, String>> orderListMap;
    OrderListAdapter orderListAdapter;
    List<Map<String, String>> datas;
    private ImageView back;
    private static final int ON_CLICK_ITEM_DOING = 565;
    private static final int ON_CLICK_DO_NOTHING = 44;
    private static final int ON_CLICK_SEND = 685;//发货
    private static final int ON_ITEM_CLICK = 564;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_store);
        initView();
        downLoadOrderData();
        initEvent();


    }

    private void initEvent() {
        orderList.setOnItemClickListener(this);
        back.setOnClickListener(this);

    }

    private void initDatas(List<Map<String, String>> datas) {
        this.datas = datas;
        orderListAdapter = new OrderListAdapter(this.datas, PrintStoreActivity.this);
        orderList.setAdapter(orderListAdapter);
    }

    private void initView() {
        orderList = (ListView) findViewById(R.id.orderList);
        back = (ImageView) findViewById(R.id.back);

    }

    public boolean downLoadOrderData() {
        String url = "http://ganxiaohao.gicp.net/downPrintOrderAction.action";
        final AlertDialog.Builder builder = new AlertDialog.Builder(PrintStoreActivity.this);
        builder.setMessage("数据加载").create();
        final AlertDialog dialog = builder.show();
        class MyHandler implements Runnable {
            Handler handler;
            String url;
            String storeID;

            public MyHandler(Handler handler, String url, String storeID) {
                this.handler = handler;
                this.url = url;
                this.storeID = storeID;
            }

            @Override
            public void run() {
                //下载订单信息
                String resultXML = null;
                try {
                    // resultXML = QueryOrdersOnServer.getStringStream(url, storeID);
                    while (true) {
                        resultXML = QueryOrdersOnServer.getStringStream(url, storeID);
                        if (!resultXML.equals("error")) {
                            break;
                        }

                    }
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
                            System.out.println(">0");
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            builder.setMessage("暂无订单信息").create().show();
                        }

                        initDatas(orderListMap);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        System.out.println(userID + "----------------------");
        new Thread(new MyHandler(handler, url, userID)).start();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //    打开之后出来是否改变订单状态

        /*
        * 打开之后出来是否改变订单状态
        * requestCode=1
        * */
        Intent intent = new Intent();
        intent.setClass(PrintStoreActivity.this, MyStoreOrderDetailActivity.class);
        intent.putExtra("data", (Serializable) orderListMap.get(i));
        intent.putExtra("item", i);
        startActivityForResult(intent, ON_ITEM_CLICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ON_ITEM_CLICK: {
                if (resultCode == ON_CLICK_ITEM_DOING) {
                    Map<String, String> map = (Map<String, String>) data.getSerializableExtra("datas");
                    int i = data.getIntExtra("item", 0);
                    datas.set(i, map);
                    orderListAdapter.notifyDataSetChanged();
                } else if (resultCode == ON_CLICK_DO_NOTHING) {
                    //啥也不改变

                    System.out.println("啥也没干");
                } else if (resultCode == ON_CLICK_SEND) {
                    Map<String, String> map = (Map<String, String>) data.getSerializableExtra("datas");
                    int i = data.getIntExtra("item", 0);
                    datas.set(i, map);
                    orderListAdapter.notifyDataSetChanged();
                }

                break;
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:{
                PrintStoreActivity.this.finish();
            }
        }
    }
}
