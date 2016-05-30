package com.example.yuanpeiyu.vpntest;

import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by yuanpeiyu on 2016/3/16.
 */
public class SmsMmsUsage {

    private static final String TAG = "ypy";

    private static final Uri URI_MMS_SMS = Uri.parse("content://mms-sms");
    private static final Uri URI_SMS = Uri.parse("content://sms");
    private static final Uri URI_MMS = Uri.parse("content://mms");
    private static final int SMS_TYPE_INBOX = 1;
    private static final int SMS_TYPE_SENT = 2;
    private static final int MMS_TYPE_INBOX = 1;
    private static final int MMS_TYPE_SENT = 2;
    private static final String KEY_SMS_TYPE = "type";
    private static final String KEY_SMS_ADDRESS = "address";
    private static final String KEY_MMS_BOX = "msg_box";
    private static final String KEY_ID = "_id";
    private static final String KEY_THREAD_ID = "thread_id";
    private static final String KEY_MMS_ADDR_TYPE = "type";
    private static final String KEY_MMS_ADDR_ADDRESS = "address";
    private static final String KEY_MMS_TYPE = "m_type";
    private static final String MMS_ADDR = "addr";
    private static final int MMS_ADDR_TYPE_FROM = 137;
    private static final int MMS_ADDR_TYPE_TO = 151;

    public static final int MESSAGE_TYPE_SEND_REQ = 0x80;
    public static final int MESSAGE_TYPE_SEND_CONF = 0x81;
    public static final int MESSAGE_TYPE_RETRIEVE_CONF = 0x84;

    private static final int TYPE_SMS = 0;
    private static final int TYPE_MMS = 1;
    private static final int TYPE_RECEIVE = 0;
    private static final int TYPE_SEND = 1;

    private final HandlerThread mHandlerThread = new HandlerThread(TAG);
    private MmsSms mLastMms;
//    private MmsSms mLastSendMms;
    private Handler mHandler;

    class MmsSms implements SerializableBean {
        public String number;
        public String contact;
        public Integer type;
        public Integer smsSendReceive;
        public Long startTime;
        public int _id;
        public int threadId;

        @Override
        public byte[] toBinary() {
            return SerializableBeanUtils.convertObjectToGsonStringBinary(this);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o) || (o instanceof MmsSms && isSame((MmsSms) o));
        }

