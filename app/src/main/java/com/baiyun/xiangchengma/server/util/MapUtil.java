package com.baiyun.xiangchengma.server.util;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.PolylineOptions;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 地图工具类
 * 创造地图,以及调用定位，精度略低
 * Created by wangbai on 2022/9/8.
 */

public class MapUtil implements AMapLocationListener, AMap.OnCameraChangeListener {
    private static final String TAG = "MapUtil";
    private static MapUtil sMapUtil;
    // 刷新的距离
    private static double REFRESH_DISTANCE = 300;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMap    aMap;
    private Context mContext;
    private LatLng mLatLng;
    private LatLng  preLatlng;
    private double totalDistance = 0;
    private MapListener mMapListener;
    private SparseArray<Marker> mMarkerSparseArray = new SparseArray<>();
    private boolean             first              = true;
    private boolean             firstChange        = true;
    private int                 getZoomB           = 19;
    private String preAddress;

    private MapUtil() {

    }

    public static MapUtil getInstance() {
        if (sMapUtil == null) {
            synchronized (MapUtil.class) {
                if (sMapUtil == null) {
                    sMapUtil = new MapUtil();
                }
            }
        }
        return sMapUtil;
    }

    public MapUtil initMap(AMap aMap, Context context) {
        initMap(aMap, context, getZoomB);
        return this;
    }

    // 初始化地图,关键代码
    public MapUtil initMap(AMap aMap, Context context, int getZoomB) {
        this.aMap = aMap;
        this.mContext = context;
        try {
            mlocationClient = new AMapLocationClient(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(3000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
        // 显示定位蓝点
        initPoint();
        // 去掉右边缩放按钮
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnCameraChangeListener(this);
        // 开启指南针
        aMap.getUiSettings().setCompassEnabled(true);
        return this;
    }

    // 自定义定位蓝点
    public void initPoint() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        // 设置边框的颜色
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        // 设置边框的填充色
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        // 因为模式问题，所以要手动调用
        if (mLatLng != null) {
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatLng));
        }
        // 地图的层级大小
        aMap.moveCamera(CameraUpdateFactory.zoomTo(getZoomB));
        // TODO: 2018/5/21 0021  重置指南针位置，找了好久
        if (mLatLng != null) {
            float bearing = 0.0f;  // 地图默认方向
            float tilt = 0.0f;  // 地图默认方向
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, getZoomB, tilt, bearing)));
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                String address = amapLocation.getAddress();
                // 当前位置
                mLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (mMapListener != null && first) {
                    mMapListener.getLatLng(mLatLng);
                    first = false;
                    preLatlng = mLatLng;
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatLng));
                    preAddress = address;
                    final LatLng latLng = MapUtil.getInstance().getLatLng();

                } else {
                    if (TextUtils.isEmpty(preAddress) || !preAddress.equals(address)) {
                        preAddress = address;
                        final LatLng latLng = MapUtil.getInstance().getLatLng();

                    }
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    public void clearAllMarker() {
        for (int i = 0; i < mMarkerSparseArray.size(); i++) {
            Marker marker = mMarkerSparseArray.get(i);
            marker.remove();
        }
        mMarkerSparseArray.clear();
    }

    public void addAllMarker(List<LatLng> latLngList) {
        for (LatLng latLng : latLngList) {
            addMarker(latLng);
        }
    }

    public void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        // TODO: 2018/5/13 如果是复杂的,可以把布局转成bitmap
        //        View view = View.inflate(mContext, R.layout.marker, null);
        //        Bitmap bitmap = convertViewToBitmap(view);



        Marker marker = aMap.addMarker(markerOptions);
        mMarkerSparseArray.put(mMarkerSparseArray.size(), marker);
        marker.showInfoWindow();
        // 是否显示infoWindow
        infoWindow();
    }

    public void addRectangle(LatLng latLng) {
        // 绘制一个长方形
        aMap.addPolygon(new PolygonOptions()
                .addAll(createRectangle(latLng, 0.0001, 0.0001))
                .fillColor(Color.parseColor("#FFCBCB"))
                // 线的宽度取消
                .strokeWidth(0)
        );
        // 虚线组成一个长方形
        aMap.addPolyline(new PolylineOptions()
                .addAll(createRectangle(latLng, 0.0001, 0.0001))
                .width(10)
                .setDottedLine(true)
                .color(Color.parseColor("#F45A5A")));
    }

    public void addRectangle(List<LatLng> latLngList) {
        // 绘制一个长方形
        aMap.addPolygon(new PolygonOptions()
                .addAll(latLngList)
                .fillColor(Color.parseColor("#4DBDEDFF"))
                // 线的宽度取消
                .strokeWidth(0)
        );
        // 虚线组成一个长方形
        aMap.addPolyline(new PolylineOptions()
                .addAll(latLngList)
                .width(10)
                .setDottedLine(true)
                .color(Color.parseColor("#3BC3F5")));
    }

    /**
     * 生成一个长方形的四个坐标点
     */
    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        // 添加最后一个点，组成闭合
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
        // 矩形的四个点
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
        return latLngs;
    }

    /**
     * 布局转化成bitmap
     *
     * @param view
     * @return
     */
    public Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public LatLng getCenterLatLng() {
        return preLatlng;
    }

    public void infoWindow() {
        aMap.setInfoWindowAdapter(new InfoWindowAdapter());
    }


    private BitmapDescriptor getBitmapDescriptor(int id) {
        return BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), id));
    }

    /**
     * 跳转高地地图
     *
     * @param latLng
     * @param type
     */
    public void startGaoDeMap(LatLng latLng, int type) {
        // type 4:步行 0:开车
        Intent intent = new Intent("android.intent.action.VIEW",
                Uri.parse("androidamap://route?sourceApplication=赳猎人&dname=车辆位置" + "&dlat=" + latLng.latitude + "&dlon=" + latLng.longitude + "&dev=0&t=" + type));
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setPackage("com.autonavi.minimap");
        if (isInstallByread("com.autonavi.minimap")) {
            mContext.startActivity(intent);
        } else {

        }
    }

    /**
     * 是否安装高德地图
     *
     * @param packageName
     * @return
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName)
                .exists();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLng currentLatlng = cameraPosition.target;
        double distance = AMapUtils.calculateLineDistance(preLatlng, currentLatlng);
        totalDistance = distance;
        Log.d(TAG, "onCameraChange: 距离" + totalDistance);
        if (totalDistance >= REFRESH_DISTANCE) {
            if (firstChange) {
                firstChange = false;
                return;
            }
            preLatlng = currentLatlng;
            Log.d(TAG, "onCameraChange: 要刷新了");
            totalDistance = 0;
            if (mMapListener != null) {
                mMapListener.refresh();
            }
        }
    }

    public void setMapRefreshListener(MapListener mapRefreshListener) {
        mMapListener = mapRefreshListener;
    }

    /**
     * 百度转高德
     *
     * @param latLng
     * @return
     */
    public LatLng baidu2Gaode(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter();
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.BAIDU);
        // sourceLatLng待转换坐标点 LatLng类型
        converter.coord(latLng);
        // 执行转换操作
        latLng = converter.convert();
        return latLng;
    }

    /**
     * 移动指定距离刷新的回调
     */
    public interface MapListener {

        void refresh();

        void getLatLng(LatLng latLng);

    }

    public class InfoWindowAdapter implements AMap.InfoWindowAdapter {
        private View infoWindow = null;

        @Override
        public View getInfoWindow(Marker marker) {
            if (infoWindow == null) {

            }
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}