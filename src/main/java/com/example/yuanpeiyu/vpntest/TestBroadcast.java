package com.example.yuanpeiyu.vpntest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by yuanpeiyu on 2016/1/18.
 */
public class TestBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            Log.d("ypy","receive action is " + action);
        } else {
            Log.d("ypy","receive action but intent is null");
        }
    }
}
