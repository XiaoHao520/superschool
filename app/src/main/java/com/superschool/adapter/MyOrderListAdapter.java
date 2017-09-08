package com.superschool.adapter;

import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.superschool.R;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/6/14.
 */

public class MyOrderListAdapter extends BaseAdapter {
    List<Map<String, String>> datas;
    Activity context;

    public MyOrderListAdapter(List<Map<String, String>> datas, Activity context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.my_order_item, null);
        TextView orderId = (TextView) itemView.findViewById(R.id.orderId);

        TextView time = (TextView) itemView.findViewById(R.id.time);
        TextView status = (TextView) itemView.findViewById(R.id.status);
        Map<String, String> map = datas.get(i);

        orderId.setText(map.get("orderID"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date str = null;
        try {
            str = sdf.parse(map.get("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println();

        time.setText(sdf2.format(str).toString());
        switch (map.get("orderstatus")) {
            case "incomplete": {
                status.setText("未完成");
                break;
            }
            case "doing":{
                status.setText("处理中");
                break;
            }
        }

        return itemView;
    }
}
