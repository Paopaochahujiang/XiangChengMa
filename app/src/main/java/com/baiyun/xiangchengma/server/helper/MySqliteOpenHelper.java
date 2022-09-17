package com.baiyun.xiangchengma.server.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 单例模式(构造器必须私有化,并且对外提供函数)
 */
public class MySqliteOpenHelper extends SQLiteOpenHelper {

   //2对外提供函数
    private static SQLiteOpenHelper mInstance;

    public static synchronized  SQLiteOpenHelper getmInstance(Context context){
        if(mInstance == null){
            mInstance = new MySqliteOpenHelper(context,"UseLocation",null,1);//想要数据库升级，修改版本号

        }
        return  mInstance;
    }
    private MySqliteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //创建表 表数据初始化，数据库第一次创建的时候调用，不会重复创建，此函数并不会重复创建
    //初始化使用
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建表:person 表
        //自动增长
        //主键:primary key 必须唯一
        String sql="create table persons(_id integer primary key autoincrement,name text,latitude text,longitude,text)";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
