package com.baiyun.xiangchengma.activity.mainFragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.activity.LocationPage.MapActivity;
import com.baiyun.xiangchengma.bean.UserLocationInfo;
import com.baiyun.xiangchengma.server.impl.UserLocationInfoDAOImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements AMapLocationListener, LocationSource {
    private Activity mActivity;
    private AMap mMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private LatLng myLocation;
    private BitmapDescriptor successDescripter;
    private EditText etAddress;
    private TextView tvAddressName;
    private LinearLayout llOk;
    private View view;
    private Polyline polyline;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = getActivity();
        view = View.inflate(mActivity, R.layout.activity_map, null);
        mapView = (MapView) view.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);
        initMap();
        setUpLocationStyle();


        return view;
    }

    /**
     * 监听
     */
//    private void initListener() {
//        llOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String signedAddress = etAddress.getText().toString().trim();//签到地址
//                String addressName = tvAddressName.getText().toString().trim();//定位地址名称
//                if (TextUtils.isEmpty(signedAddress)){
//                    ToastUtils.showToast("请输入签到地点");
//                    return;
//                }
//
//            }
//        });
//    }

    private void initMap() {
        if (mMap == null) {
            mMap = mapView.getMap();
        }

        mMap.setLocationSource(this);// 设置定位监听

        mMap.setMyLocationEnabled(true);

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        mMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        myLocationStyle.interval(2000); //
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        mMap.moveCamera(cameraUpdate);
        successDescripter = BitmapDescriptorFactory.fromResource(R.drawable.gps_point);
        mMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。


        uiSettings.setAllGesturesEnabled (true);
        //实例化线条数组
        List<LatLng> latLngs = new ArrayList<LatLng>();


//        UserDAOImpl userDAO=new UserDAOImpl(MapActivity.this);
//
//        user = userDAO.getUser();
        // 实例化接口
        UserLocationInfoDAOImpl userLocationInfoDAO = new UserLocationInfoDAOImpl(getContext());
        // 获取当前最近一次定位
//        Map<String, UserLocationInfo> userLocationInfo = userLocationInfoDAO.getUserLocationInfo(1);
//        //获取map集合里得user数组
//        UserLocationInfo user =userLocationInfo.get("user");
//
//        latitude = user.getLatitude();
//        longitude = user.getLongitude();

        //获得所有点集合
        List<UserLocationInfo> allUserLocationInfo = userLocationInfoDAO.getAllUserLocationInfo(1);

        //遍历点集合,并画出线条
        for(UserLocationInfo userLocationInfo : allUserLocationInfo){

            latLngs.add(new LatLng(userLocationInfo.getLatitude(), userLocationInfo.getLongitude()));

        }


        latLngs.add(new LatLng(25.900430,113.265061));
        latLngs.add(new LatLng(24.955192,116.140092));
        latLngs.add(new LatLng(24.898323,114.057694));
        latLngs.add(new LatLng(26.898323,112.057694));
        latLngs.add(new LatLng(27.898323,112.057694));



        polyline = mMap.addPolyline(new PolylineOptions().

                addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));

    }

    private void setUpLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        myLocationStyle.strokeWidth(0);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        mMap.setMyLocationStyle(myLocationStyle);
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationOption = null;
        mLocationClient = null;
        mMap = null;
        mapView.onDestroy();

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            if (mListener != null) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            }
            //获取当前经纬度坐标
            String address = aMapLocation.getAddress();
            myLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            //fixedMarker();
            tvAddressName.setText(address);
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            try {
                mLocationClient = new AMapLocationClient(mActivity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationOption.setOnceLocation(true);//只定位一次
            mLocationOption.setHttpTimeOut(2000);
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();//开始定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            BACK_LOCATION_PERMISSION
    };
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    @Override
    public void onResume() {

        try{
            super.onResume();
            mapView.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {

                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }



    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try{
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
        return true;
    }

}
