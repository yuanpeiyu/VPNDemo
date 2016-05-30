package com.example.yuanpeiyu.vpntest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yuanpeiyu on 2016/1/9.
 */
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "ypy";
    private static final Uri URI_SMS_MMS = Uri.parse("content://mms-sms");
    private static final Uri URI_SMS = Uri.parse("content://sms");
    private static final Uri URI_MMS = Uri.parse("content://mms");
    private static final Uri URI_SMS_INBOX = Uri.parse("content://sms/inbox");
    private static final Uri URI_MMS_INBOX = Uri.parse("content://mms/inbox");
    private static final Uri URI_SMS_SENT = Uri.parse("content://sms/sent");
    private static final Uri URI_MMS_SENT = Uri.parse("content://mms/sent");

    private static final Uri URI_TEST = Uri.parse("content://test");

    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, "=====================sms selfChange is " + selfChange + ", uri is " + uri);
            Log.d("ypy", "===================== notifyChange id is " + Thread.currentThread().toString());
//            checkSms(uri);
        }
    };

    private ContentObserver mmsObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, "=====================mms selfChange is " + selfChange + ", uri is " + uri);
            checkMms(URI_MMS_INBOX);
            checkMms(URI_MMS_SENT);
        }
    };

    private void checkSms(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            Log.d(TAG, "===================== " + uri + " + cursor count is " + cursor.getCount());
            if (cursor.moveToNext()) {
                Log.d(TAG, "===================== start");
                int count = cursor.getColumnCount();
                for (int i = 0; i < count ; i++) {
                    if (cursor.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
                        Log.d(TAG, cursor.getColumnName(i) + "  " + cursor.getInt(i));
                    } else if (cursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
                        Log.d(TAG, cursor.getColumnName(i) + "  " + cursor.getString(i));
                    }
                }
                String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                String contact = getContactNameByPhoneNumber(address);
                Log.d(TAG, address + " = " + contact);
                Log.d(TAG, "===================== end");
                Log.d(TAG, "");
            }
        } else {
            Log.d(TAG, "===================== cursor is null");
        }
    }

    private void checkMms(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            Log.d(TAG, "===================== " + uri + " cursor count is " + cursor.getCount());
            if (cursor.moveToNext()) {
                Log.d(TAG, "===================== start");
                int count = cursor.getColumnCount();
                for (int i = 0; i < count ; i++) {
                    if (cursor.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
                        Log.d(TAG, cursor.getColumnName(i) + "  " + cursor.getInt(i));
                    } else if (cursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
                        Log.d(TAG, cursor.getColumnName(i) + "  " + cursor.getString(i));
                    }
                }
                /*String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                String contact = getContactNameByPhoneNumber(address);
                Log.d(TAG, address + " = " + contact);*/
                Log.d(TAG, "===================== end");
                Log.d(TAG, "");
            }
        } else {
            Log.d(TAG, "===================== cursor is null");
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        Log.d("ypy", "===================== attachBaseContext");
        super.attachBaseContext(base);
//        UuSafeManager.getInstance().initSdk(getBaseContext());
        /*LogcatHelper.getInstance(this).start();
        UUSandboxSdk.Init.setCallback(base, new UUSandboxSdk.SdkCallback() {
            @Override
            public void doLogin() {

            }

            @Override
            public void reloadSettings(Set<String> set) {

            }

            @Override
            public void onKeywordHit(String s) {
                Toast.makeText(MyApplication.this, "receive keyword " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String s) {

            }
        });*/
    }

    private static final String PERM_JSON = "{\n" +
            "  \"content\": {\n" +
            "    \"desc\": \"app-permission\",\n" +
            "    \"ver\": 1,\n" +
            "    \"app\": {\n" +
            "      \"pkg\": \"com.tencent.mm\",\n" +
            "      \"ver\": 1,\n" +
            "      \"sdk_min\": 10,\n" +
            "      \"sdk_max\": 22,\n" +
            "      \"sign\": \"\",\n" +
            "      \"md5\": \"\"\n" +
            "    },\n" +
            "    \"permission\": {\n" +
            "      \"launch\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"autostart\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"bg_run\": {\n" +
            "        \"ctl\": 1,\n" +
            "        \"time_bg\": 5,\n" +
            "        \"time_lock\": 1,\n" +
            "        \"clean\": 1\n" +
            "      },\n" +
            "      \"notification\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"sms\": {\n" +
            "        \"send\": 1,\n" +
            "        \"query\": 1,\n" +
            "        \"update\": 1\n" +
            "      },\n" +
            "      \"call\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"call_logs\": {\n" +
            "        \"query\": 1,\n" +
            "        \"update\": 1\n" +
            "      },\n" +
            "      \"contacts\": {\n" +
            "        \"query\": 1,\n" +
            "        \"update\": 1\n" +
            "      },\n" +
            "      \"input\": {\n" +
            "        \"ctl\": 2\n" +
            "      },\n" +
            "      \"sensor\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"phone\": {\n" +
            "        \"number\": 1\n" +
            "      },\n" +
            "      \"location\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"net\": {\n" +
            "        \"m_switch\": 1,\n" +
            "        \"mobile\": 1,\n" +
            "        \"w_switch\": 1,\n" +
            "        \"wifi\": 1,\n" +
            "        \"act\": {\n" +
            "          \"on\": 1,\n" +
            "          \"out\": 1,\n" +
            "          \"dft\": 1\n" +
            "        },\n" +
            "        \"lst\": []\n" +
            "      },\n" +
            "      \"audio\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"camera\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"bluetooth\": {\n" +
            "        \"switch\": 1,\n" +
            "        \"use\": 1\n" +
            "      },\n" +
            "      \"window\": {\n" +
            "        \"shots\": 1\n" +
            "      },\n" +
            "      \"range_zone\": {\n" +
            "        \"ctl\": 1,\n" +
            "        \"act\": {\n" +
            "          \"on\": 1,\n" +
            "          \"out\": 2,\n" +
            "          \"dft\": 1\n" +
            "        },\n" +
            "        \"lst\": []\n" +
            "      },\n" +
            "      \"range_time\": {\n" +
            "        \"ctl\": 1,\n" +
            "        \"act\": {\n" +
            "          \"on\": 1,\n" +
            "          \"out\": 2,\n" +
            "          \"dft\": 1\n" +
            "        },\n" +
            "        \"lst\": []\n" +
            "      },\n" +
            "      \"clipboard\": {\n" +
            "        \"ctl\": 2\n" +
            "      },\n" +
            "      \"mail\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"print\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"file\": {\n" +
            "        \"ctl\": 1,\n" +
            "        \"mode\": \"aes256\"\n" +
            "      },\n" +
            "      \"media_res\": {\n" +
            "        \"ctl\": 1\n" +
            "      },\n" +
            "      \"web\": {\n" +
            "        \"urlbw\": 1,\n" +
            "        \"urlrcd\": 1\n" +
            "      },\n" +
            "      \"vpn\": {\n" +
            "        \"ctl\": 1,\n" +
            "        \"type\": \"sangfor\",\n" +
            "        \"ip\": \"\",\n" +
            "        \"port\": \"\",\n" +
            "        \"name\": \"\",\n" +
            "        \"pwd\": \"\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Override
    public void onCreate() {
        super.onCreate();
        /*LogcatHelper.getInstance(getApplicationContext()).start();*/
//        UUSandboxSdk.Login.onLoginSucc("yyyyyyyyyyy");
//        UUSandboxSdk.Permissions.setAppPerm(PERM_JSON);
//        HashSet<String> words = new HashSet<>();
//        words.add("ccc");
//        boolean ret = UUSandboxSdk.Keyword.setKeywords(words);
//        Log.d("ypy", "set keywords ret is " + ret);
//        startService(new Intent(getApplicationContext(), MyService.class));
//        getContentResolver().registerContentObserver(URI_SMS_MMS, false, mmsObserver);
//        getContentResolver().registerContentObserver(URI_SMS, true, observer);
//        getContentResolver().registerContentObserver(URI_MMS, true, mmsObserver);
//        getContentResolver().registerContentObserver(URI_SMS_INBOX, false, observer);
//        getContentResolver().registerContentObserver(URI_SMS_SENT, false, observer);
//        getContentResolver().registerContentObserver(URI_MMS_INBOX, false, mmsObserver);
//        getContentResolver().registerContentObserver(URI_MMS_SENT, false, mmsObserver);
//        getContentResolver().registerContentObserver(URI_MMS_OUTBOX, true, observer);
//        SmsMmsUsage.getInstance().start(this);
//        CallLogUsage.getInstance().start(this);
        /*getContentResolver().registerContentObserver(URI_TEST, false, observer);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       Log.d("ypy", "===================== notifyChange id is " + Thread.currentThread().toString());
                       getContentResolver().notifyChange(URI_TEST, null);
                   }
               }).start();
            }
        }, 3000);*/
        /*Log.d("ypy", "===================== onCreate");
        Log.d("ypy", "===================== onCreate");
        Log.d("ypy", "===================== onCreate");
        Log.d("ypy", "===================== onCreate");
        Log.d("ypy", "===================== onCreate");
        Log.d("ypy", "===================== onCreate");
        Log.d("ypy", "===================== onCreate");
        Object systemConfig = RefInvoke.invokeStaticMethod("com.android.server.SystemConfig",
                "getInstance", new Class[]{}, new Object[]{});
        Log.d("ypy", "===================== " + Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, "5"));
        Uri uri = Uri.parse("content://com.pekall.emdm/s/s/sd/sad/asdsa");
        Log.d("ypy", "===================== getAuthority " + uri.getAuthority());
        Log.d("ypy", "===================== getPath " + uri.getPath());*/
        /*Map<String, Object> features = (Map<String, Object>) RefInvoke.getFieldOjbect("com.android.server.SystemConfig",
                systemConfig, "mAvailableFeatures");
        features.put("android.hardware.camera", null);*/
        /*List<String> fields = RefInvoke.getAllFieldOjbect("com.android.server.SystemConfig", systemConfig);
        if (fields != null && fields.size() > 0) {
            for (String s : fields) {
                Log.d("ypy", "===================== field is " + s);
            }
        } else {
            Log.d("ypy", "===================== fields is null");
        }*/
//        registerActivityLifecycleCallbacks(this);
//        TestClass.main(null);
        /*boolean remote = !getPackageName().equals(currentProcessName().trim());
        if (remote) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("ypy", "Sandbox engin is " + UUSandboxSdk.Sandbox.getEngineVersion());
                UuSafeManager.getInstance().start();
            }
        }, 5000);*/
    }

    public static String currentProcessName() {
        String cmdline = String.format("/proc/%d/cmdline", Binder.getCallingPid());
        String procName = catFile(cmdline);
        return procName;
    }

    public static String catFile(String filename) {
        File file = new File(filename);
        StringBuffer stringBuffer = new StringBuffer();
        if (file.exists()) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeQuietly(bufferedReader);
            }
        }
        return stringBuffer.toString();
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    private String getContactNameByPhoneNumber(String address) {
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, // Which columns to return.
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
                        + address + "'", // WHERE clause.
                null, // WHERE clause value substitution
                null); // Sort order.
        if (cursor == null) {
            Log.d(TAG, "getPeople null");
            return null;
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int nameFieldColumnIndex = cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            return name;
        }
        return null;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d("ypy", "============================= onActivityCreated " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("ypy", "============================= onActivityStarted " + activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("ypy", "============================= onActivityResumed " + activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("ypy", "============================= onActivityPaused " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d("ypy", "============================= onActivityStopped " + activity.getLocalClassName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d("ypy", "============================= onActivitySaveInstanceState " + activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d("ypy", "============================= onActivityDestroyed " + activity.getLocalClassName());
    }
}
