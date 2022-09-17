package com.baiyun.xiangchengma.server.util;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.baiyun.xiangchengma.controller.BlueToothController;

import java.util.Formatter;

public class BlueToothTools {
    public static final int REQUEST_CODE = 0;
    private BroadcastReceiver receiver;
    private Activity activity;
    private BlueToothController mController;

    /**
     * 蓝牙工具构造器
     * @param activity 当前activity上下文
     * @param mController 蓝牙控制器
     * @param receiver 广播监听蓝牙状态注册对象
     */
    public BlueToothTools(Activity activity, BlueToothController mController, BroadcastReceiver receiver) {
        this.receiver = receiver;
        this.activity = activity;
        this.mController = mController;
    }

    /**
     * 蓝牙功能的初始化：
     * 1. 对蓝牙权限的获取
     * 2. 注册广播监听蓝牙状态
     */
    public void Init() {
        registerBluetoothReceiver();

        /**
         * 用户权限安卓10以上需要,获取定位权限
         **/
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(activity, strings, 1);
            }
        }
        //软件运行时直接申请打开蓝牙
        mController.turnOnBlueTooth(activity, REQUEST_CODE);
    }

    public void unregisterReceiver() {
        activity.unregisterReceiver(receiver);
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
        activity.registerReceiver(receiver, filter);
    }

    /**
     * 根据蓝牙信号强度计算距离
     * @param intent 状态发生改变时的意图对象
     * @return 两个设备大概距离，精准到小数点后两位
     */
    public static Double calculatedDistByRSSI(Intent intent){
        short aShort = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
        int iRssi = Math.abs(aShort);
        double power = (iRssi - 59) / 25.0;
        String mm = new Formatter().format("%.2f", Math.pow(10, power)).toString();
        return Double.parseDouble(mm);
    }

}
