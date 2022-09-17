package com.baiyun.xiangchengma.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class DeviceAdapter extends BaseAdapter{

    private List<BluetoothDevice> mData;
    private Context mContext;

    public DeviceAdapter(List<BluetoothDevice> data, Context context){
        mData = data;
        mContext = context.getApplicationContext();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;
        //复用view，优化性能
        if(itemView == null){
            itemView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_2, viewGroup,false);
        }


        TextView line1 = itemView.findViewById(android.R.id.text1);
        TextView line2 = itemView.findViewById(android.R.id.text2);

        line1.setTextColor(Color.BLACK);
        line2.setTextColor(Color.BLACK);

        //获取对应的蓝牙设备
        BluetoothDevice device = (BluetoothDevice) getItem(i);

        //显示设备名称
//        line1.setText(device.getName());
        String name = device.getName();

        if( name == null || name.length() <=0 || !name.toLowerCase(Locale.ROOT).matches("^[0-1][y,r,g]$")){
            line1.setText(name+"该用户命名不规范，请主动出示健康码");
        }else {
            StringBuilder text = new StringBuilder();
            text.append("性别：");
            text.append(name.charAt(0) == '1'? "男":"女");
            String color = "";
            switch (name.charAt(1)){
                case 'r':
                    color = "红";
                    break;
                case 'y':
                    color = "黄";
                    break;
                case 'g':
                    color = "绿";
                    break;
            }
            text.append("  健康码：");
            text.append(color);
            line1.setText(text);
        }

        //显示设备地址
        line2.setText(device.getAddress());

        return itemView;
    }

    //刷新列表，防止搜索结果重复出现
    public void refresh(List<BluetoothDevice> data){
        mData = data;
        notifyDataSetChanged();
    }

}
