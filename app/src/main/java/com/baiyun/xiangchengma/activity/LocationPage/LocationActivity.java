package com.baiyun.xiangchengma.activity.LocationPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baiyun.xiangchengma.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

public class LocationActivity extends AppCompatActivity {


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

    private TextView position;

    private AMapLocationListener mapLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(aMapLocation!=null)
            {
                if(aMapLocation.getErrorCode()==0)
                {
                     latitude=aMapLocation.getLatitude();
                     Longitude=aMapLocation.getLongitude();
                     province=aMapLocation.getProvince();
                     city=aMapLocation.getCity();
                     district=aMapLocation.getDistrict();
                     streetNumber=aMapLocation.getStreetNum();


                     text="经度: "+Longitude+"\n"
                            +"纬度: "+latitude+"\n"
                            +"详细位置: "+province+city+district+streetNumber;

                    position.setText(text);
                    Log.d("HandleAndPostDelayed", "监听被调用");

                }
                else
                {
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    position.setText("定位失败");
                }
            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);
        position=findViewById(R.id.position_text);
        AMapLocationClient.updatePrivacyShow(getApplicationContext(),true,true);
        AMapLocationClient.updatePrivacyAgree(getApplicationContext(),true);
        try {
            mLocationClient=new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.setLocationListener(mapLocationListener);

        mLocationOption=new AMapLocationClientOption();

        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        if(null!=mLocationClient)
        {
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        mLocationClient.onDestroy();
    }

    public static String sHA1(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getText() {
        return text;
    }

    public AMapLocationListener getMapLocationListener() {
        return mapLocationListener;
    }

    public AMapLocationClient getmLocationClient() {
        return mLocationClient;
    }
}