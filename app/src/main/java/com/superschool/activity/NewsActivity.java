package com.superschool.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.superschool.R;
import com.superschool.entity.FileInfo;
import com.superschool.function.PickImage;
import com.superschool.http.UploadDataInsertInToServer;
import com.superschool.sample.FileOperation;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private static final int IMAGE_PICK = 1;
    private TextView send;
    private EditText content;
    private TextView back;
    private static final int SEND = 1;
    private static final int BACK = 0;
    private static Intent intent;
    private Button addImg;
    static Bitmap bitmap;

    LinearLayout addImgs;
    ArrayList<String> imgUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_activity);
        imgUrlList = new ArrayList<String>();
        initView();
        initEvent();


    }

    private void initEvent() {
        send.setOnClickListener(this);
        send.setOnTouchListener(this);
        back.setOnClickListener(this);
        addImg.setOnClickListener(this);

    }

    private void initView() {
        send = (TextView) this.findViewById(R.id.send);
        content = (EditText) this.findViewById(R.id.content);
        back = (TextView) findViewById(R.id.back);
        intent = this.getIntent();
        addImg = (Button) findViewById(R.id.addImg);
        addImgs = (LinearLayout) findViewById(R.id.addImgs);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send: {
                send.setTextColor(Color.WHITE);
                send.setTextColor(Color.BLACK);
                final SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                String userID = sharedPreferences.getString("userID", null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String filesDirectory = "http://xiaohaobucket.oss-cn-shanghai.aliyuncs.com/";
                        String url = "http://ganxiaohao.gicp.net/newsAction.action";
                        ArrayList<Map<String, String>> fileList = new ArrayList<Map<String, String>>();
                        HashMap<String, String> articleMap = new HashMap<String, String>();
                        String userID = sharedPreferences.getString("userID", null);
                        String contentStr = content.getText().toString();
                        articleMap.put("userID", userID);
                        articleMap.put("title", "noTitle");
                        articleMap.put("content", contentStr);
                        Date date = new Date();
                        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                        String time = df.format(date);
                        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
                        FileInfo fileInfo;
                        if (imgUrlList != null) {
                            articleMap.put("directory", filesDirectory + userID + "/" + time + "/");
                            for (int i = 0; i < imgUrlList.size(); i++) {
                                fileInfo = new FileInfo();
                                fileInfo.setFilename(i + ".jpg");
                                fileInfo.setFilePath(imgUrlList.get(i));
                                fileInfo.setFilename(userID + "/" + time + "/" + i + ".jpg");
                                fileInfos.add(fileInfo);
                            }
                        }
                        FileOperation fileOperation = new FileOperation(getApplicationContext(), fileInfos);
                        try {
                            fileOperation.filesUpload();
                        } catch (ClientException e) {
                            e.printStackTrace();
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        }
                        String result = null;
                        try {
                            result = UploadDataInsertInToServer.InsertInToServer(articleMap, url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(result);
                    }
                }).start();

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String time = df.format(date);
                intent.putExtra("userID", userID);
                intent.putExtra("content", content.getText().toString());
                intent.putExtra("date", time);
                //intent.putStringArrayListExtra("imgUrlList", imgUrlList);
                this.setResult(SEND, intent);
                this.finish();
                break;
            }
            case R.id.back: {
                this.setResult(BACK, intent);
                this.finish();
                break;
            }
            case R.id.addImg: {
                Intent contentSelection = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelection.addCategory(Intent.CATEGORY_OPENABLE);//添加种类
                contentSelection.setType("image/*");
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelection);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "选取文件");
                startActivityForResult(chooserIntent, IMAGE_PICK);
                break;
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.send: {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    send.setTextColor(Color.WHITE);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    send.setTextColor(Color.BLACK);
                }

                break;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri[] results = null;
        if (requestCode == IMAGE_PICK) {
            ContentResolver resolver = getContentResolver();
            Uri imgUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, imgUri);

                if (bitmap != null) {

                    ImageView newImageView = new ImageView(getApplicationContext());
                    LinearLayout.LayoutParams myParams = new LinearLayout.LayoutParams(250, 250);
                    newImageView.setLayoutParams(myParams);
                    addImgs.addView(newImageView);
                    newImageView.setImageBitmap(bitmap);//这样就能打开一张图片
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            PickImage pickImage = new PickImage(imgUri, this);
            imgUrlList.add(pickImage.getImageAbsolutePath());
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.setResult(BACK, intent);
        NewsActivity.this.finish();
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        System.out.println("newsActivity'back");
        this.setResult(BACK, intent);

        NewsActivity.this.finish();
    }

}
