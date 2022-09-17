package com.baiyun.xiangchengma.server.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baiyun.xiangchengma.bean.Info;

import java.util.ArrayList;

public class InfoDBOpenHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;      //声明一个AndroidSDK自带的数据库变量db
    public InfoDBOpenHelper(Context context){
        super(context,"db_info",null,1);
        db = getReadableDatabase();      //把数据库设置成可写入状态
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //社区人员健康信息表
        db.execSQL("CREATE TABLE IF NOT EXISTS info("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date DATE," +
                "name TEXT," +
                "idCard TEXT UNIQUE," +
                "phone TEXT," +
                "address TEXT," +
                "ifFever TEXT,"+     //  存入数据库的值为是或否
                "tem TEXT,"+
                "ifTouch TEXT,"+
                "touchName TEXT,"+
                "touchPhone TEXT,"+
                "touchDate DATE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS info");
        onCreate(db);
    }

    //社区人员健康信息表的增查
    public void infoAdd(String rdate,String rname,String ridCard,String rphone,String raddress,String rifFever,String rtem,String rifTouch,String rtouchName,String rtouchPhone,String rtouchDate){
        db.execSQL("INSERT OR REPLACE INTO info(date,name,idCard,phone,address,ifFever,tem,ifTouch,touchName,touchPhone,touchDate)VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{rdate,rname,ridCard,rphone,raddress,rifFever,rtem,rifTouch,rtouchName,rtouchPhone,rtouchDate});
    }

    public ArrayList<Info> getInfoData(){
        ArrayList<Info> list = new ArrayList<Info>();
        Cursor cursor = db.query("info",null,null,null,null,null,"name DESC");
        while(cursor.moveToNext()){
            @SuppressLint("Range") String date=cursor.getString(cursor.getColumnIndex("date"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String idCard = cursor.getString(cursor.getColumnIndex("idCard"));
            @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("phone"));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
            @SuppressLint("Range") String ifFever = cursor.getString(cursor.getColumnIndex("ifFever"));
            @SuppressLint("Range") String tem = cursor.getString(cursor.getColumnIndex("tem"));
            @SuppressLint("Range") String ifTouch = cursor.getString(cursor.getColumnIndex("iftouch"));
            @SuppressLint("Range") String touchName = cursor.getString(cursor.getColumnIndex("touchName"));
            @SuppressLint("Range") String touchPhone = cursor.getString(cursor.getColumnIndex("touchPhone"));
            @SuppressLint("Range")  String touchDate = cursor.getString(cursor.getColumnIndex("touchDate"));
            list.add(new Info(date,name,idCard,phone,address,ifFever,tem,ifTouch,touchName,touchPhone,touchDate));
        }
        return list;
    }
}
