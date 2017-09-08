package com.superschool.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.superschool.R;

import java.util.List;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/6/14.
 */

public class OrderListAdapter extends BaseAdapter {
    List<Map<String, String>> datas;
    Activity context;

    public OrderListAdapter(List<Map<String, String>> datas, Activity context) {
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.order_item, null);
        TextView orderId = (TextView) itemView.findViewById(R.id.orderId);

        TextView from = (TextView) itemView.findViewById(R.id.from);
        TextView finished = (TextView) itemView.findViewById(R.id.finished);
        Map<String, String> map = datas.get(i);

        orderId.setText(map.get("orderID"));
        from.setText(map.get("fromID"));
        switch (map.get("orderstatus")){
            case "incomplete":{
                finished.setText("未完成");
                break;
            }
            case "doing":{
                finished.setText("处理中");
                break;
            }
        }

        return itemView;
    }
}
