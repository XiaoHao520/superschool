package com.superschool.databases;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAOHAO on 2017/4/16.
 */

public class FirstPageDatas {
    private Map<String, String> map;
    private InitDatabase initDatabase;
    private static Object[] updateData;

    public FirstPageDatas(InitDatabase initDatabase) {
        this.initDatabase = initDatabase;
        SQLiteDatabase sqLiteDatabase=initDatabase.getWritableDatabase();
        //initDatabase.onUpgrade(sqLiteDatabase,1,1);
    }

    //针对用户的操作
    public boolean insertOrUpdateUserData(String userID, Object[] data) {

        String sql = null;
        if (userIsExists(userID)) {
            updateData = new Object[5];

            for (int i = 0; i < data.length - 1; i++) {
                updateData[i] = data[i + 1];
            }

            sql = "update  myInfo set username=?,lastLocation=?,lastWether=?,lastLatitude=?,lastLongitude=? where userID='" + userID + "'";
            initDatabase.updateData(sql, updateData);


            return true;
        } else {
            sql = "insert into myInfo values(?,?,?,?,?,?)";
            initDatabase.insertData(sql, data);

            return true;
        }


    }

    //针对学生超市的操作
    public boolean insertOrUpdateStudentStoreData(String storeID, Object[] data) {

        if (!storeIsExists(storeID)) {
            String sql = "insert into studentstore values(?,?,?,?,?,?,?)";
            initDatabase.updateData(sql, data);
            System.out.println("--------------------插入成功！");
            return true;
        }else {
            System.out.println("已经存在不用插入.....");
        }
        return false;
    }

    public List<Map<String,String>> getStudentStoreDatas(String school){

        List<Map<String,String>>storeList=new ArrayList<Map<String, String>>();
        String sql="select * from studentstore where school='"+school+"'";
        Cursor cursor=initDatabase.query(sql,null);
        while(cursor.moveToNext()){
            Map<String,String>  map = new HashMap<String, String>();
            map.put("storeID",cursor.getString(cursor.getColumnIndex("storeID")));
            map.put("latitude",String.valueOf(cursor.getDouble(cursor.getColumnIndex("latitude"))));
            map.put("longitude",String.valueOf(cursor.getDouble(cursor.getColumnIndex("longitude"))));
            map.put("type",cursor.getString(cursor.getColumnIndex("type")));
            map.put("storename",cursor.getString(cursor.getColumnIndex("storename")));
            map.put("storeinfo",cursor.getString(cursor.getColumnIndex("storeinfo")));
            storeList.add(map);
        }
        return storeList;
    }

    public Map<String, String> getUserDatas(String userID) {

        map = new HashMap<String, String>();
        String sql = "select *from myInfo where userID='" + userID + "'";
        Cursor cursor = initDatabase.query(sql,null);
        while (cursor.moveToNext()) {
            map.put("userID", userID);
            map.put("username", cursor.getString(cursor.getColumnIndex("username")));
            map.put("lastLocation", cursor.getString(cursor.getColumnIndex("lastLocation")));
            map.put("lastLatitude", String.valueOf(cursor.getDouble(cursor.getColumnIndex("lastLatitude"))));
            map.put("lastLongitude", String.valueOf(cursor.getDouble(cursor.getColumnIndex("lastLongitude"))));
            map.put("lastWether", String.valueOf(cursor.getString(cursor.getColumnIndex("lastWether"))));
        }
        return map;
    }

    private boolean userIsExists(String userID) {
        String userIsExists = "select userID from myInfo where userID='" + userID + "'";//查看用户是否存在。
        Cursor findUser = initDatabase.query(userIsExists,null);
        if (findUser.moveToNext()) {
            if (findUser.getCount() > 0) {
                return true;

            }
        }
        return false;
    }

    private boolean storeIsExists(String storeID) {
        String storeIsExists = "select storeID from studentstore where storeID=?";
        String[] data=new String[]{storeID};
        Cursor findStore = initDatabase.query(storeIsExists,data);
        if (findStore.moveToNext()) {
            if (findStore.getCount() > 0) {
                return true;
            }
        }
        return false;
    }
}
