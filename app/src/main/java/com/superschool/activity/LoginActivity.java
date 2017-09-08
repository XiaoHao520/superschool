package com.superschool.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.superschool.R;
import com.superschool.entity.User;
import com.superschool.init.JMessageSdk;
import com.superschool.services.UserLoginOrRegister;

import java.io.IOException;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView accout;
    private EditText pwd;
    private Button loginBtn;
    private static int rsCode;
    private static final int TO_FIRSTFRAGMENT = 1;
    User user = new User();
    private Button registBtn;
    Context context;

    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Set up the login form.
        context = getApplicationContext();
        initView();
        initEvent();


    }

    public void initView() {
        loginBtn = (Button) this.findViewById(R.id.login_button);
        accout = (AutoCompleteTextView) this.findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.password);
        registBtn = (Button) findViewById(R.id.regist_button);

    }

    public void initEvent() {
        loginBtn.setOnClickListener(this);
        registBtn.setOnClickListener(this);
    }

    public class MyHandler implements Runnable {
        private Handler mHandler;

        public MyHandler(Handler mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public void run() {

            try {
                user.setUserID(accout.getText().toString());
                user.setPassword(pwd.getText().toString());
                user.setDoWhat("login");
                rsCode = new UserLoginOrRegister(context).userLoginOrRegisterService(user);
                System.out.println("发回码：：：：：：：" + rsCode);
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
            case R.id.login_button: {
                User user = new User();
                user.setUserID(accout.getText().toString());
                user.setPassword(pwd.getText().toString());
                JMessageSdk jMessageSdk = new JMessageSdk(user, context);
                jMessageSdk.jMessagelogin();
                Intent intent = this.getIntent();
                intent.putExtra("ok", 1);
                intent.putExtra("userID", accout.getText().toString());
                intent.putExtra("password", pwd.getText().toString());
                LoginActivity.this.setResult(TO_FIRSTFRAGMENT, intent);
                finish();
                break;
            }
            case R.id.regist_button: {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("ok", 0);
            LoginActivity.this.setResult(TO_FIRSTFRAGMENT, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}

