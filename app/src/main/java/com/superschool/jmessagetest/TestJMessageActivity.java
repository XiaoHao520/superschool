package com.superschool.jmessagetest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superschool.R;
import com.superschool.entity.User;
import com.superschool.jmessagesdk.JMessageLogin;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

public class TestJMessageActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_jmessage);
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

/*
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JMessageLogin login1 = new JMessageLogin(system, context);
                login1.getLogin();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                cn.jpush.im.android.api.model.Message message=JMessageClient.createSingleCustomMessage(store.get("storeID"), appkey, message);

                //Message message = JMessageClient.createSingleTextMessage("xiaohao", appkey, "来自系统");
                JMessageClient.sendMessage(message);
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {


                        }
                    }
                });
            }
        });*/


        mConversation = Conversation.createSingleConversation(conversation.getText().toString(), appkey);

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

                        Map<String,String> msgMap=new HashMap<String, String>();
                        msgMap.put("name","自定义消息");



                        String msgStr = msg.getText().toString();
                        final Message jmsg=JMessageClient.createSingleCustomMessage(conversation.getText().toString(), appkey, msgMap);

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

    public void onEvent(OfflineMessageEvent event) {
        //获取事件发生的会话对象
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
        for (int i = 0; i < newMessageList.size(); i++) {
            Message msg = newMessageList.get(i);

        }
    }


    /**
     * 如果在JMessageClient.init时启用了消息漫游功能，则每当一个会话的漫游消息同步完成时
     * sdk会发送此事件通知上层。
     **/
    public void onEvent(ConversationRefreshEvent event) {
        //获取事件发生的会话对象
        Conversation conversation = event.getConversation();
        //获取事件发生的原因，对于漫游完成触发的事件，此处的reason应该是
        //MSG_ROAMING_COMPLETE
        ConversationRefreshEvent.Reason reason = event.getReason();
        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到ConversationRefreshEvent事件,待刷新的会话是%s.\n", conversation.getTargetId()));
        System.out.println("事件发生的原因 : " + reason);
    }


    public void onEvent(final MessageEvent event) {
        final Message msg = event.getMessage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                System.out.println("调用事件处理");

                System.out.println(msg.toString());

                switch (msg.getContentType()) {
                    case text:
                        //处理文字消息
                        TextContent textContent = (TextContent) msg.getContent();
                        System.out.println("------------------->" + textContent.getText());
                        break;
                    case image:
                        //处理图片消息
                        ImageContent imageContent = (ImageContent) msg.getContent();
                        imageContent.getLocalPath();//图片本地地址
                        imageContent.getLocalThumbnailPath();//图片对应缩略图的本地地址
                        break;
                    case voice:
                        //处理语音消息
                        VoiceContent voiceContent = (VoiceContent) msg.getContent();
                        voiceContent.getLocalPath();//语音文件本地地址
                        voiceContent.getDuration();//语音文件时长
                        break;
                    case custom:
                        //处理自定义消息
                        CustomContent customContent = (CustomContent) msg.getContent();
                        customContent.getNumberValue("custom_num"); //获取自定义的值
                        customContent.getBooleanValue("custom_boolean");
                        String content= customContent.getStringValue("name");
                        Map<String,String> map=customContent.getStringExtras();
                        System.out.println("===================================="+content);
                        break;
                    case eventNotification:
                        //处理事件提醒消息
                        EventNotificationContent eventNotificationContent = (EventNotificationContent) msg.getContent();
                        switch (eventNotificationContent.getEventNotificationType()) {
                            case group_member_added:
                                //群成员加群事件
                                break;
                            case group_member_removed:
                                //群成员被踢事件
                                break;
                            case group_member_exit:
                                //群成员退群事件
                                break;
                        }
                        break;
                }
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
        JMessageClient.unRegisterEventReceiver(this);
    }
}




