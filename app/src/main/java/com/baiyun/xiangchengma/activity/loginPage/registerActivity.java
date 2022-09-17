package com.baiyun.xiangchengma.activity.loginPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.bean.User;
import com.baiyun.xiangchengma.server.impl.UserDAOImpl;

public class registerActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private CheckBox mSureAgreeButton;                //确定同意按钮

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //找到按钮
        mSureButton = (Button) findViewById(R.id.btn_register);

        //找到editText的值
        mAccount = (EditText) findViewById(R.id.et_account);
        mPwd = (EditText) findViewById(R.id.et_password);
        mPwdCheck = (EditText) findViewById(R.id.et_password_Confirm);
        mSureAgreeButton = (CheckBox) findViewById(R.id.cb_agree);
        //监听按钮
        mSureButton.setOnClickListener(this);      //注册界面两个按钮的监听事件


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:  //确认按钮的监听事件
                if (mSureAgreeButton.isChecked()) {
                    register();
                    break;
                } else {
                    Toast.makeText(this, "请先同意用户协议", Toast.LENGTH_SHORT).show();
                }


        }
    }

    /**
     * 注册
     */
    public void register() {

        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userPwd = mPwd.getText().toString().trim();
            //获得数据库接口
            UserDAOImpl userDAO = new UserDAOImpl(this);
            boolean isExist = userDAO.isExists(userName);
            boolean flag = true;
            if (isExist) {//用户已经存在
                Toast.makeText(this, "用户名已经存在，不能重复注册",
                        Toast.LENGTH_SHORT).show();
                flag = false;
                return;
            }

            userDAO.insertUser(new User(userName));//保存数据
            if (flag) {//保存数据成功
                Toast.makeText(this, "注册成功",
                        Toast.LENGTH_SHORT).show();
                Intent intent_Register_to_Login = new Intent(registerActivity.this, LoginActivity.class);    //切换User Activity至Login Activity
                startActivity(intent_Register_to_Login);
                finish();
            } else {
                Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public boolean isUserNameAndPwdValid() {
        String userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
        String userPwd = mPwd.getText().toString().trim();
        if (userName.equals("")) { //用户名为空
            Toast.makeText(this, "用户名不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (userPwd.equals("")) {
            Toast.makeText(this, "密码不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



}