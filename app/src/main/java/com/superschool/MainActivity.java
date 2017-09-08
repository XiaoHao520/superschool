package com.superschool;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.superschool.Data.LocationData;
import com.superschool.adapter.MyFragmentAdapter;
import com.superschool.fregment.FirstFragment;
import com.superschool.fregment.SecondFragment;
import com.superschool.fregment.ThirdFragment;
import com.superschool.service.InitLocalService;
import com.superschool.service.MessageService;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements View.OnScrollChangeListener {

    private ViewPager myViewPager;
    private TextView f1Text;
    private TextView f2Text;
    private TextView f3Text;
    ImageView bt0;
    ImageView bt1;
    ImageView bt2;
    List<Fragment> fragments;
    Handler handler;
    FragmentManager fragmentManager;
    FirstFragment firstFragment;
    SecondFragment secondFragment;
    ThirdFragment thirdFragment;
    MyFragmentAdapter myFragmentAdapter;
    ThirdFragment.OrderReceiver orderReceiver;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        ActivityManager manager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
      List<ActivityManager.RunningAppProcessInfo> processInfos=manager.getRunningAppProcesses();
        if(processInfos.size()>0){
            System.out.println("000000000000000000000000000"+processInfos.get(0));
        }

        JMessageClient.init(this);
        //当所有东西加载完后在开启服务

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    setContentView(R.layout.main);
                    initView();
                    initViewPager();
                    initEvent();
                    initService();
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                handler.sendEmptyMessage(1);
            }
        };
        new Thread(runnable).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
                String user = sharedPreferences.getString("userID", "");
                if (user.isEmpty()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userID", "vistor");
                    editor.commit();
                    new LocationData(getApplicationContext(), "vistor");
                } else {
                    new LocationData(getApplicationContext(), user);
                }
            }
        }).start();

    }

    private void initService() {
        System.out.println("******************正在启动服务*********************");
        Intent startMsgService=new Intent(this, MessageService.class);
        startService(startMsgService);
        Intent startLocalService=new Intent(this, InitLocalService.class);
        startService(startLocalService);
        System.out.println("***************************************");
    }

    private void initViewPager() {
        fragmentManager = getSupportFragmentManager();
        fragments = new ArrayList<Fragment>();
        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();
        thirdFragment = new ThirdFragment();
        fragments.add(firstFragment);
        fragments.add(secondFragment);
        fragments.add(thirdFragment);
        myFragmentAdapter = new MyFragmentAdapter(fragmentManager, fragments);
        myViewPager.setOffscreenPageLimit(3);
        myViewPager.setAdapter(myFragmentAdapter);
        myViewPager.setCurrentItem(0);
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0: {
                        bt0.setImageResource(R.drawable.schoolactived);
                        f1Text.setTextColor(0xffFE4080);
                        f2Text.setTextColor(0xff717171);
                        f3Text.setTextColor(0xff717171);
                        bt1.setImageResource(R.drawable.news);
                        bt2.setImageResource(R.drawable.user);
                        break;
                    }
                    case 1: {
                        f1Text.setTextColor(0xff717171);
                        f2Text.setTextColor(0xffFE4080);
                        f3Text.setTextColor(0xff717171);
                        bt0.setImageResource(R.drawable.schoolnoactived);
                        bt1.setImageResource(R.drawable.newsactived);
                        bt2.setImageResource(R.drawable.user);
                        break;
                    }
                    case 2: {
                        f1Text.setTextColor(0xff717171);
                        f2Text.setTextColor(0xff717171);
                        f3Text.setTextColor(0xffFE4080);
                        bt2.setImageResource(R.drawable.useractived);
                        bt0.setImageResource(R.drawable.schoolnoactived);
                        bt1.setImageResource(R.drawable.news);

                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initEvent() {
        f1Text.setOnClickListener(new MyListener(0));
        f2Text.setOnClickListener(new MyListener(1));
        f3Text.setOnClickListener(new MyListener(2));
        bt0.setOnClickListener(new MyListener(0));
        bt1.setOnClickListener(new MyListener(1));
        bt2.setOnClickListener(new MyListener(2));
    }

    private void initView() {

        myViewPager = (ViewPager) findViewById(R.id.viewPager);
        f1Text = (TextView) findViewById(R.id.f1);
        f2Text = (TextView) findViewById(R.id.f2);
        f3Text = (TextView) findViewById(R.id.f3);
        bt0 = (ImageView) findViewById(R.id.schoolImg);
        bt1 = (ImageView) findViewById(R.id.newsImg);
        bt2 = (ImageView) findViewById(R.id.meImg);

    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {

    }

    class MyListener implements View.OnClickListener {
        private int index;

        public MyListener(int index) {
            this.index = index;

        }

        @Override
        public void onClick(View view) {
            myViewPager.setCurrentItem(this.index);

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.logout();
    }
}
