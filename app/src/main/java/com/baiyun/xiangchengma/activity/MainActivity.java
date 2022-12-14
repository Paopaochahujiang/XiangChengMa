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
    private int lastfragment;   //???????????????????????????Fragment
    private RequireHandle requireHandle;
    private MapFragment mapFragment;

    private AmapLocationUtil amapLocationUtil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //???????????????????????????????????????
        setContentView(R.layout.activity_main);



        timer.schedule(task, 1000, 1000);//???????????????????????????????????????


        mBottomNavigationView = findViewById(R.id.bv_bottomNavigation);
        initUI();



        /**
         * ??????????????????10????????????,??????????????????
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
            //???????????????
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    //?????????????????????
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    //??????LocationUtils?????????????????????
                    Map<String, Object> cnBylocation = LocationUtils.getCNBylocation(MainActivity.this);
                    //??????????????????
                    String mcityName = (String) cnBylocation.get("mcityName");


                    //???????????????????????????????????????
                    if (null == amapLocationUtil) {
                        amapLocationUtil = new AmapLocationUtil(MainActivity.this);
                    }else {
                        amapLocationUtil.destroyLocation();//?????????????????????
                    }
                    //??????????????????
                    amapLocationUtil.initLocation(MainActivity.this);
                    //????????????
                    amapLocationUtil.startLocation();
                    //????????????
                    amapLocationUtil.setOnCallBackListener(new AmapLocationUtil.onCallBackListener() {
                        @Override
                        public void onCallBack(double longitude, double latitude, AMapLocation location, boolean isSucdess, String address) {

                            //isSucdess    true  ????????????   false  ??????
                            if(isSucdess){
                                //?????????????????????
                                UserDAOImpl userDAO = new UserDAOImpl(MainActivity.this);
//                        userDAO.updateUser(1,latitude,longitude);
                                //??????????????????
                                Map<String, User> user = userDAO.getUser();
                                //??????????????????id
                                User currentUser = user.get("user");
                                //???????????????????????????
                                UserLocationInfoDAOImpl userLocationInfoDAO=new UserLocationInfoDAOImpl(MainActivity.this);
                                //???????????????????????????????????????,???????????????????????????
                                userLocationInfoDAO.getAllUserLocationInfoAndDelete(currentUser.getId());
                                //??????????????????????????????????????????
                                userLocationInfoDAO.insertUserLocationInfo(new UserLocationInfo(currentUser.getId(),currentUser.getName(),latitude,longitude,mcityName));
                                //????????????,??????
                                Log.e("--->", "longitude" + longitude + "\n" + "latitude" + latitude + "\n" + "isSucdess" + isSucdess + "\n" + "address" + mcityName);
                                Log.e("????????????", "sendMessageAtTime: "+ mcityName);

                            }else{
                                //???????????????????????????
                                amapLocationUtil.startLocation();
                            }

                        }
                    });
                }
            });
        }
    };
    //?????????????????????
    private void initUI() {

        taskAllFragment=new TaskAllFragment();
        infoFragment=new InfoFragment();
        mapFragment=new MapFragment();
        fragments=new Fragment[]{taskAllFragment,infoFragment,mapFragment};
        lastfragment=0; //?????????????????????????????????item
        getSupportFragmentManager().beginTransaction().replace(R.id.mainview,taskAllFragment).show(taskAllFragment).commit();
        mBottomNavigationView = findViewById(R.id.bv_bottomNavigation);

        //mbottomNavigationView ????????????????????????????????????changeFragment
        mBottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);

    }




    //??????toast???????????????
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
     * ????????????????????????????????????????????????????????????????????????
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        amapLocationUtil.destroyLocation();
    }





    //????????????changeFragment
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment=new BottomNavigationView.OnNavigationItemSelectedListener() {
        //????????????item???id????????????????????????switchFragment??????????????????????????????
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
        transaction.hide(fragments[lastfragment]);  //????????????Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.mainview,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

}