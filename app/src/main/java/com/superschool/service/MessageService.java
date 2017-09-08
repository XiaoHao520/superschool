package com.superschool.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.superschool.R;
import com.superschool.activity.ChatingActivity;
import com.superschool.activity.PrintUploadActivity;
import com.superschool.databases.InitDatabase;

import java.io.Serializable;
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

/**
 * Created by XIAOHAO on 2017/6/6.
 */

public class MessageService extends Service {
    @Override
    public void onCreate() {
        initOrderInfo();
        JMessageClient.registerEventReceiver(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBinder extends Binder {
        public void startService() {

        }

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
            case custom: {
                //处理自定义消息
                CustomContent customContent = (CustomContent) msg.getContent();
                String msgType = customContent.getStringValue("contentType");

                if (msgType.equals("resultMsg")) {
                    String resultMsg = customContent.getStringValue("resultType");
                    switch (resultMsg) {
                        case "orderHandling": {
                            //订单处理中
                            Notification.Builder builder = new Notification.Builder(this);
                            // Intent intent = new Intent(this, PrintUploadActivity.class);
                            //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                            // builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.drawable.logo);
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
                            builder.setAutoCancel(true);
                            builder.setContentTitle("您的订单我们正在处理中...");
                            builder.setContentText("来自：" + customContent.getStringValue("storeID"));
                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            manager.notify(0, builder.build());
                            break;
                        }
                    }

                } else if (msgType.equals("orderMsg")) {
                    System.out.println("订单消息");
                    String orderType = customContent.getStringValue("orderType");
                    Map<String, String> map = new HashMap<String, String>();
                    int noRead = 0;

                    //存入数据库
                    Notification.Builder builder = new Notification.Builder(this);
                    switch (orderType) {
                        case "printOrder": {
                            System.out.println("--------------------打印订单");
                            //打印的订单
                            String bossID = customContent.getStringValue("storeID");
                            String userID = customContent.getStringValue("fromID");
                            String tel = customContent.getStringValue("tel");
                            String receiver = customContent.getStringValue("receiver");
                            String address = customContent.getStringValue("address");
                            String isSend = customContent.getStringValue("isSend");
                            String mark = customContent.getStringValue("beizhu");
                            //存入数据库
                            InitDatabase initDatabase = new InitDatabase(this);
                            String sql = "insert into printorder values(?,?,?,?,?,?,?,?,?,?)";
                            String data[] = new String[]{bossID, userID, orderType, isSend, address, receiver, tel, mark, "no", "no"};
                            initDatabase.insertData(sql, data);
                            System.out.println("--------------------存入数据");
                            String select = "select * from printorder where read = ?";
                            Cursor cursor = initDatabase.query(select, new String[]{"no"});
                            noRead = cursor.getCount();
                            map.put("noRead", String.valueOf(noRead));
                            System.out.println("未读消息：" + noRead);
                            //发送通知
                            Intent intent = new Intent(this, PrintUploadActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                            builder.setContentIntent(pendingIntent);
                            builder.setSmallIcon(R.drawable.logo);
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
                            builder.setAutoCancel(true);
                            builder.setContentTitle("您收到一条打新的印订单");
                            builder.setContentText("来自：" + userID);
                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            manager.notify(0, builder.build());
                            break;
                        }

                    }

                    //发送广播
                    map.put("orderType", orderType);
                    Intent sendBroadcastIntent = new Intent();
                    sendBroadcastIntent.putExtra("orderInfo", (Serializable) map);
                    sendBroadcastIntent.setAction("com.superschool.UPDATE_UI");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(sendBroadcastIntent);
                    System.out.println("广播 已经发送");

                } else if (msgType.equals("chatMsg")) {
                    Notification.Builder builder = new Notification.Builder(this);
                    //消息来自哪里去哪里
                    Intent intent = new Intent(this, ChatingActivity.class);




                    String fromID=customContent.getStringValue("fromID");
                    String toID=customContent.getStringValue("toID");
                    String msgContent=customContent.getStringValue("msgContent");
                    Map<String,String> map=new HashMap<String, String>();
                    map.put("fromID",fromID);
                    map.put("toID",toID);
                    map.put("msgContent",msgContent);
                    Intent sendBroadcastIntent = new Intent();
                    sendBroadcastIntent.putExtra("chatMsg", (Serializable) map);
                    sendBroadcastIntent.setAction("com.superschool.chatmsg");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(sendBroadcastIntent);
                    System.out.println("广播 已经发送");

                    intent.putExtra("chatingUserID", fromID);
                    intent.putExtra("chatMsg", (Serializable) map);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    builder.setSmallIcon(R.drawable.logo);
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
                    builder.setAutoCancel(true);
                    builder.setContentTitle(fromID);
                    builder.setContentText(msgContent);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(0, builder.build());
                }


                break;
            }
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

    @Override
    public void onDestroy() {
        stopForeground(true);
        Intent intent = new Intent("com.superschool.restart");
        sendBroadcast(intent);
        super.onDestroy();
    }


    private void initOrderInfo() {
        //这里 我首先要判断当前用户是开什么店铺的。
        /*
        * 1.首先去查print表，是否有该用户，并且返回结果
        * 先假设只有打印
        * */
        System.out.println("初始化本地服务");

        Map<String, String> map = new HashMap<String, String>();
        SharedPreferences shared = this.getSharedPreferences("user", MODE_PRIVATE);
        String userID = shared.getString("userID", null);
        InitDatabase database = new InitDatabase(this);
        String sql = "select read from printorder where bossID=? and read=?";
        String[] obj = new String[]{userID, "no"};
        Cursor cursor = database.query(sql, obj);
        if (cursor != null) {
            String noRead = String.valueOf(cursor.getCount());
            map.put("noRead", noRead);
            map.put("orderType", "printOrder");
            Intent sendBroadcastIntent = new Intent();
            sendBroadcastIntent.putExtra("orderInfo", (Serializable) map);
            sendBroadcastIntent.setAction("com.superschool.UPDATE_UI");
            LocalBroadcastManager.getInstance(this).sendBroadcast(sendBroadcastIntent);
            System.out.println("广播 已经发送");
        } else {
            System.out.println("没有开过店");
        }
    }
}
