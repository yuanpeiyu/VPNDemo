package com.example.yuanpeiyu.vpntest;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.WindowManager;

/**
 * Created by yuanpeiyu on 2016/1/9.
 */
public class MyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        CellIdProvider.getInstance(getApplicationContext());
//        sendAlarm();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/
/*                AlertDialog.Builder builder = new AlertDialog.Builder(MyService.this);
                builder.setTitle("test");
                builder.setMessage("sssssssssssssssssssssss");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();*/
//            }
//        }, 10 * 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(MyService.this);
        builder.setTitle("test");
        builder.setMessage("sssssssssssssssssssssss");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();*/
        return START_STICKY;
    }

    public void sendAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerMillions = System.currentTimeMillis();
        Intent intent = new Intent("com.ypy.test");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerMillions, 30 * 1000, pendingIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, MyService.class));
    }
}
