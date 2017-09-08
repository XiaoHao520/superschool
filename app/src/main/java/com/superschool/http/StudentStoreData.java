package com.superschool.http;

import android.content.Context;

import com.superschool.databases.FirstPageDatas;
import com.superschool.databases.InitDatabase;
import com.superschool.entity.XmlToListMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/5/19.
 */

public class StudentStoreData {
    static List<Map<String, String>> storeInfoList;
    private String url = null;
    Map<String, Object> storeInfo = new HashMap<String, Object>();
    private Context context;

    public StudentStoreData(Context context) {
        this.context = context;
    }

    private List<Map<String, String>> getStudentStoreFromServer(String school) throws Exception {
        storeInfoList = new ArrayList<Map<String, String>>();
        url = "http://ganxiaohao.gicp.net/studentStoreAction.action";
        StoreInfoFromServer storeInfo = new StoreInfoFromServer();
        String result = storeInfo.getStringStream(url, school);
        if (result == null) {
            storeInfoList = null;
        } else {
            //组装数据
            XmlToListMap xmlToListMap = new XmlToListMap(result);
            storeInfoList = xmlToListMap.toListMap();
        }
        return storeInfoList;
    }

    private List<Map<String, String>> getStudentStoreFromLocal(String school) {
        InitDatabase initDatabase = new InitDatabase(context);//初始化数据库
        FirstPageDatas firstPageDatas = new FirstPageDatas(initDatabase);//获取first Fragment的数据
        storeInfoList = firstPageDatas.getStudentStoreDatas(school);
        return storeInfoList;
    }

    public List<Map<String, String>> getStudentStoreData(String school) throws Exception {
        InitDatabase initDatabase = new InitDatabase(context);//初始化数据库
        FirstPageDatas firstPageDatas = new FirstPageDatas(initDatabase);//获取first Fragment的数据
        List<Map<String, String>> storeInfoListFromLocal = getStudentStoreFromLocal(school);


        if (storeInfoListFromLocal.size() != 0) {
            storeInfoList = storeInfoListFromLocal;
        } else {
            storeInfoList = getStudentStoreFromServer(school);
            System.out.println("*******************************");
            System.out.println(storeInfoList);
            System.out.println("*******************************");
            if (storeInfoList != null) {
                for (int i = 0; i < storeInfoList.size(); i++) {
                    Map<String, String> map = storeInfoList.get(i);
                    Object data[] = new Object[7];
                    data[0] = map.get("storeID");
                    data[1] = map.get("latitude");
                    data[2] = map.get("longitude");
                    data[3] = map.get("type");
                    data[4] = map.get("school");
                    data[5] = map.get("storename");
                    data[6] = map.get("storeinfo");
                    firstPageDatas.insertOrUpdateStudentStoreData(data[0].toString(), data);
                }
            }

        }
        return storeInfoList;
    }
}
