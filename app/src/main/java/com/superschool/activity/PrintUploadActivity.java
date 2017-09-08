package com.superschool.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.superschool.R;
import com.superschool.customeLayout.customeFlowLayout.FlowLayout;
import com.superschool.entity.FileInfo;
import com.superschool.function.FileZip;
import com.superschool.function.PickFile;
import com.superschool.http.UploadFileInsertServer;
import com.superschool.sample.FileOperation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class PrintUploadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int FILE_PICK = 1;
    private Button submit;
    private ImageButton selectFile;
    private EditText name;
    private EditText address;
    private EditText tel;
    private RadioButton transferYes;
    private RadioButton transferNo;
    private static HashMap<String, String> customerInfo;
    private EditText mark;
    FlowLayout addFilesLayout;
    List<String> filePath = new ArrayList<String>();
    private static Context context;
    Map<String, String> store;
    private TextView storeName;
    private String appkey = "b2b2aa47bae76270d275879b";
    private String myDisk = "http://xiaohaobucket.oss-cn-shanghai.aliyuncs.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.print_upload);
        context = getApplicationContext();
        Intent intent = getIntent();
        store = (Map<String, String>) intent.getSerializableExtra("storeInfo");
        initView();
        initEvent();

    }

    private void initEvent() {
        selectFile.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    private void initView() {

        submit = (Button) findViewById(R.id.submit);
        selectFile = (ImageButton) findViewById(R.id.selectFile);
        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);
        tel = (EditText) findViewById(R.id.tel);
        transferYes = (RadioButton) findViewById(R.id.transferYes);
        transferNo = (RadioButton) findViewById(R.id.transferNo);
        mark = (EditText) findViewById(R.id.mark);
        addFilesLayout = (FlowLayout) findViewById(R.id.addFiles);
        storeName = (TextView) findViewById(R.id.storeName);
        storeName.setText(store.get("storename"));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectFile: {
                openFileSystem();
                break;
            }
            case R.id.submit: {
                String username = name.getText().toString();
                String addressName = address.getText().toString();
                String phone = tel.getText().toString();
                String isSend = null;
                if (transferNo.isChecked()) {
                    isSend = "no";
                } else isSend = "yes";
                String beizhu = mark.getText().toString();
                Message msg = new Message();

                if (username.isEmpty() || phone.isEmpty() || addressName.isEmpty() || filePath.size() == 0) {


                } else {
                    submit.setText("正在上传...");

                    submit.setEnabled(false);
                }

                Handler handler = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        final Map<String, String> message = (Map<String, String>) msg.obj;
                         message.put("contentType","orderMsg");
                        if (message.get("result").equals("error")) {
                            Toast.makeText(getApplicationContext(), "联系方式，以及文件项均不能为空！", Toast.LENGTH_LONG).show();

                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final cn.jpush.im.android.api.model.Message orderMsg = JMessageClient.createSingleCustomMessage(store.get("storeID"), appkey, message);
                                    //  final cn.jpush.im.android.api.model.Message orderMsg = JMessageClient.createSingleTextMessage(store.get("storeID"), appkey, "简单文本消息");
                                    JMessageClient.sendMessage(orderMsg);
                                    orderMsg.setOnSendCompleteCallback(new BasicCallback() {
                                        @Override
                                        public void gotResult(int i, String s) {
                                            if (i == 0) {
                                                System.out.println("发送成功");
                                            } else {
                                                System.out.println("发送失败");
                                            }
                                        }
                                    });
                                }
                            }).start();


                            Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(PrintUploadActivity.this);
                            builder.setMessage("提交成功,请勿重新提交");

                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PrintUploadActivity.this.finish();
                                }
                            });
                            builder.show();
                        }
                    }
                };
                new Thread(new MyHandler(handler, filePath)).start();
                break;
            }
        }
    }


    class MyHandler implements Runnable {
        Handler mHander;
        List<String> filePath;

        public MyHandler(Handler mHander, List<String> filePath) {
            this.mHander = mHander;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            String username = name.getText().toString();
            String addressName = address.getText().toString();
            String phone = tel.getText().toString();
            String isSend = null;
            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            final String userID = sharedPreferences.getString("userID", null);
            String userIdFromJMsg=null;

            UserInfo myInfo= JMessageClient.getMyInfo();
            userIdFromJMsg=String.valueOf(myInfo.getUserID());
            if (transferNo.isChecked()) {
                isSend = "no";
            } else {
                isSend = "yes";
            }
            String beizhu = mark.getText().toString();
            Message msg = new Message();

            if (username.isEmpty() || phone.isEmpty() || addressName.isEmpty() || filePath.size() == 0) {
                customerInfo.put("result", "error");
                msg.obj = customerInfo;

            } else {

                customerInfo.put("storeID", store.get("storeID"));
                customerInfo.put("orderType", "printOrder");
                customerInfo.put("fromID", userID);
                customerInfo.put("receiver", username);
                customerInfo.put("address", addressName);
                customerInfo.put("tel", phone);
                customerInfo.put("isSend", isSend);
                customerInfo.put("beizhu", beizhu);
                Date date = new Date();
                customerInfo.put("date", date.toString());
                String orderID =userIdFromJMsg+new SimpleDateFormat("yyyyMMddHHmmss").format(date);
                customerInfo.put("orderID",orderID);
                //得到文件后先将文件打包    zip名字：老板的idto用户id+日期
                String filename = store.get("storeID") + "to" + userID + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".zip";
                String zipName = getApplicationContext().getExternalCacheDir() + "/" + filename;
                FileZip fileZip = new FileZip((ArrayList<String>) filePath, zipName);
                try {
                    fileZip.zip();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFilePath(zipName);
                fileInfo.setFilename(store.get("storeID") + "/" + filename);
                FileOperation operation = new FileOperation(getApplicationContext(), fileInfo);
                try {
                    if (operation.fileUpload()) {
                        File file = new File(zipName);
                        file.delete();
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                customerInfo.put("filename", myDisk + "/" + store.get("storeID") + "/" + filename);
                System.out.println(customerInfo);
                String url = "http://ganxiaohao.gicp.net/fileUploadToPrintAction.action";  //请求action

                String result = null;
                try {
                    result = UploadFileInsertServer.upload(customerInfo,url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (result.equals("ok")) {
                    customerInfo.put("result", "ok");
                    msg.obj = customerInfo;
                }
            }
            mHander.sendMessage(msg);

        }
    }






















    private void openFileSystem() {
        Intent contentSelection = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelection.addCategory(Intent.CATEGORY_OPENABLE);//添加种类
        contentSelection.setType("*/*");
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelection);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "选取文件");
        startActivityForResult(chooserIntent, FILE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        customerInfo = new HashMap<String, String>();
        Uri fileUri = data.getData();
        PickFile pick = new PickFile(fileUri, this);
        File file = new File(pick.getImageAbsolutePath());
        String fileFullName = file.getName();
        LinearLayout linearLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.my_file_layout, null);
        System.out.println(file.getName());
        System.out.println(file.getName().length());
        ImageView imageView = (ImageView) linearLayout.getChildAt(0);
        TextView filename = (TextView) linearLayout.getChildAt(1);
        String lastName = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
        System.out.println(lastName);

        if (lastName.equalsIgnoreCase("jpeg") || lastName.equalsIgnoreCase("jpg") || lastName.equalsIgnoreCase("png") || lastName.equalsIgnoreCase("gif")) {
            imageView.setImageResource(R.drawable.image);
        } else {
            imageView.setImageResource(R.drawable.document);
        }
        filename.setText(file.getName());
        addFilesLayout.addView(linearLayout);
        filePath.add(pick.getImageAbsolutePath());
    }
}
