package com.superschool.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superschool.R;
import com.superschool.entity.User;
import com.superschool.services.UserLoginOrRegister;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerbtn;
    private EditText email;
    private EditText pwd1;
    private EditText pwd2;
    private EditText tel;
    private static int rsCode = 0;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        context=getApplicationContext();
        initView();
        initEvent();
    }

    private void initEvent() {
        registerbtn.setOnClickListener(this);

    }

    private void initView() {
        registerbtn = (Button) this.findViewById(R.id.register);
        email = (EditText) this.findViewById(R.id.email);
        tel = (EditText) this.findViewById(R.id.tel);
        pwd1 = (EditText) findViewById(R.id.pwd1);
        pwd2 = (EditText) findViewById(R.id.pwd2);
    }

    public class MyHandler implements Runnable {
        Handler mHandler;

        public MyHandler(Handler mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public void run() {
            User user = new User();
            user.setDoWhat("register");
            user.setEmail(email.getText().toString());
            user.setTel(tel.getText().toString());
            user.setUserID(email.getText().toString());
            if (pwd1.getText().toString().equals(pwd2.getText().toString())) {
                user.setPassword(pwd1.getText().toString());
            } else {
                System.out.println("两次输入的密码不一致！");
            }
            try {

                rsCode = new UserLoginOrRegister(context).userLoginOrRegisterService(user);
                System.out.println("rsCode="+rsCode);
                if(rsCode==200){
                    System.out.println("执行注册成功");

                }
                System.out.println("执行到MyHandle'run");
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = new Message();
            message.obj = rsCode;
            mHandler.sendMessage(message);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register: {
                Toast.makeText(getApplicationContext(), "注册界面", Toast.LENGTH_LONG).show();
                Handler mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        String str = String.valueOf(msg.obj);
                        rsCode = Integer.parseInt(str);
                        if (rsCode == 200) {

                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();


                        } else if (rsCode == 404) {
                            Toast.makeText(getApplicationContext(), "注册失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                new Thread(new MyHandler(mHandler)).start();
                break;
            }
        }
    }
}
