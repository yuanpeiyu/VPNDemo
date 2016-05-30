package com.example.yuanpeiyu.vpntest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuanpeiyu.vpntest.localVPN.LocalVPNService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by yuanpeiyu on 2016/1/18.
 */
public class TestMainActivity extends Activity implements View.OnClickListener {

    private TextView mDataView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (int i = 0; i < 30; i++) {
                Log.d("ypy", "in handler i is " + i);
                SystemClock.sleep(100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        mDataView = (TextView) findViewById(R.id.data);
        Intent intent = getIntent();
        getData(intent);
        findViewById(R.id.switch_screen).setOnClickListener(this);
//        sendNotify();
        /*mHandler.sendEmptyMessage(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("ypy", "in Thread looper.loop 1");
                Looper.prepare();
                Log.d("ypy", "in Thread looper.loop 2");
                Looper looper = Looper.myLooper();
                Log.d("ypy", "in Thread looper.loop 3");
                new Handler(looper){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                    }
                };
                Log.d("ypy", "in Thread looper.loop 4");
                looper.loop();
                Log.d("ypy", "in Thread looper.loop 5");
            }
        }).start();*/

        getWindowParams();
//        startActivity(new Intent(this, MainActivity.class));
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void getWindowParams() {
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Log.d("ypy", "metrics.density: " + metrics.density
                + ",metrics.densityDpi: " + metrics.densityDpi
                + ",metrics.xdpi: " + metrics.xdpi
                + ",metrics.ydpi: " + metrics.ydpi
                + ",metrics.widthPixels: " + metrics.widthPixels
                + ",metrics.heightPixels: " + metrics.heightPixels
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ypy", "in onResume");
        /*for (int i = 0; i < 30; i++) {
            Log.d("ypy", "in onResume i is " + i);
            SystemClock.sleep(100);
        }*/
    }

    private void buildVpn() {
        if (VpnService.prepare(this) != null) {
            startActivityForResult(VpnService.prepare(this), 0);
        } else {
            startService(new Intent(this, LocalVPNService.class));
//            startService(new Intent(this, ToyVpnService.class));
        }
//        invokeStaticMethod(VpnService.class.getName(), "prepareAndAuthorize", new Class[]{Context.class}, new Object[]{this});
    }

    public static Object invokeStaticMethod(String class_name, String method_name, Class[] pareTyple, Object[] pareVaules) {

        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getDeclaredMethod(method_name, pareTyple);
            return method.invoke(null, pareVaules);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ypy", "=================== onActivityResult");
        buildVpn();
    }

    private void sendNotify() {
        Log.d("ypy", "=================== sendNotify");
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("test")
                .setContentText("test notify")
                .setTicker("just do it")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false);
        builder.setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_NO_CLEAR;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }

    private void getData(Intent intent) {
        if (intent != null) {
            String ret = intent.getStringExtra("test");
            Log.d("ypy", "ret is " + ret);
            if (!TextUtils.isEmpty(ret)) {
                mDataView.setText(String.valueOf(ret));
            }
        } else {
            Log.d("ypy", "intent is null");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getData(intent);
        setIntent(intent);
        getData(getIntent());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.switch_screen == id) {
//            startActivity(new Intent(this, GSMCellLocationActivity.class));
//            showDialog();
//            startActivity(new Intent(this, MainActivity.class));
//            startActivity(new Intent(Intent.ACTION_VIEW));
            buildVpn();
            /*try {
                Log.d("ypy", "click button" );
                String record = UuSafeManager.getInstance().getIMRecord();
                if (TextUtils.isEmpty(record)) {
                    mDataView.setText("没有获取到记录");
                } else {
                    mDataView.setText(record);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    testInnerNet();
                }
            }).start();*/
        }
    }

    private void testInnerNet() {
        String url = "https://192.168.10.28:4432";
        try {
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{sTrustManager}, new SecureRandom());
                HttpsURLConnection
                        .setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(sHostnameVerifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();
            int code = connection.getResponseCode();
            Log.d("ypy", "response code is " + code);
            String response = (String) connection.getContent();
            Log.d("ypy", "response content is " + response);
            /*if (code == 200) {
                String response = (String) connection.getContent();
                Log.d("ypy", "response content is " + response);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        /*Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        dialog.show();*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");
//        builder.setMessage("This is test dialog");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);
        TextView tv = new TextView(this);
        tv.setText("========asdsadsamdskald");
        builder.setView(tv);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "点击了确定按钮",
                        Toast.LENGTH_SHORT).show();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "点击了取消按钮",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static HostnameVerifier sHostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private static TrustManager sTrustManager = new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(
                X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(
                X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    };

    public static HttpURLConnection openConnection(String url) throws MalformedURLException, IOException {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        HttpURLConnection conn;
        if (url.startsWith("https")) {
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{sTrustManager}, new SecureRandom());
                HttpsURLConnection
                        .setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(sHostnameVerifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn = (HttpsURLConnection) new URL(url).openConnection();
        } else {
            conn = (HttpURLConnection) new URL(url).openConnection();
        }
        return conn;
    }
}
