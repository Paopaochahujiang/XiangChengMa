package com.baiyun.xiangchengma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

public class NfcDemoActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView tvUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取显示文本框
        tvUid = (TextView) findViewById(R.id.tv_uid);
        //获取NfcAdapter实例
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //获取通知
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        //如果获取不到则不支持NFC
        if (nfcAdapter == null) {
            Toast.makeText(NfcDemoActivity.this,"设备不支持NFC",Toast.LENGTH_LONG).show();
            return;
        }
        //如果获取到的为不可用状态则未启用NFC
        if (nfcAdapter!=null&&!nfcAdapter.isEnabled()) {
            Toast.makeText(NfcDemoActivity.this,"请在系统设置中先启用NFC功能",Toast.LENGTH_LONG).show();
            return;
        }
        //因为启动模式是singleTop，于是会调用onNewIntent方法
        onNewIntent(getIntent());

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //获取、传递、解析intent对象，intent中携带卡对象
        resolveIntent(intent);
    }

    //解析intent
    void resolveIntent(Intent intent) {
        //获取intent中携带的标签对象
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            //处理标签对象
            processTag(intent);
        }
    }

    //字节数组转换十六进制
    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F" };
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }


    //处理tag
    public void processTag(Intent intent) {
        //获取到卡对象
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //获取卡id这里即uid，字节数组类型
        byte[] aa = tagFromIntent.getId();
        //字节数组转十六进制字符串
        String str = ByteArrayToHexString(aa);
        tvUid.setText(str);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            //设置程序不优先处理
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            //设置程序优先处理
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    null, null);

    }
}