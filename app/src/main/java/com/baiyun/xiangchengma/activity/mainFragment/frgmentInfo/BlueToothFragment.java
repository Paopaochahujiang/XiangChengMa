package com.baiyun.xiangchengma.activity.mainFragment.frgmentInfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.adapter.DeviceAdapter;
import com.baiyun.xiangchengma.controller.BlueToothController;
import com.baiyun.xiangchengma.nfc.NfcActivity;
import com.baiyun.xiangchengma.server.util.BlueToothTools;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class BlueToothFragment extends Fragment {

    //扫描出的设备列表
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    //蓝牙控制器
    private BlueToothController mController = new BlueToothController();
    //列表适配器
    private DeviceAdapter mAdapter;
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
                Double dist = BlueToothTools.calculatedDistByRSSI(intent);
                if (dist < 1.0) {
                    //找到一个添加一个
                    Log.d(device.getName(), dist + "米");
                    if (!mDeviceList.contains(device)) {
                        mDeviceList.add(device);
                        mController.stopFindDevice();
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };
    private BlueToothTools mBTTools;


    private FloatingActionButton fab_save;
    private FloatingActionButton fab_del;
    private FloatingActionButton fab_NFC;
    private ListView listView_info;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__blue_tooth, container, false);


        listView_info = (ListView) view.findViewById(R.id.listView_bluetooth);
        init();
        mBTTools.Init();

        fab_save = (FloatingActionButton) view.findViewById(R.id.fab_1);
        fab_del = (FloatingActionButton) view.findViewById(R.id.fab_2);
        fab_NFC = (FloatingActionButton) view.findViewById(R.id.fab_3);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mController.findDevice();
            }
        });

        fab_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            }
        });
        fab_NFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getContext(), NfcActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        mAdapter = new DeviceAdapter(mDeviceList, this.getActivity());
        listView_info.setAdapter(mAdapter);

    }

    private void init() {
        mBTTools = new BlueToothTools(this.getActivity(), mController, receiver);
    }

    private void refresh() {
        onCreate(null);
    }


}