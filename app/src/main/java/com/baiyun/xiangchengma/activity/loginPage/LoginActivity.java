package com.baiyun.xiangchengma.activity.loginPage;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baiyun.xiangchengma.R;
import com.baiyun.xiangchengma.activity.MainActivity;
import com.baiyun.xiangchengma.bean.User;
import com.baiyun.xiangchengma.server.impl.UserDAOImpl;

import java.util.List;

//implements View.OnClickListener 之后，
//就可以把onClick事件写到onCreate()方法之外
//这样，onCreate()方法中的代码就不会显得很冗余
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private UserDAOImpl userDAO;     //声明UserDBOpenHelper对象，这玩意儿用来创建数据表
    private Button mBtLogin;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private CheckBox remember;
    //实现记住密码需要用到SharePreferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    //注册切换页面
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);    //把视图层 View 也就是 layout 的内容放到 Activity 中进行显示

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();

        userDAO = new UserDAOImpl(this);     //实例化DBOpenhelper，进行登录验证的时候要用来进行数据查询
//        mUserDBOpenHelper.add("001","2222");
//        mUserDBOpenHelper.add("西门社区","12345");

    }

    private void initView() {
        //初始化视图中的控件对象
        mBtLogin = findViewById(R.id.bt_loginactivity_login);
        mBtLogin.setOnClickListener(this);      //设置点击事件监听器
        mEtUsername = findViewById(R.id.et_loginactivity_username);
        mEtPassword = findViewById(R.id.et_loginactivity_password);
        remember = (CheckBox) findViewById(R.id.cb_remember);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemenber = pref.getBoolean("remember_password", false);
        if (isRemenber) {
            mEtUsername.setText(pref.getString("name", ""));
            mEtPassword.setText(pref.getString("password", ""));
            remember.setChecked(true);
        }
    }

    public void onClick(View view) {
        String name = mEtUsername.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {

            //获得当前用户数据
            List<User> allUser = userDAO.getAllUser();
            //设置布尔值
            boolean match = false;
            //遍历所有人员表
            for (int i = 0; i < allUser.size(); i++) {

                User user = allUser.get(i);

                if (name.equals(user.getName())) {

                    match = true;

                    break;
                } else {
                    match = false;
                }
            }
            if (match) {
                //记住密码
                editor = pref.edit();
                //同意协议的选线被选中
                if (remember.isChecked()) {
                    editor.putBoolean("remember_password", true);
                    editor.putString("name", name);
                    editor.putString("password", password);
                    //提示
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    editor.apply();
                    finish();//销毁此Activity
                } else {
                    Toast.makeText(this, "请先同意用户协议", Toast.LENGTH_SHORT).show();
                    //如果没有则清空
                    editor.clear();
                }
            } else {
                Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
        }


    }

    //注册事件,切换页面
    public void toRegister(View view) {
        intent = new Intent(LoginActivity.this, registerActivity.class);
        startActivity(intent);
    }
}
