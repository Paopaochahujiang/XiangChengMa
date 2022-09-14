package com.baiyun.xiangchengma;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 0;
    //扫描出的设备列表
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    //已绑定设备列表
    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();
    //蓝牙控制器
    private BlueToothController mController = new BlueToothController();

    private ListView mListView;
    private DeviceAdapter mAdapter;
    private Toast mToast;
    private Button mBtnNfc;
    private Button mBtnMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnNfc = findViewById(R.id.btn_NFC);
        mBtnMap = findViewById(R.id.btn_Map);
        OnClick onClick = new OnClick();
        mBtnNfc.setOnClickListener(onClick);
        mBtnMap.setOnClickListener(onClick);

        initUI();

        registerBluetoothReceiver();

        /**
         * 用户权限安卓10以上需要,获取定位权限
         **/
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        }
        //软件运行时直接申请打开蓝牙
        mController.turnOnBlueTooth(this, REQUEST_CODE);
    }

    //初始化用户界面
    private void initUI() {
        mListView = findViewById(R.id.device_list);
        mAdapter = new DeviceAdapter(mDeviceList, this);
        mListView.setAdapter(mAdapter);
    }


    /**
     * 注册蓝牙接收器
     */
    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //注册接收器
        registerReceiver(receiver, filter);
    }

    //注册广播监听搜索结果
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //setProgressBarIndeterminateVisibility(true);
                //初始化数据列表
//                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("蓝牙", "蓝牙搜索已结束");
                mController.findDevice();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //利用蓝牙信号计算距离
                short aShort = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                int iRssi = Math.abs(aShort);
                double power = (iRssi - 59) / 25.0;
                String mm = new Formatter().format("%.2f", Math.pow(10, power)).toString();
                if (Double.parseDouble(mm) < 1.0) {
                    //找到一个添加一个
                    Log.d(device.getName(), mm + "米");
                    if (!mDeviceList.contains(device)) {
                        mDeviceList.add(device);
                        mController.stopFindDevice();
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

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
     * 此方法用于初始化菜单，其中menu参数就是即将要显示的Menu实例。 返回true则显示该menu,false 则不显示;
     * (只会在第一次初始化菜单时调用) Inflate the menu; this adds items to the action bar
     * if it is present.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 函数onOptionsItemSelected()为处理菜单被选中运行后的事件处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.enable_visibility) {
            mController.enableVisibily(this);
        }
        //查找设备
        else if (id == R.id.find_device) {
//            mAdapter.refresh(mDeviceList);
            mController.findDevice();
        }
        //查看已绑定设备
        else if (id == R.id.bonded_device) {
            mBondedDeviceList = mController.getBondedDeviceList();
            mAdapter.refresh(mBondedDeviceList);
        }
        //停止查找设备
        else if (id == R.id.stop_find_device) {
            mController.stopFindDevice();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 退出时注销广播、注销连接过程、注销等待连接的监听
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

    }

    class OnClick implements View.OnClickListener {
        Intent intent = null;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_NFC:
                    intent = new Intent(MainActivity.this, NfcDemoActivity.class);
                    break;
                case R.id.btn_Map:
                    intent = new Intent(MainActivity.this, MapActivity.class);
                    break;
            }
            startActivity(intent);
        }
    }
}