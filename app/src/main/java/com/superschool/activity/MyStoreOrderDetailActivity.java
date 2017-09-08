package com.superschool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.superschool.R;
import com.superschool.http.UpdateOrderStatusOnServer;
import com.superschool.sample.FileOperation;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;


public class MyStoreOrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private String appkey = "b2b2aa47bae76270d275879b";
    private TextView receiver;
    private TextView fromID;
    private TextView address;
    private TextView tel;
    private TextView now;
    private TextView remarks;
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
        setContentView(R.layout.my_store_order_detail);
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
        toChat= (ImageView) findViewById(R.id.toChat);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.downLoadFiles: {
                downLoadFiles.setEnabled(false);
                downLoadFiles.setText("正在下载");
                FileOperation download = new FileOperation(getApplicationContext());

                try {
                    if (download.filesDownload(datas.get("filesName"))) {
                        downLoadFiles.setText("下载成功");
                        //更新数据库订单信息；

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String url="http://ganxiaohao.gicp.net/handleOrderAction.action";
                                String result= null;
                                try {
                                    result = UpdateOrderStatusOnServer.getStringStream(url,"printOrder",datas.get("orderID"),"doing");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                System.out.println("update order'status ok");
                                if(result.equals("ok")){
                                    datas.put("orderstatus","doing");
                                    rscode=ON_CLICK_ITEM_DOING;
                                    System.out.println("update order'status ok");
                                }
                            }
                        }).start();



                        //下载成功后发消息给客户提示他正在处理他的订单
                        Map<String, String> message = new HashMap<>();
                        message.put("contentType", "resultMsg");
                        message.put("resultType", "orderHandling");
                        message.put("storeID", datas.get("storeID"));
                        String targetID=datas.get("fromID");
                        System.out.println(targetID);
                      //  final cn.jpush.im.android.api.model.Message  msg=JMessageClient.createSingleTextMessage(targetID,appkey,"这个为啥没报错");

                        System.out.println("msgContent:"+message);
                        final cn.jpush.im.android.api.model.Message msg = JMessageClient.createSingleCustomMessage(targetID, appkey, message);
                        JMessageClient.sendMessage(msg);
                        msg.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    System.out.println("发送成功");
                                } else {
                                    System.out.println("发送失败");
                                }
                            }
                        });

                    } else {
                        downLoadFiles.setText("下载失败");
                    }
                    downLoadFiles.setEnabled(true);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

            case R.id.back:{
                intent.putExtra("datas", (Serializable) datas);
                this.setResult(rscode,intent);
                MyStoreOrderDetailActivity.this.finish();
                break;
            }
            case R.id.toChat:{
                Intent intent=new Intent(MyStoreOrderDetailActivity.this,ChatingActivity.class);
                intent.putExtra("chatingUserID",fromID.getText().toString());
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        intent.putExtra("datas", (Serializable) datas);
        this.setResult(rscode,intent);
        MyStoreOrderDetailActivity.this.finish();
    }
}
