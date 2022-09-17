package com.baiyun.xiangchengma.ViewData;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.TextView;

import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.bean.User;
import com.baiyun.xiangchengma.bean.UserLocationInfo;
import com.baiyun.xiangchengma.server.UserLocationInfoDAO;
import com.baiyun.xiangchengma.server.impl.UserLocationInfoDAOImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewLocationData extends AppCompatActivity {

    //私有化id
    private Integer id;
    //私有化姓名
    private String name;
    //私有化纬度
    private double latitude;
    //私有化经度
    private double longitude;
    //私有化地点
    private String location;
    //私有化文本
    private TextView position;

    private UserLocationInfoDAO userLocationInfoDAO;
    //私有化文本
    private String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_location_data);

        //获取postion实例
        position=findViewById(R.id.current_postion);
        
        //实例化数据库定位接口
        userLocationInfoDAO = new UserLocationInfoDAOImpl(NewLocationData.this);

        Map<String, UserLocationInfo> userLocationInfo = userLocationInfoDAO.getUserLocationInfo(1);

        UserLocationInfo locationInfo = userLocationInfo.get("user");

        id = locationInfo.getId();
        name = locationInfo.getName();
        latitude=locationInfo.getLatitude();
        longitude=locationInfo.getLongitude();
        location=locationInfo.getLocation();

        text="id"+id+"\n"
                +"姓名"+name+"\n"+
                "经度: "+longitude+"\n"
                +"纬度: "+latitude+"\n"
                +"详细位置: "+location;

        position.setText(text);


    }

    //销毁
    public void onDestroy()
    {
        super.onDestroy();
    }
}