        private boolean isSame(MmsSms s) {
            return this._id == s._id
                    && this.threadId == s.threadId
                    && this.type == s.type;
        }
    }

    private static final SmsMmsUsage sInstance = new SmsMmsUsage();
    private Context mContext;

    private static final int SMS_ALL_ID = 1;
    private static final int MMS_ALL= 2;
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI("sms", "#", SMS_ALL_ID);
        uriMatcher.addURI("mms-sms", null, MMS_ALL);
    }

    private ContentObserver mMmsObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, "=====================mms Observer selfChange is " + selfChange + ", uri is " + uri);
            if (uriMatcher.match(uri) == MMS_ALL) {
                mHandler.sendEmptyMessage(MyHandler.MESSAGE_MMS_CHANGED);
            }
        }
    };

    private ContentObserver mSmsObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, "=====================sms Observer selfChange is " + selfChange + ", uri is " + uri);
            if (uriMatcher.match(uri) == SMS_ALL_ID) {
                Message message = Message.obtain();
                message.what = MyHandler.MESSAGE_SMS_CHANGED;
                message.obj = uri;
                mHandler.sendMessage(message);
            }
        }
    };

    class MyHandler extends Handler {

        public static final int MESSAGE_MMS_CHANGED = 0x001;
        public static final int MESSAGE_SMS_CHANGED = 0x002;

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_MMS_CHANGED :
                    checkMms();
                    break;
                case MESSAGE_SMS_CHANGED :
                    checkSms((Uri) msg.obj);
                    break;
            }
        }
    }

    private SmsMmsUsage() {

    }

    public static SmsMmsUsage getInstance() {
        return sInstance;
    }

    public void start(Context context) {
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());
        mContext = context;
        mContext.getContentResolver().registerContentObserver(URI_MMS_SMS, false, mMmsObserver);
        mContext.getContentResolver().registerContentObserver(URI_SMS, true, mSmsObserver);

        checkSms(Uri.parse("content://sms/143"));
    }

    private void checkSms(Uri uri) {
        Log.d(TAG, "start query " + uri);
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            Log.d(TAG, "query " + uri + " cursor count is " + cursor.getCount());
            if (cursor.moveToNext()) {
                MmsSms mmsSms = new MmsSms();
                int type = cursor.getInt(cursor.getColumnIndex(KEY_SMS_TYPE));
                if (SMS_TYPE_INBOX == type) {
                    mmsSms.smsSendReceive = TYPE_RECEIVE;
                } else if (SMS_TYPE_SENT == type){
                    mmsSms.smsSendReceive = TYPE_SEND;
                } else {
                    Log.d(TAG, "Type error : query " + uri + " msg type is " + type);
                    cursor.close();
                    return;
                }
                mmsSms.type = TYPE_SMS;
                mmsSms._id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                mmsSms.threadId = cursor.getInt(cursor.getColumnIndex(KEY_THREAD_ID));
                mmsSms.number = cursor.getString(cursor.getColumnIndex(KEY_SMS_ADDRESS));
                mmsSms.contact = getContactNameByPhoneNumber(mmsSms.number);
                mmsSms.startTime = cursor.getLong(cursor.getColumnIndex("date"));
                Log.d(TAG, "sms is " + new String(mmsSms.toBinary()));
            } else {
                Log.d(TAG, "query " + uri + " cursor moveToNext return false");
            }
            cursor.close();
        } else {
            Log.d(TAG, "query " + uri + " cursor is null");
        }
    }

    private void checkMms() {
        Log.d(TAG, "start query " + URI_MMS);
        Cursor cursor = mContext.getContentResolver().query(URI_MMS, null, null, null, null);
        if (cursor != null) {
            Log.d(TAG, "query " + URI_MMS + " cursor count is " + cursor.getCount());
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
                MmsSms mmsSms = new MmsSms();
                int type = cursor.getInt(cursor.getColumnIndex(KEY_MMS_BOX));
                int msg_type = cursor.getInt(cursor.getColumnIndex(KEY_MMS_TYPE));
                int addrType = -1;
                if (MMS_TYPE_INBOX == type && msg_type == MESSAGE_TYPE_RETRIEVE_CONF) {
                    mmsSms.smsSendReceive = TYPE_RECEIVE;
                    addrType = MMS_ADDR_TYPE_FROM;
                } else if (MMS_TYPE_SENT == type
                        && (msg_type == MESSAGE_TYPE_SEND_REQ
                        || msg_type == MESSAGE_TYPE_SEND_CONF)){
                    mmsSms.smsSendReceive = TYPE_SEND;
                    addrType = MMS_ADDR_TYPE_TO;
                } else {
                    Log.d(TAG, "Type error : query " + URI_MMS + " msg type is " + type
                            + " m_type is " + msg_type);
                    cursor.close();
                    return;
                }
                mmsSms.type = TYPE_MMS;
                mmsSms._id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                mmsSms.threadId = cursor.getInt(cursor.getColumnIndex(KEY_THREAD_ID));
                mmsSms.number = getMmsNumber(mmsSms._id, addrType);
                mmsSms.contact = getContactNameByPhoneNumber(mmsSms.number);
                mmsSms.startTime = cursor.getLong(cursor.getColumnIndex("date")) * 1000;
                if (mLastMms != null && mmsSms.equals(mLastMms)) {
                    Log.d(TAG, "mLastMms is " + new String(mLastMms.toBinary())
                            + " , current mmsSms is " + new String(mmsSms.toBinary()));
                } else {
                    mLastMms = mmsSms;
                    Log.d(TAG, "mms is " + new String(mmsSms.toBinary()));
                }
            } else {
                Log.d(TAG, "query " + URI_MMS + " cursor moveToNext return false");
            }
            cursor.close();
        } else {
            Log.d(TAG, "query " + URI_MMS + " cursor is null");
        }
    }

    private String getMmsNumber(int id, int type) {
        String number = null;
        Uri uri = new Uri.Builder().scheme("content").authority("mms")
                .appendPath(id + "").appendPath(MMS_ADDR).build();
        String selection = KEY_MMS_ADDR_TYPE + " = " + type;
        Log.d(TAG, "start query " + uri);
        Cursor cursor = mContext.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            Log.d(TAG, "query " + uri + " cursor count is " + cursor.getCount());
            if (cursor.moveToNext()) {
                number = cursor.getString(cursor.getColumnIndex(KEY_MMS_ADDR_ADDRESS));
            } else {
                Log.d(TAG, "query " + uri + " cursor moveToNext return false");
            }
            cursor.close();
        } else {
            Log.d(TAG, "query " + uri + " cursor is null");
        }
        return number;
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
