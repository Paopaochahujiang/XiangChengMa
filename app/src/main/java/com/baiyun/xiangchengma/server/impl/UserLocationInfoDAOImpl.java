package com.baiyun.xiangchengma.server.impl;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.baiyun.xiangchengma.bean.UserLocationInfo;
import com.baiyun.xiangchengma.server.UserLocationInfoDAO;
import com.baiyun.xiangchengma.server.helper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户位置信息数据访问接口实现
 * Created by wangbai on 2022/9/9.
 */
public class UserLocationInfoDAOImpl implements UserLocationInfoDAO {

    private DBHelper mHelpter = null;

    public UserLocationInfoDAOImpl(Context context) {

        mHelpter = new DBHelper(context);

        //database文件夹的创建靠这句话
        SQLiteDatabase database = mHelpter.getReadableDatabase();
    }

    /**
     * 实现插入用户定位信息方法
     * @param user
     */
    @Override
    public void insertUserLocationInfo(UserLocationInfo user ) {

        //写入数据库
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("insert into location_info(_id,name,latitude,longitude,location) values(?,?,?,?,?)",
                    new Object[]{user.getId(),user.getName(), user.getLatitude(), user.getLongitude(),user.getLocation()});
        }

        db.close();

    }

    /**
     * 删除用户位置信息方法,定期清理用户数据,防止数据冗余(保持用户数据固定100条)
     * @param ordinal
     */
    @Override
    public void deleteUserLocationInfo(Integer ordinal) {
        //写入数据库
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        //判断权限是否打开
        if(db.isOpen()) {
            db.execSQL("delete from location_info where ordinal =?",
                    new Object[]{ordinal});

            Log.e("数据日志", "首行数据已删除,其序号为: "+ordinal );
        }
        db.close();
    }

    /**
     * 更新用户位置信息
     * @param ordinal
     * @param latitude
     * @param longitude
     */
    @Override
    public void  updateUserLocationInfo(Integer ordinal,double latitude,double longitude) {
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("update location_info set latitude=?,longitude=? where  ordinal=?",
                new Object[]{latitude,longitude,ordinal});
            Log.d("update", "数据已更新");
        }
        db.close();
    }

    /**
     * 检索用户所保存信息,定期清理用户数据
     */
    @SuppressLint("Range")
    public void getAllUserLocationInfoAndDelete(Integer id) {
        //初始化序号参数
        int ordinal;
        //获得写入数据库权限
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        //判断权限是否打开
        if(db.isOpen()){
        //获得游标数据
        Cursor c = db.rawQuery("select * from location_info ", null);

        if (c.getCount() > 200) {
            //获得第一条数据库用户的第一条记录
            Cursor firstUserLocation = db.rawQuery("select * from location_info limit 1", null);
            //遍历游标数据
            while (firstUserLocation.moveToNext()) {
                {
                    //获得当前数据的序号
                    ordinal = firstUserLocation.getInt(firstUserLocation.getColumnIndex("ordinal"));
                    //打印数据
                    Log.e("数据检索", "获得了该数据的序号为: " + ordinal);
                    Log.d("数据核实", "获得了该数据的名字为: "+firstUserLocation.getString(firstUserLocation.getColumnIndex("name")));
                }

                //删除该行数据
                deleteUserLocationInfo(ordinal);
            }

        }

            c.close();
            db.close();

        }

    }
    /**
     * 检索用户信息,如若发现储存信息超过100条,则调用deleteUserLocationInfo删除多余信息
     * @param id
     * @return
     */
    @SuppressLint("Range")
    @Override
    public List<UserLocationInfo> getAllUserLocationInfo(Integer id) {
        SQLiteDatabase db = mHelpter.getWritableDatabase();

        List<UserLocationInfo> list = new ArrayList<UserLocationInfo>();
        //判断权限是否打开
        if(db.isOpen()) {
            Cursor c = db.rawQuery("select * from location_info where _id=1", null);

            //迭代游标数据
            while (c.moveToNext()) {

                UserLocationInfo locationInfo = new UserLocationInfo();

                locationInfo.setOrdinal(c.getInt(c.getColumnIndex("ordinal")));

                locationInfo.setId(c.getInt(c.getColumnIndex("_id")));

                locationInfo.setName(c.getString(c.getColumnIndex("name")));

                locationInfo.setLatitude(c.getDouble(c.getColumnIndex("latitude")));

                locationInfo.setLongitude(c.getDouble(c.getColumnIndex("longitude")));

                locationInfo.setLocation(c.getString(c.getColumnIndex("location")));

                //写入数据
                list.add(locationInfo);
            }
            c.close();
            db.close();
        }
        return list;
    }


    /**
     * 查询用户最新位置信息(1条)
     * @param id
     * @return
     */
    @SuppressLint("Range")
    @Override
    public Map<String, UserLocationInfo> getUserLocationInfo(Integer id) {
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        UserLocationInfo user = new UserLocationInfo();
        //设置数组存储结果
        HashMap<String,UserLocationInfo> map =new HashMap<>();
        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from location_info order by ordinal desc limit 1  ", null);
            //迭代游标
            while (cursor.moveToNext()) {
                {
                    @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex("_id"));

                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));

                    Double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));

                    Double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

                    String location  = cursor.getString(cursor.getColumnIndex("location"));

                    user.setId(_id);
                    user.setName(name);
                    user.setLatitude(latitude);
                    user.setLongitude(longitude);
                    user.setLocation(location);

                    map.put("user", user);

                    Log.d("Userlocation", "query: _id" + _id + " name:" + name + " latitude:" + latitude + " longitude:" + longitude);

                }

            }
            cursor.close();
            db.close();
        }
        return map;
    }

    /**
     * 判断用户数据是否存在
     * @param id
     * @return
     */
    @Override
    public boolean isExists(Integer id) {
        SQLiteDatabase db = mHelpter.getWritableDatabase();
        boolean isExists=false;
        if(db.isOpen()) {
            Cursor c = db.rawQuery("select * from user_info where _id = ? ", (String[]) new Object[]{id});
            isExists= c.moveToNext();
            c.close();
            db.close();
        }
        return isExists;
    }

}

