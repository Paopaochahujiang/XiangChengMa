package com.baiyun.xiangchengma;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 蓝牙控制器，定义了操作蓝牙的方法
 */
public class BlueToothController {

    private BluetoothAdapter mAdapter;

    public BlueToothController(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 打开蓝牙
     */
    public void turnOnBlueTooth(Activity activity,int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开蓝牙可见性
     */
    public void enableVisibily(Context context){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        context.startActivity(intent);
    }

    /**
     * 查找设备
     */
    public void findDevice() {
        assert (mAdapter != null);
//        mAdapter.startDiscovery();
        scann(mAdapter,3);
        Log.d( "蓝牙","蓝牙开始查找设备");
    }

    void scann(final BluetoothAdapter mBluetoothAdapter, int seconds)
    {
        final long desired_miliseconds=seconds*1000;
        final long start_mils=System.currentTimeMillis();
        final Timer tm=new Timer();
        tm.schedule(new TimerTask() {
            @Override
            public void run() {
                if((System.currentTimeMillis()-start_mils)>=desired_miliseconds&& mBluetoothAdapter.isDiscovering())
                {
                    Log.d("蓝牙", "蓝牙搜索已结束");
                    mBluetoothAdapter.cancelDiscovery();
                    tm.cancel();
                }else if((System.currentTimeMillis()-start_mils)<desired_miliseconds&&!mBluetoothAdapter.isDiscovering())
                {
                    Log.d( "蓝牙","蓝牙开始查找设备");
                    mBluetoothAdapter.startDiscovery();
                }
            }
        },1000,1000);

    }

    /**
     * 关闭查找设备
     */
    public void stopFindDevice() {
        assert (mAdapter != null);
        mAdapter.cancelDiscovery();
        Log.d( "蓝牙","蓝牙取消搜索设备");
    }


    /**
     * 获取已绑定设备
     */
    public List<BluetoothDevice> getBondedDeviceList(){
        return new ArrayList<>(mAdapter.getBondedDevices());
    }
}
