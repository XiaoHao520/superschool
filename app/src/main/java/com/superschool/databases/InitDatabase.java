package com.superschool.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by XIAOHAO on 2017/4/16.
 */

public class InitDatabase extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "mySchool.db";
    Context context;

    public InitDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String userInfo = "CREATE TABLE IF NOT EXISTS myInfo(\n" +
                "  userID VARCHAR(45) NOT NULL,\n" +
                "  username VARCHAR(45) NULL,\n" +
                "  lastLocation VARCHAR(45) NULL,\n" +
                "  lastWether VARCHAR(45) NULL,\n" +
                "  lastLatitude DOUBLE NULL,\n" +
                "  lastLongitude DOUBLE NULL,\n" +
                "  PRIMARY KEY (userID));\n";

        //创建用户记录表
        sqLiteDatabase.execSQL(userInfo);
        String studentStoreSql = "CREATE TABLE IF NOT EXISTS studentstore (\n" +
                "  storeID VARCHAR(45) NOT NULL,\n" +
                "  latitude DOUBLE NOT NULL,\n" +
                "  longitude DOUBLE NOT NULL,\n" +
                "  type VARCHAR(45) NOT NULL,\n" +
                "  school VARCHAR(45) NOT NULL,\n" +
                "  storename VARCHAR(45) NOT NULL,\n" +
                "  storeinfo VARCHAR(45) NOT NULL,\n" +
                "  PRIMARY KEY (storeID));\n";
        //创建学生超市信息表
        sqLiteDatabase.execSQL(studentStoreSql);

        //服务端时 storeID fromID
        String printOrder= "CREATE TABLE IF NOT EXISTS printorder (\n" +
                "  `bossID` VARCHAR(40) NOT NULL,\n" +
                "  `from` VARCHAR(40) NOT NULL,\n" +
                "  `orderType` VARCHAR(45) NOT NULL,\n" +
                "  `send` VARCHAR(45) NOT NULL,\n" +
                "  `to` VARCHAR(45) NOT NULL,\n" +
                "  `receiver` VARCHAR(45) NOT NULL,\n" +
                "  `tel` VARCHAR(45) NOT NULL,\n" +
                "  `mark` VARCHAR(45) NOT NULL,\n" +
                "  `read` VARCHAR(45) NOT NULL,\n" +
                "  `finished` VARCHAR(45) NOT NULL);";

        sqLiteDatabase.execSQL(printOrder) ;
        System.out.println("创建 printorder 表");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor query(String sql,String[] obj) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.rawQuery(sql, obj);
        return cursor;
    }

    public void insertData(String sql, Object[] obj) {
         SQLiteDatabase db = this.getWritableDatabase();
         db.execSQL(sql, obj);
         db.close();
    }

    public void updateData(String sql, Object[] obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql, obj);
        db.close();
    }
    public void deleteData(String table,String where,String[]args){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(table, where, args);
        db.close();
    }

}
