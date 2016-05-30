package com.example.yuanpeiyu.vpntest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

/**
 * Created by yuanpeiyu on 2016/3/14.
 */
public class TestDeviceOwnerActivity extends Activity implements View.OnClickListener {

    private DevicePolicyManager mDpm;
    private ComponentName mAdmin;
    private EditText mEditView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_device_owner);
        mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//        mAdmin = new ComponentName(getApplicationContext(), MdmDeviceAdminReceiver.class);
        initView();
        registBluetoothReceiver();
    }

    private void registBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        BroadcastReceiver receiver = new BluetoothReceiver();
        registerReceiver(receiver, filter);
    }

    private void initView() {
        mEditView = (EditText) findViewById(R.id.spinner_view);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.set_default_launcher).setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.add == id) {
            mDpm.addUserRestriction(mAdmin, mEditView.getText().toString());
        } else if (R.id.clear == id) {
            mDpm.clearUserRestriction(mAdmin, mEditView.getText().toString());
        } else if (R.id.set_default_launcher == id) {
            /*ComponentName name = new ComponentName(getApplicationContext(), MainActivity.class);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MAIN);
            filter.addCategory(Intent.CATEGORY_HOME);
            mDpm.addPersistentPreferredActivity(mAdmin, filter, name);*/
//            mDpm.setSecureSetting(mAdmin, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF + "");
            sendSMS("10086", "shhhhhhhhhhhhhhhh");
        }
    }

    public void sendSMS(String phoneNumber, String message) {
        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                Log.d("ypy", "BluetoothReceiver receive action is " + action);
                if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                    int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -100);
                    Log.d("ypy", "Bluetooth scan mode is " + mode);
                    if (mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
//                        changeScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE);
                    } else if (mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        changeScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE);
                    }
                }
            }
        }

        private void changeScanMode(int mode) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            RefInvoke.invokeMethod("android.bluetooth.BluetoothAdapter", "setScanMode",
                    adapter, new Class[]{int.class}, new Object[]{mode});
        }
    }
}
