package com.superschool.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.superschool.R;
import com.superschool.test.view.RedPointView;

public class TestPointActivity extends AppCompatActivity {
    private LinearLayout testBtn = null;
    private ImageView testImg = null;
    private Button testBtn1 = null;
    private ImageView testImg1 = null;
    private Button testBtn2 = null;
    private ImageView testImg2 = null;
    private Button relativeBtn1 = null;
    private Button relativeBtn2 = null;


    private RedPointView btnPoint3 = null;
    private RedPointView btnPoint4 = null;

    private RedPointView btnPoint = null;
    private RedPointView imgPoint = null;
    private RedPointView btnPoint1 = null;
    private RedPointView imgPoint1 = null;
    private RedPointView btnPoint2 = null;
    private RedPointView imgPoint2= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_point);
        findId();
        initView();
        setListener();
    }
    private void initView() {
        btnPoint = new RedPointView(this, testBtn);
        btnPoint.setContent(0);
        btnPoint.setSizeContent(10);
        btnPoint.setColorContent(Color.BLACK);
        btnPoint.setColorBg(Color.RED);
        btnPoint.setPosition(Gravity.RIGHT, Gravity.TOP);

        imgPoint = new RedPointView(this, testImg);
        imgPoint.setContent(0);
        imgPoint.setSizeContent(10);
        imgPoint.setColorContent(Color.WHITE);
        imgPoint.setColorBg(Color.BLACK);
        imgPoint.setPosition(Gravity.RIGHT, Gravity.TOP);
        btnPoint1 = new RedPointView(this, testBtn1);
        btnPoint1.setContent(1);
        btnPoint1.setSizeContent(13);
        btnPoint1.setColorContent(Color.WHITE);
        btnPoint1.setColorBg(Color.RED);
        btnPoint1.setPosition(Gravity.RIGHT, Gravity.BOTTOM);

        imgPoint1 = new RedPointView(this, testImg1);
        imgPoint1.setContent(1);
        imgPoint1.setSizeContent(13);
        imgPoint1.setColorContent(Color.WHITE);
        imgPoint1.setColorBg(Color.BLACK);
        imgPoint1.setPosition(Gravity.RIGHT, Gravity.BOTTOM);


        btnPoint2 = new RedPointView(this, testBtn2);
        btnPoint2.setContent(2);
        btnPoint2.setSizeContent(16);
        btnPoint2.setColorContent(Color.GREEN);
        btnPoint2.setColorBg(Color.YELLOW);
        btnPoint2.setPosition(Gravity.LEFT, Gravity.TOP);

        imgPoint2 = new RedPointView(this, testImg2);
        imgPoint2.setContent(2);
        imgPoint2.setSizeContent(16);
        imgPoint2.setColorContent(Color.WHITE);
        imgPoint2.setColorBg(Color.RED);
        imgPoint2.setPosition(Gravity.LEFT, Gravity.BOTTOM);

        //注：在relativeLayout下此方案不可行，下面为父view是relativeLayout的两个button添加小红点控件,不可行。。

//		btnPoint3 = new RedPointView(this, relativeBtn1);
//		btnPoint3.setContent(2);
//		btnPoint3.setSizeContent(16);
//		btnPoint3.setColorContent(Color.GREEN);
//		btnPoint3.setColorBg(Color.YELLOW);
//		btnPoint3.setPosition(Gravity.LEFT, Gravity.TOP);

//		btnPoint4 = new RedPointView(this, relativeBtn2);
//		btnPoint4.setContent(2);
//		btnPoint4.setSizeContent(16);
//		btnPoint4.setColorContent(Color.GREEN);
//		btnPoint4.setColorBg(Color.YELLOW);
//		btnPoint4.setPosition(Gravity.LEFT, Gravity.TOP);

    }

    private void setListener() {
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnPoint.show();
                btnPoint1.show();
                btnPoint2.show();
                imgPoint.hide();
                imgPoint1.hide();
                imgPoint2.hide();
            }
        });

        testImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btnPoint.hide();
                btnPoint1.hide();
                btnPoint2.hide();
                imgPoint.show();
                imgPoint1.show();
                imgPoint2.show();
            }
        });

    }

    private void findId() {
        testBtn = (LinearLayout) findViewById(R.id.test_btn);
        testImg = (ImageView) findViewById(R.id.test_img);

        testBtn1 = (Button) findViewById(R.id.test_btn1);
        testImg1 = (ImageView) findViewById(R.id.test_img1);

        testBtn2 = (Button) findViewById(R.id.test_btn2);
        testImg2 = (ImageView) findViewById(R.id.test_img2);


        relativeBtn1 = (Button) findViewById(R.id.relative_btn1);
        relativeBtn2 = (Button) findViewById(R.id.relative_btn2);

    }
}
