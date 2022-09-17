package com.baiyun.xiangchengma.server.helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    //数据库名称
    private static final String DB_NAME = "user.db";
    //数据库版本
    private static final int version = 1;
    //数据库用户表创造语句
    private static final String SQL_CREATE = "create table user_info(_id integer primary key autoincrement,name text)";
    //数据库用户位置信息表创建语句
    private static final String SQL_CREATELOCATIONINFO="create table location_info(ordinal integer primary key autoincrement,_id integer ,name text,latitude text,longitude text,location text)";
    //数据库用户信息表删除语句
    private static final String SQL_DROP = "drop table if exists user_info";
    //数据库用户位置信息表删除语句
    private static final String SQL_DROPLOCATIONINFO = "drop table if exists location_info";
    public DBHelper(Context context) {

        //创造数据库
        super(context, DB_NAME, null, version);
    }

    /**
     * 创建表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建用户表
        db.execSQL(SQL_CREATE);
        //创建用户信息表
        db.execSQL(SQL_CREATELOCATIONINFO);

        Log.e("打印", "数据库已创建 ");
    }

    /**
     * 更新数据库表
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //删除表
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_DROPLOCATIONINFO);
        //重新创造表
        db.execSQL(SQL_CREATELOCATIONINFO);
        db.execSQL(SQL_CREATE);
    }
}
