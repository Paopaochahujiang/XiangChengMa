package com.baiyun.xiangchengma.ViewData;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.adapter.UseLocationAdapter;
import com.baiyun.xiangchengma.bean.UserLocationInfo;
import com.baiyun.xiangchengma.server.UserLocationInfoDAO;
import com.baiyun.xiangchengma.server.impl.UserLocationInfoDAOImpl;

import java.util.ArrayList;
import java.util.List;

public class AllLocationData extends AppCompatActivity {

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
    private ListView mListView;

    private UserLocationInfoDAO userLocationInfoDAO;
    //检索出的地址列表
    private List<UserLocationInfo> userLocationInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_location_data);
        mListView = findViewById(R.id.location_list);


        //实例化数据库接口
        userLocationInfoDAO = new UserLocationInfoDAOImpl(AllLocationData.this);
        //获取返回数据对象
        List<UserLocationInfo> locationInfo = userLocationInfoDAO.getAllUserLocationInfo(1);
        //4、创建适配器 连接数据源和控件的桥梁
        //参数 1：当前的上下文环境
        //参数 2：当前列表项所加载的布局文件
        //(android.R.layout.simple_list_item_1)这里的布局文件是Android内置的，里面只有一个textview控件用来显示简单的文本内容
        //参数 3：数据源
        UseLocationAdapter useLocationAdapter = new UseLocationAdapter(locationInfo, AllLocationData.this);
        //5、将适配器加载到控件中
        mListView.setAdapter(useLocationAdapter);
        //6、刷新列表
//        useLocationAdapter.refresh(userLocationInfo);
        //7、为列表中选中的项添加单击响应事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                UserLocationInfo item = (UserLocationInfo) useLocationAdapter.getItem(i);
                String result = item.getLocation();
                Toast.makeText(AllLocationData.this, "您的历史位置：" + result+"\n"+"当前轨迹记录总数为"+ useLocationAdapter.getCount(), Toast.LENGTH_LONG).show();
            }
        });


    }


    //销毁
    public void onDestroy()
    {
        super.onDestroy();
    }
}