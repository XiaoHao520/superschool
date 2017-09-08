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
 * Created by XIAOHAO on 2017/6/24.
 */

public class ChatAdapter extends BaseAdapter {
    List<Map<String, String>> datas;
    Activity context;
    String localUser;

    public ChatAdapter(List<Map<String, String>> datas, Activity context, String localUser) {
        this.datas = datas;
        this.context = context;
        this.localUser = localUser;
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
        Map<String, String> map = datas.get(i);
        System.out.println("------------------------------"+map.get("msgContent"));

        System.out.println("==========================="+map);
        System.out.println("null:" + map.isEmpty());
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = null;
        if (!map.isEmpty()) {
            String fromID = map.get("fromID");
            if (fromID.equals(localUser)) {
                itemView = inflater.inflate(R.layout.chat_me_item, null);
            } else {
                itemView = inflater.inflate(R.layout.chat_friend_item, null);
            }
            TextView msgContainer = (TextView) itemView.findViewById(R.id.msgContainer);
           msgContainer.setText(map.get("msgContent"));
        }
        return itemView;
    }
}
