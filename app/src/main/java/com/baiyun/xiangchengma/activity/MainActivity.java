package com.baiyun.xiangchengma.activity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.MapsInitializer;
import com.baiyun.xiangchengma.Listener.impl.RequireHandle;
import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.activity.mainFragment.InfoFragment;
import com.baiyun.xiangchengma.activity.mainFragment.MapFragment;
import com.baiyun.xiangchengma.activity.mainFragment.TaskAllFragment;
import com.baiyun.xiangchengma.adapter.DeviceAdapter;
import com.baiyun.xiangchengma.bean.User;
import com.baiyun.xiangchengma.bean.UserLocationInfo;
import com.baiyun.xiangchengma.server.impl.UserDAOImpl;
import com.baiyun.xiangchengma.server.impl.UserLocationInfoDAOImpl;
import com.baiyun.xiangchengma.server.util.AmapLocationUtil;
import com.baiyun.xiangchengma.server.util.LocationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    Timer timer = new Timer();
    private Handler handler2;
    private Runnable runnable2;


    private ListView mListView;
    private DeviceAdapter mAdapter;
    private Toast mToast;
    private Button mBtnNfc;
    private Button mBtnMap;
    private Button mBtnLoc;
    private Button mBtnSql;
    private Button mBtnAllLoc;
    private BottomNavigationView mBottomNavigationView;
    private TaskAllFragment taskAllFragment;
    private InfoFragment infoFragment;
    private Fragment[] fragments;
    private int lastfragment;   //用于记录上个选择的Fragment
    private RequireHandle requireHandle;
    private MapFragment mapFragment;

    private AmapLocationUtil amapLocationUtil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //让底部导航栏不被软键盘顶起
        setContentView(R.layout.activity_main);



        timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒


        mBottomNavigationView = findViewById(R.id.bv_bottomNavigation);
        initUI();



        /**
         * 用户权限安卓10以上需要,获取定位权限
         **/
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
//                    || ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
//                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
//                ActivityCompat.requestPermissions(this, strings, 1);
//            }
//        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    //定义任务计时器
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    //使用LocationUtils工具类加强精度
                    Map<String, Object> cnBylocation = LocationUtils.getCNBylocation(MainActivity.this);
                    //获取精确地名
                    String mcityName = (String) cnBylocation.get("mcityName");


                    //判断该工具类是否已被实例化
                    if (null == amapLocationUtil) {
                        amapLocationUtil = new AmapLocationUtil(MainActivity.this);
                    }else {
                        amapLocationUtil.destroyLocation();//若实例化则销毁
                    }
                    //初始化工具类
                    amapLocationUtil.initLocation(MainActivity.this);
                    //开启定位
                    amapLocationUtil.startLocation();
                    //设置监听
                    amapLocationUtil.setOnCallBackListener(new AmapLocationUtil.onCallBackListener() {
                        @Override
                        public void onCallBack(double longitude, double latitude, AMapLocation location, boolean isSucdess, String address) {

                            //isSucdess    true  定位成功   false  失败
                            if(isSucdess){
                                //引进数据库方法
                                UserDAOImpl userDAO = new UserDAOImpl(MainActivity.this);
//                        userDAO.updateUser(1,latitude,longitude);
                                //获得当前用户
                                Map<String, User> user = userDAO.getUser();
                                //获取当前用户id
                                User currentUser = user.get("user");
                                //实例用户地址表接口
                                UserLocationInfoDAOImpl userLocationInfoDAO=new UserLocationInfoDAOImpl(MainActivity.this);
                                //检索当前数据库数据是否溢出,并且删除第一行数据
                                userLocationInfoDAO.getAllUserLocationInfoAndDelete(currentUser.getId());
                                //执行当前数据并且插入最后一行
                                userLocationInfoDAO.insertUserLocationInfo(new UserLocationInfo(currentUser.getId(),currentUser.getName(),latitude,longitude,mcityName));
                                //定位成功,打印
                                Log.e("--->", "longitude" + longitude + "\n" + "latitude" + latitude + "\n" + "isSucdess" + isSucdess + "\n" + "address" + mcityName);
                                Log.e("详细地名", "sendMessageAtTime: "+ mcityName);

                            }else{
                                //定位失败，重试定位
                                amapLocationUtil.startLocation();
                            }

                        }
                    });
                }
            });
        }
    };
    //初始化用户界面
    private void initUI() {

        taskAllFragment=new TaskAllFragment();
        infoFragment=new InfoFragment();
        mapFragment=new MapFragment();
        fragments=new Fragment[]{taskAllFragment,infoFragment,mapFragment};
        lastfragment=0; //表示上个被选中的导航栏item
        getSupportFragmentManager().beginTransaction().replace(R.id.mainview,taskAllFragment).show(taskAllFragment).commit();
        mBottomNavigationView = findViewById(R.id.bv_bottomNavigation);

        //mbottomNavigationView 绑定的一个点击监听的函数changeFragment
        mBottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);

    }




    //设置toast的标准格式
    private void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            mToast.show();
        } else {
            mToast.setText(text);
            mToast.show();
        }
    }





    /**
     * 退出时注销广播、注销连接过程、注销等待连接的监听
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        amapLocationUtil.destroyLocation();
    }





    //监听函数changeFragment
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment=new BottomNavigationView.OnNavigationItemSelectedListener() {
        //对点击的item的id做判断，然后通过switchFragment函数来进行界面的操作
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Intent intent = null;
            switch (menuItem.getItemId())
            {
                case R.id.menu_task:
                {
                    if(lastfragment!=0)
                    {
                        switchFragment(lastfragment,0);
                        lastfragment=0;
                    }
                    return true;
                }
                case R.id.menu_info:
                {
                    if (lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;
                    }
                    return true;
                }
                case R.id.map_loc:
                {
                    if (lastfragment!=2)
                    {
                        switchFragment(lastfragment,2);
                        lastfragment=2;
                    }
                    return true;
                }
            }
            return false;
        }
    };

    private void switchFragment(int lastfragment,int index){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);  //隐藏上个Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.mainview,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

}