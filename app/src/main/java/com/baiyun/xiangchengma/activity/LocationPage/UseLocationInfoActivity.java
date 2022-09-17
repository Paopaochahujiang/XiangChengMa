package com.baiyun.xiangchengma.activity.LocationPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.ViewData.AllLocationData;
import com.baiyun.xiangchengma.ViewData.NewLocationData;
import com.baiyun.xiangchengma.server.impl.UserLocationInfoDAOImpl;



/**
 * 用于查询用户数据的功能模块
 */
public class UseLocationInfoActivity extends AppCompatActivity {

    //私有化数据库接口实现类
    private UserLocationInfoDAOImpl userLocationInfoDAO;

    //私有化按钮
    private Button mBtnQueryAll;
    private Button mBtnQueryOne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_location_info);

        //实例化按钮
        mBtnQueryAll=findViewById(R.id.btn_QueryAll);
        mBtnQueryOne=findViewById(R.id.btn_QueryOne);


        OnClick onClick = new OnClick();

        //设置点击事件
        mBtnQueryAll.setOnClickListener(onClick);
        mBtnQueryOne.setOnClickListener(onClick);

    }

    /**
     * 创造数据库以及对应的数据表
     * @param view
     */
    public void createDB(View view) {
        userLocationInfoDAO=new UserLocationInfoDAOImpl(this);
    }



    //配置点击事件
    class OnClick implements View.OnClickListener {
        Intent intent = null;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_QueryAll:
                    intent = new Intent(UseLocationInfoActivity.this, AllLocationData.class);
                    break;
                case R.id.btn_QueryOne:
                    intent = new Intent(UseLocationInfoActivity.this, NewLocationData.class);
                    break;


            }
            startActivity(intent);
        }
    }
}