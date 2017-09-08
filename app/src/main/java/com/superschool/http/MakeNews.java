package com.superschool.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/4/24.
 */

public class MakeNews {
    private String[] imgUrls;
    List<Map<String, String>> fileList;
    Map<String,String> articleMap;

    public MakeNews(Map<String,String> articleMap, String[] imgUrls) {
        this.articleMap=articleMap;
        this.imgUrls = imgUrls;
    }

    public void send() {
        String url = "http://ganxiaohao.gicp.net/newsAction.action";  //请求action
        HashMap<String, String> textMap = new HashMap<String, String>();

        fileList = new ArrayList<Map<String, String>>();

            for (int i = 0; i < imgUrls.length; i++) {
                HashMap<String, String> fileMaps = new HashMap<String, String>();
                fileMaps.put("uploads", imgUrls[i]);
                fileList.add(fileMaps);

            }



        HttpUrlConnectionUtil2.request(url, fileList, (HashMap<String, String>) articleMap);
    }

}
