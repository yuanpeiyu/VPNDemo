package com.example.yuanpeiyu.vpntest;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by yuanpeiyu on 2016/3/16.
 */
public class CallLogUsage {

    private static final String TAG = "ypy";
    private static final CallLogUsage sInstance = new CallLogUsage();

    private Context mContext;
    private CallLogBean mLastCallLog;

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            onCallLogChanged(uri);
        }
    };

    class CallLogBean implements SerializableBean {

        public String number;
        public String contact;
        public int duration;
        public Integer type;
        public Long startTime;
        public int _id;

        @Override
        public byte[] toBinary() {
            return SerializableBeanUtils.convertObjectToGsonStringBinary(this);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o) || (o instanceof CallLogBean && isSame((CallLogBean) o));
        }

        private boolean isSame(CallLogBean s) {
            return this._id == s._id;
        }
    }

    private CallLogUsage() {

    }

    public static CallLogUsage getInstance() {
        return sInstance;
    }

    public void start(Context context) {
        mContext = context;
        mContext.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mObserver);
    }

    private void onCallLogChanged(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, "_id DESC");
        if (cursor != null) {
            if (cursor.moveToNext()) {
                Log.d(TAG, "\n");
                int count = cursor.getColumnCount();
                for (int i = 0; i < count ; i++) {
                    if (cursor.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
                        Log.d(TAG, cursor.getColumnName(i) + "  " + cursor.getInt(i));
                    } else if (cursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
                        Log.d(TAG, cursor.getColumnName(i) + "  " + cursor.getString(i));
                    }
                }
                Log.d(TAG, "\n");
                CallLogBean bean = new CallLogBean();
                bean._id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                if (mLastCallLog != null && mLastCallLog._id == bean._id) {
                    Log.d(TAG, "mLastCallLog is " + new String(mLastCallLog.toBinary())
                            + " , current bean is " + new String(bean.toBinary()));
                    return;
                } else {
                    bean.duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                    bean.startTime = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                    bean.number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    bean.contact = getContactNameByPhoneNumber(bean.number);
                    bean.type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                    String json = new String(bean.toBinary());
                    Log.d(TAG, "bean is " + json);
                    mLastCallLog = bean;
                }
            } else {
                Log.d(TAG, "query " + uri + " cursor moveToNext return false");
            }
            cursor.close();
        } else {
            Log.d(TAG, "query " + uri + " cursor is null");
        }
    }

    private String getContactNameByPhoneNumber(String address) {
        String name = null;
        if (!TextUtils.isEmpty(address)) {
            String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.NUMBER };
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(address));
            Cursor cursor = mContext.getContentResolver().query(uri, projection,
                    null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    int nameFieldColumnIndex = cursor
                            .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                    name = cursor.getString(nameFieldColumnIndex);
                }
                cursor.close();
            } else {
                Log.d(TAG, "getPeople null");
            }
        }
        return name;
    }
}
