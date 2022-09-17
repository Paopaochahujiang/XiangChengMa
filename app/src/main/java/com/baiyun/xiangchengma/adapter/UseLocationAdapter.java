package com.baiyun.xiangchengma.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baiyun.xiangchengma.bean.UserLocationInfo;

import java.util.List;

/**
 * 配置用户定位数据信息适配器
 */
public class UseLocationAdapter extends BaseAdapter {

    private List<UserLocationInfo> mData;
    private Context mContext;

    public UseLocationAdapter(List<UserLocationInfo> data, Context context){
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


        line1.setTextColor(Color.RED);
        line2.setTextColor(Color.BLACK);


        //获取对应的对象
        UserLocationInfo userLocationInfo = (UserLocationInfo) getItem(i);

        //显示用户所在地址
        line1.setText("地址序号"+userLocationInfo.getOrdinal()+"\n"
                + "用户姓名"+userLocationInfo.getName());
        //显示设备地址
        line2.setText(
                "经度: "+userLocationInfo.getLongitude()+"\n"
                +"纬度: "+userLocationInfo.getLatitude()+"\n"
                +"详细位置: "+userLocationInfo.getLocation());

        return itemView;
    }

    //刷新列表，防止搜索结果重复出现
    public void refresh(List<UserLocationInfo> data){
        mData = data;
        notifyDataSetChanged();
    }
}
