package com.baiyun.xiangchengma.activity.UsePage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baiyun.xiangchengma.server.helper.MySqliteOpenHelper;
import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.bean.User;
import com.baiyun.xiangchengma.server.impl.UserDAOImpl;

import java.util.HashMap;
import java.util.Map;


/**
 * 数据库界面
 */
public class SqlActivity extends AppCompatActivity {




    private UserDAOImpl userDAO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sql);
    }

    /**
     * 生成DB文件
     * @param view
     */
    public void createDB(View view) {

        userDAO=new UserDAOImpl(this);



    }

    /**
     * 查询所有数据
     * @param
     */
    @SuppressLint("Range")
    public void queryAll(View view) {
//
        userDAO=new UserDAOImpl(this);

         userDAO.getUser();

//
    }

    /**
     * 数据库插入
     * @param view
     */
    public void insert(View view) {


        userDAO=new UserDAOImpl(this);

        User user =new User("wangbai");

        userDAO.insertUser(user);

        Log.d("message", "定位已更新");
    }

    /**
     * 数据库修改
     *
     */
    public void update(View view) {

        SQLiteOpenHelper helper = MySqliteOpenHelper.getmInstance(this);

        SQLiteDatabase db = helper.getWritableDatabase();

        //判断数据库是否打开
        if(db.isOpen()){

            String sql = "update persons set latitude=?,longitude=? where  _id=?";

            //语句执行
            db.execSQL(sql,new Object[]{1,1,1});

            Log.d("HandleAndPostDelayed", "定位已更新");

        }

        db.close();

    }

    /**
     * 数据库删除
     * @param view
     */
    public void delete(View view) {
        SQLiteOpenHelper helper =MySqliteOpenHelper.getmInstance(this);


        SQLiteDatabase db = helper.getWritableDatabase();

        //判断数据库是否打开
        if(db.isOpen()){

            //删除语句
            String sql = "delete from persons where _id=?";

            //语句执行
            for(int i=0;i<=7;i++) {
                if(i!=6) {
                    db.execSQL(sql, new Object[]{i});
                }
            }
        }

        db.close();

    }

    /**
     * 查询单个数据
     * @param view
     */
    @SuppressLint("Range")
    public  Map<String,Object> queryOne(View view) {

        SQLiteOpenHelper helper =MySqliteOpenHelper.getmInstance(this);

        @SuppressLint("Range")  String latitude;
        @SuppressLint("Range") String longitude;


        HashMap<String, Object> map = new HashMap<>();
        //创建database
        SQLiteDatabase db = helper.getReadableDatabase();

        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from user_info", null);

            //迭代游标
            while(cursor.moveToNext()){

               {
                @SuppressLint("Range") int _id = cursor.getInt(cursor.getColumnIndex("_id"));

                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));

                latitude = cursor.getString(cursor.getColumnIndex("latitude"));

                longitude= cursor.getString(cursor.getColumnIndex("longitude"));

                Log.d("wangbai","query: _id"+_id+" name:"+name+" latitude:"+latitude+" longitude:"+longitude);

                    Double latitudes = Double.valueOf(latitude);
                    Double longitudes = Double.valueOf(longitude);



                    map.put("User",new User(_id,name));
            }
            }
            //关闭游标
            cursor.close();
            //关闭数据库
            db.close();
        }
        return  map;
    }
}