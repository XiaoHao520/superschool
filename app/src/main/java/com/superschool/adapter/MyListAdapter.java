package com.superschool.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.superschool.R;
import com.superschool.entity.News;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/4/23.
 */

public class MyListAdapter extends BaseAdapter {
    public Activity context;
    public List<News> list;
    public List<Map<String, String>> listNewsMap;
    public Bitmap bitmap;

    public MyListAdapter(Activity context, List<Map<String, String>> listNewsMap, Bitmap bitmap) {
        this.context = context;
        this.listNewsMap = listNewsMap;
        this.bitmap = bitmap;
    }

    @Override
    public int getCount() {
        return listNewsMap.size();
    }

    @Override
    public Object getItem(int i) {
        return listNewsMap.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //初始化数据
        //News news=list.get(i);
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.news, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.titleImg);
        TextView userID = (TextView) itemView.findViewById(R.id.userID);
        TextView date = (TextView) itemView.findViewById(R.id.date);
        TextView newsContent = (TextView) itemView.findViewById(R.id.news_content);

        imageView.setImageBitmap(bitmap);
        userID.setText(listNewsMap.get(i).get("userID"));
        newsContent.setText(listNewsMap.get(i).get("content"));


        try {
            date.setText(getTime(listNewsMap.get(i).get("date")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return itemView;
    }

    private String getTime(String timeStr) throws ParseException {
        //20170503161736
        String time = null;
        Date now = new Date();
        String nowChage = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(now);
        String[] nowToArray = nowChage.split("-");
        Date past = new SimpleDateFormat("yyyyMMddhhmmss").parse(timeStr);
        String pastChange = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(past);
        time = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(past);
        String[] pastToArray = pastChange.split("-");
        final int[] pastStringToInt = new int[pastChange.length()];
        final int[] nowStringToInt = new int[nowToArray.length];
        int year = 0;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int seconds = 0;

        for (int i = 0; i < pastToArray.length; i++) {

            pastStringToInt[i] = Integer.parseInt(pastToArray[i]);
            nowStringToInt[i] = Integer.parseInt(nowToArray[i]);
            switch (i) {
                case 0: {
                    year = pastStringToInt[i] - nowStringToInt[i];
                    break;
                }
                case 1: {
                    month = pastStringToInt[i] - nowStringToInt[i];
                    break;
                }
                case 2: {
                    day = pastStringToInt[i] - nowStringToInt[i];
                    break;
                }
                case 3: {
                    hour = pastStringToInt[i] - nowStringToInt[i];
                    break;
                }
                case 4: {
                    minute = pastStringToInt[i] - nowStringToInt[i];
                    break;
                }
                case 5: {
                    seconds = pastStringToInt[i] - nowStringToInt[i];
                    break;
                }
            }

        }

        return time;
    }
}
