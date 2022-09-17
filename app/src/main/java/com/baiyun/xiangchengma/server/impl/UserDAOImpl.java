package com.baiyun.xiangchengma.server.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.baiyun.xiangchengma.server.helper.DBHelper;
import com.baiyun.xiangchengma.server.UserDAO;
import com.baiyun.xiangchengma.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据访问接口实现
 * Created by wangbai on 2022/9/9.
 */

public class UserDAOImpl implements UserDAO {


    private DBHelper mHelpter = null;

    public UserDAOImpl(Context context) {

        mHelpter = new DBHelper(context);

        //database文件夹的创建靠这句话
        SQLiteDatabase database = mHelpter.getReadableDatabase();
    }

    @Override
    public void insertUser(User user) {

        //写入数据库
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("insert into user_info(name) values(?)",
                    new Object[]{user.getName()});
        }

        db.close();

    }

    @Override
    public void deleteUser(Integer id) {
        //写入数据库
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        db.execSQL("delete from user_info where _id = ? ",
                new Object[]{id});
        db.close();
    }

    @Override
    public void  updateUser(Integer id,double latitude,double longitude) {
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        if(db.isOpen()) { db.execSQL("update user_info set latitude=?,longitude=? where  _id=?",
                new Object[]{latitude,longitude,id});
            Log.d("update", "数据已更新");
        }
        db.close();
    }


    /**
     * 获得当前所有的用户信息
     * @return
     */
    @SuppressLint("Range")
    @Override
    public List<User> getAllUser() {
        SQLiteDatabase db = mHelpter.getWritableDatabase();

        List<User> list = new ArrayList<User>();

        Cursor c = db.rawQuery("select * from user_info ", null);
        while (c.moveToNext()) {

            User user = new User();
            user.setId(c.getInt(c.getColumnIndex("_id")));
            user.setName(c.getString(c.getColumnIndex("name")));


            list.add(user);
        }
        c.close();
        db.close();

        return list;
    }
    @SuppressLint("Range")
    @Override
    public Map<String,User> getUser() {
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        User user = new User();
        //设置数组存储结果
        HashMap<String,User> map =new HashMap<>();
        Cursor cursor = db.rawQuery("select * from user_info where _id = 1 ",null);
        while (cursor.moveToNext()){
             {
                @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex("_id"));

                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));

//                Double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
//
//                 Double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

                 user.setId(_id);
                 user.setName(name);


                 map.put("user",user);

                Log.d("UserInfo", "query: _id" + _id + " name:" + name );

            }

        }

        cursor.close();
        db.close();
        return map;
    }

    @Override
    public boolean isExists(String name) {
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        List<User> list = new ArrayList<User>();
        Cursor c = db.rawQuery("select * from user_info where name = ? ",  new String[]{name});
        boolean isExists = c.moveToNext();
        c.close();
        db.close();
        return isExists;
    }
}