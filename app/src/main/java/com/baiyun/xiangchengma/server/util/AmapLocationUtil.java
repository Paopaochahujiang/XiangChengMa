package com.baiyun.xiangchengma.server.util;


import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.MapsInitializer;

/**
 *
 * AmapLocationUtil
 * create by wangbai 2022/9/9
 *
 * 精度准确,可获取地名,但地名精确度不够
 */
public class AmapLocationUtil {
    private Context mContext;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    public static double longitude = 0;
    public static double latitude = 0;
    private onCallBackListener mOnCallBackListener = null;


    public AmapLocationUtil(Context context) {
        this.mContext = context;

    }

    /**
     * 初始化定位
     */
    public void initLocation(Context context) {
        //初始化client
        if (null == locationClient) {
            try {

                locationClient.updatePrivacyShow(context,true,true);
                locationClient.updatePrivacyAgree(context,true);
                locationClient = new AMapLocationClient(mContext);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        locationOption = getDefaultOption(context);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);

    }


    private AMapLocationClientOption getDefaultOption(Context context) {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        //如果网络可用就选择高精度
        if (NetUtils.isConnected(context)) {
            //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        }
        //否则就选择仅设备模式
        else {
            mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        }
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            StringBuilder sb = new StringBuilder();
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    String district = location.getDistrict();
                    locationSuccess(longitude, latitude, true, location, district);
                    //定位成功，停止定位：如果实时定位，就把stopLocation()关闭
                    stopLocation();
                } else {

                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                    Log.e("---> 定位失败", sb.toString());
                    LocationFarile(false, location);

                }
            } else {
                LocationFarile(false, location);

            }
        }
    };

    private void LocationFarile(boolean isSucdess, AMapLocation location) {
        if (mOnCallBackListener != null) {
            mOnCallBackListener.onCallBack(0, 0, location, false, "");
        }
    }

    public void locationSuccess(double longitude, double latitude, boolean isSucdess, AMapLocation location, String address) {
        if (mOnCallBackListener != null) {
            mOnCallBackListener.onCallBack(longitude, latitude, location, true, address);
        }
    }

    public void setOnCallBackListener(onCallBackListener listener) {
        this.mOnCallBackListener = listener;
    }

    public interface onCallBackListener {
        void onCallBack(double longitude, double latitude, AMapLocation location, boolean isSucdess, String address);
    }
    /**
     * 开始定位
     */
    public void startLocation() {
        locationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     */
    public void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
}