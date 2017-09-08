package com.superschool.jmessagetest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superschool.R;
import com.superschool.entity.User;
import com.superschool.jmessagesdk.JMessageLogin;
import com.superschool.service.MessageService;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

public class JMessageActivity extends AppCompatActivity {
    private Button send;
    private EditText msg;
    User user;
    EditText conversation;
    private String appkey = "b2b2aa47bae76270d275879b";
    Conversation mConversation;
    private Button addFriends;
    private Button allowAdd;
    private String friendName;
    Context context;
    private Button enterSingleConversation;
    EditText username;
    EditText password;
    User system;
    private MessageService.MyBinder myBinder;

    private ServiceConnection serviceConnction = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myBinder = (MessageService.MyBinder) iBinder;
            myBinder.startService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jmessage);
        context = getApplicationContext();
        JMessageClient.init(context);
        JMessageClient.setDebugMode(true);
        JMessageClient.registerEventReceiver(this);
        user = new User();
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        conversation = (EditText) findViewById(R.id.conversation);
        Button login = (Button) findViewById(R.id.login);
        addFriends = (Button) findViewById(R.id.addFriends);
        allowAdd = (Button) findViewById(R.id.allowAdd);
        enterSingleConversation = (Button) findViewById(R.id.enterSingleConversation);

        msg = (EditText) findViewById(R.id.msg);
        send = (Button) findViewById(R.id.send);
        system = new User();
        system.setUserID("admin");
        system.setPassword("admin");
        mConversation = Conversation.createSingleConversation(conversation.getText().toString(), appkey);

        System.out.println("---------------------------");
        Intent intent = new Intent(this, MessageService.class);
        startService(intent);
        // bindService(intent,serviceConnction,BIND_AUTO_CREATE);
        System.out.println("---------------------------");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setUserID(username.getText().toString());

                user.setPassword(password.getText().toString());

                JMessageLogin login = new JMessageLogin(user, context);
                login.login();
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Map<String, String> msgMap = new HashMap<String, String>();
                        msgMap.put("name", "自定义消息");


                        String msgStr = msg.getText().toString();
                        final Message jmsg = JMessageClient.createSingleCustomMessage(conversation.getText().toString(), appkey, msgMap);

                        //  final Message jmsg = JMessageClient.createSingleTextMessage(conversation.getText().toString(), appkey, msgStr);
                        JMessageClient.sendMessage(jmsg);
                        jmsg.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

    }


    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

}
