package com.baiyun.xiangchengma.server.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangbai on 2022/9/8.
 * 封装地图工具类，精度不够高，解析地名准确，经纬度偏差大
 */
public class LocationUtils {

    public static String cityName;   //城市名
    private static Geocoder geocoder;  //此对象能通过经纬度来获取相应的城市等信息

    //私有化静态经纬度
    private static double latitude;
    private static double Longitude;
    //私有化位置信息
    private String province;
    private String city;
    //私有化详细信息
    private String district;
    private String streetNumber;
    //私有化文本
    private String text;

    public AMapLocationClient mLocationClient=null;


    public AMapLocationClientOption mLocationOption=null;


    //通过地理坐标获取城市名 其中CN分别是city和name的首字母缩写,返回map集合
    public static Map<String,Object> getCNBylocation(Context context) {
        geocoder = new Geocoder(context);
        //用于获取Location对象，以及其他
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        //实例化一个LocationManager对象
        locationManager = (LocationManager) context.getSystemService(serviceName);
        //provider的类型
        String provider = LocationManager.NETWORK_PROVIDER;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    //低精度   高精度：ACCURACY_FINE
        criteria.setAltitudeRequired(false);       //不要求海拔
        criteria.setBearingRequired(false);       //不要求方位
        criteria.setCostAllowed(false);      //不允许产生资费
        criteria.setPowerRequirement(Criteria.POWER_LOW);   //低功耗

        //通过最后一次的地理位置来获取Location对象
        Location location = locationManager.getLastKnownLocation(provider);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Map<String, Object> update = updateWithNewLocation(location);

        String queryed_name = (String) update.get("mcityName");

        if((queryed_name!=null)&&(0!=queryed_name.length())){
            cityName = queryed_name;
        }
        /*
        第二个参数表示更新的周期，单位为毫秒，
        第三个参数的含义表示最小距离间隔，单位是米，设定每30秒进行一次自动定位
        */
        locationManager.requestLocationUpdates(provider, 30000, 50, locationListener);
        //移除监听器，在只有一个widget的时候，这个还是适用的
        locationManager.removeUpdates(locationListener);

        return update;
    }



    //方位改变是触发，进行调用
    private final static LocationListener locationListener = new LocationListener() {
        String tempCityName;
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {

            Map<String, Object> update = updateWithNewLocation(null);

            tempCityName = (String) update.get("mcityName");

            if((tempCityName!=null)&&(tempCityName.length()!=0)){
                cityName = tempCityName;
            }
        }
        @Override
        public void onLocationChanged(Location location) {

            Map<String, Object> update = updateWithNewLocation(location);

            tempCityName = (String) update.get("mcityName");

            if((tempCityName!=null)&&(tempCityName.length()!=0)){
                cityName = tempCityName;
            }

        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };



    //更新location  return cityName
    private static Map<String,Object> updateWithNewLocation(Location location){
        String mcityName = "";
        double lat = 0;
        double lng = 0;
        //设值map数组储存数据
        HashMap<String,Object> map=new HashMap<>();

        List<Address> addList = null;

        if(location!=null){
            lat = location.getLatitude();
            lng = location.getLongitude();

        }else{
            cityName = "无法获取地理信息";
        }
        try {
            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addList!=null&&addList.size()>0){
            for(int i=0;i<addList.size();i++){
                Address add = addList.get(i);
                mcityName += add.getLocality()+add.getSubLocality()+add.getThoroughfare();
            }
        }
        Log.e("mcityName",mcityName);
        map.put("mcityName",mcityName);
        return map;
    }


}
