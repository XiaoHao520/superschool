package com.superschool.fregment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.superschool.R;
import com.superschool.activity.LoginActivity;
import com.superschool.activity.NewsActivity;
import com.superschool.adapter.MyListAdapter;
import com.superschool.entity.News;
import com.superschool.entity.XmlToListMap;
import com.superschool.http.DownloadNewsFromServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/4/15.
 */

public class SecondFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView;
    private ArrayList<News> newsList;
    private FloatingActionButton makeNews;
    View view;
    static List<Map<String, String>> listNewsMap;
    private static final int SEND = 1;
    private static final int LOGIN = 2;
    private static final int BACK = 0;
    private static final int CANCEL_LOGIN = 3;
    static MyListAdapter myListAdapter = null;
    static boolean isSend = true;
    private WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initViews(inflater, container);
       // initEvent();
        return view;
    }


    private void initEvent() {
        listView.setOnItemClickListener(this);
        makeNews.setOnClickListener(this);
    }

    private void initViews(LayoutInflater inflater, ViewGroup container) {
        if (view != null) {
            return;
        } else {
            view = inflater.inflate(R.layout.f2_layout, container, false);
            //listView = (ListView) view.findViewById(R.id.testNews);
            mWebView= (WebView) view.findViewById(R.id.web);
            makeNews = (FloatingActionButton) view.findViewById(R.id.makeNews);
            WebSettings ws= mWebView.getSettings();
            ws.setJavaScriptEnabled(true);
           // mWebView.setWebViewClient(new WebViewClient());
            mWebView.loadUrl("http://ganxiaohao.gicp.net/mobile/cards1.html");

        }





    }


    private void insertListViewData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("数据加载中.....").create();
        final AlertDialog dialog = builder.show();
        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //改变ui
                if (msg.obj != null) {
                    listNewsMap = (List<Map<String, String>>) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                    myListAdapter = new MyListAdapter(getActivity(), listNewsMap, bitmap);
                    listView.setAdapter(myListAdapter);
                    dialog.dismiss();
                }
                super.handleMessage(msg);

            }
        };
        builder.setPositiveButton("重新加载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new MyHandler(mHandler)).start();
            }
        });

        new Thread(new MyHandler(mHandler)).start();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.makeNews: {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                String userID = sharedPreferences.getString("userID", null);
                if (userID == null) {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN);
                } else {
                    Intent intent = new Intent(getActivity(), NewsActivity.class);
                    startActivityForResult(intent, SEND);
                }
                break;
            }
        }
    }

    public class MyHandler implements Runnable {
        private Handler mHandler;

        public MyHandler(Handler mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public void run() {
            try {
                String result =null;
                int x=0;
                while (true){
                    result = DownloadNewsFromServer.getStringStream("http://ganxiaohao.gicp.net/downLoadNewsAll.action");
                    x++;
                    if(!result.equals("error")||x>5){
                        break;
                    }
                }
            // String result = HttpUrlConnectionInputStream.getStringStream("http://ganxiaohao.gicp.net/downLoadNewsAll.action");
                XmlToListMap newsXmlToListMap = new XmlToListMap(result);
                List<Map<String, String>> listNewsMap = newsXmlToListMap.toListMap();
                List<Map<String, String>> listNewsMapNew = new ArrayList<Map<String, String>>();
                int count = listNewsMap.size() - 1;
                for (int i = count; 0 < i; i--) {
                    listNewsMapNew.add(listNewsMap.get(i));
                }
                Message message = new Message();
                message.obj = listNewsMapNew;
                if (listNewsMapNew != null) {
                    message.obj = listNewsMapNew;
                } else {
                    message.obj = null;
                }
                mHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == SEND) {
            isSend = true;
            String userID = data.getStringExtra("userID");
            String date = data.getStringExtra("date");
            String content = data.getStringExtra("content");
            //ArrayList<String> imgUrlLists = data.getStringArrayListExtra("imgUrlList");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userID", userID);
            map.put("content", content);
            map.put("date", date);
            listNewsMap.add(0, map);
            myListAdapter.listNewsMap = listNewsMap;
            myListAdapter.notifyDataSetChanged();
        }
        if (resultCode == BACK) {

        }
    }

    @Override
    public void onStart() {
        System.out.println("second--onstart");

        if (myListAdapter == null) {
           // insertListViewData();
        }

        super.onStart();
    }

    @Override
    public void onPause() {
        System.out.println("second--onpause");
        super.onPause();
    }

    @Override
    public void onStop() {
        System.out.println("second--onstop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        System.out.println("second--ondestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        System.out.println("second--onDestroy");
        myListAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {

        System.out.println("second--onResume");
        super.onResume();
    }


}
