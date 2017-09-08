package com.superschool.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.superschool.R;
import com.superschool.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;

public class ChatingActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView chatintListView;
    private Button sendMsgBtn;
    private EditText msgEdText;
    ChatAdapter chatAdapter;
    List<Map<String, String>> datas;
    static String localUserID;
    Map<String, String> fromChatObj;
    private String appkey = "b2b2aa47bae76270d275879b";
    private String chatingUserID;
    private TextView toChatingUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        Intent intent=getIntent();
        chatingUserID=intent.getStringExtra("chatingUserID");
        SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
        localUserID = user.getString("userID", null);
        initView();
        initEvent();
        addAdapter();
        initReceiver();

        Map<String,String> map=null;
        if(intent.getSerializableExtra("chatMsg")!=null){
            map= (Map<String, String>) intent.getSerializableExtra("chatMsg");
            System.out.println(map);
            datas.add(map);
            chatAdapter.notifyDataSetChanged();
        }
    }

    private void initReceiver() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                fromChatObj = (Map<String, String>) msg.obj;
                datas.add(fromChatObj);
                chatAdapter.notifyDataSetChanged();
            }
        };
        System.out.println("**********************注册广播****************");
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.superschool.chatmsg");
        ChatBroadCast chatReceiver = new ChatBroadCast(handler);
        LocalBroadcastManager.getInstance(ChatingActivity.this).registerReceiver(chatReceiver, filter);
        System.out.println("***************************************");



    }

    private void initView() {
        chatintListView = (ListView) findViewById(R.id.chatList);
        sendMsgBtn = (Button) findViewById(R.id.send);
        msgEdText = (EditText) findViewById(R.id.msgEdText);
        toChatingUser= (TextView) findViewById(R.id.toChatingUser);
        toChatingUser.setText(chatingUserID);
    }

    private void initEvent() {
        sendMsgBtn.setOnClickListener(this);
    }

    private void addAdapter() {
        datas = new ArrayList<Map<String, String>>();
        chatAdapter = new ChatAdapter(datas, ChatingActivity.this, localUserID);
        chatintListView.setAdapter(chatAdapter);
    }


    @Override
    public void onClick(View view) {
        System.out.println("发送");
        String msg = msgEdText.getText().toString();
        if(msg.equals("")){

            Toast.makeText(ChatingActivity.this, "不能发送空消息", Toast.LENGTH_SHORT).show();
            return;

        }
        Map<String, String> map = new HashMap<>();
        map.put("fromID", localUserID);
        map.put("msgContent", msg);
        map.put("toID", chatingUserID);
        map.put("contentType","chatMsg");
        datas.add(map);
        chatAdapter.notifyDataSetChanged();
        cn.jpush.im.android.api.model.Message jmsg=JMessageClient.createSingleCustomMessage(chatingUserID, appkey, map);
        JMessageClient.sendMessage(jmsg);
        msgEdText.setText("");
    }

    class ChatBroadCast extends BroadcastReceiver {
        private Handler handler;

        public ChatBroadCast(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = new Message();
            Map<String, String> map = (Map<String, String>) intent.getSerializableExtra("chatMsg");

            msg.obj = map;
            handler.sendMessage(msg);
        }
    }


}

