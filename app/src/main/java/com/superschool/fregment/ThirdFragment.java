package com.superschool.fregment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superschool.R;
import com.superschool.activity.MyOrderActivity;
import com.superschool.activity.PrintStoreActivity;
import com.superschool.activity.ToBeBossActivity;
import com.superschool.databases.InitDatabase;
import com.superschool.test.view.RedPointView;

import java.util.Map;

/**
 * Created by XIAOHAO on 2017/4/15.
 */

public class ThirdFragment extends Fragment implements View.OnClickListener {
    private LinearLayout toBeBoss;
    private LinearLayout myStore;
    private LinearLayout myOrder;
    private static final int TO_BE_BOSS = 1;
    OrderReceiver orderReceiver;
    RedPointView redPointView;
    private static final int MY_STORE_ORDER = 2;
    private TextView userIDText;
    String userID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f3_layout, container, false);
        SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        userID = preferences.getString("userID", null);
        initView(view);

        initEvent();

        redPointView = new RedPointView(getContext(), myStore);
        redPointView.setSizeContent(15);
        redPointView.setColorContent(Color.WHITE);
        redPointView.setColorBg(Color.RED);
        redPointView.setPosition(Gravity.RIGHT, Gravity.BOTTOM);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Map<String, String> orderInfo = (Map<String, String>) msg.obj;

                switch (orderInfo.get("orderType")) {
                    case "printOrder": {
                        if (Integer.parseInt(orderInfo.get("noRead")) == 0) {
                            redPointView.hide();
                        } else {
                            redPointView.setContent(Integer.parseInt(orderInfo.get("noRead")));
                            redPointView.show();
                        }
                        break;
                    }
                }
            }
        };
        System.out.println("**********************注册广播****************");
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.superschool.UPDATE_UI");
        orderReceiver = new OrderReceiver(handler);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(orderReceiver, filter);
        System.out.println("***************************************");
        return view;
    }


    private void initView(View view) {
        toBeBoss = (LinearLayout) view.findViewById(R.id.toBeBoss);
        myStore = (LinearLayout) view.findViewById(R.id.myStore);
        myOrder = (LinearLayout) view.findViewById(R.id.myOrder);
        userIDText= (TextView) view.findViewById(R.id.userIDText);
        if(userID==null){
            userIDText.setText("请登录");
        }else {
            userIDText.setText(userID);
        }
    }

    private void initEvent() {

        toBeBoss.setOnClickListener(this);
        myOrder.setOnClickListener(this);
        myStore.setOnClickListener(this);
    }

    private void getData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myOrder: {//order
                String storeType = null;
               if(userID==null){
                   System.out.println("请先登录");
               }else {
                   Intent intent=new Intent();
                   intent.setClass(getActivity(), MyOrderActivity.class);
                   startActivity(intent);
               }

                break;
            }
            case R.id.myStore: {
                // redPointView.hide();

                String storeType = null;
                SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                String userID = preferences.getString("userID", null);
                InitDatabase initDatabase = new InitDatabase(getActivity());
                String sql = "select type from studentstore where storeID=?";
                String[] data = new String[]{userID};
                Cursor cursor = initDatabase.query(sql, data);

                while (cursor.moveToNext()) {
                    System.out.println(userID);
                    storeType = cursor.getString(cursor.getColumnIndex("type"));
                }

                switch (storeType) {
                    case "print": {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), PrintStoreActivity.class);
                        startActivityForResult(intent, MY_STORE_ORDER);
                        break;
                    }
                    default: {

                    }
                }


                break;
            }
            case R.id.toBeBoss: {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ToBeBossActivity.class);
                startActivityForResult(intent, TO_BE_BOSS);
                break;
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

        }

    }


    //定义一个广播接收器
    public class OrderReceiver extends BroadcastReceiver {

        private Handler handler;

        public OrderReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = new Message();
            Map<String, String> map = (Map<String, String>) intent.getSerializableExtra("orderInfo");
            msg.obj = map;
            handler.sendMessage(msg);
        }

    }

}
