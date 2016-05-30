package com.example.yuanpeiyu.vpntest;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.telephony.*;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanpeiyu on 2016/1/9.
 */
public class CellIdProvider {
    private static final String TAG = "CellIdProvider";
    private static final int MSG_REGIST_PHONE_STATE_LISTENER = 0x001;
    private static CellIdProvider sInstance;
    private TelephonyManager mTelephonyManager;
    private List<String> mCurrentCellIds = new ArrayList<>();
    private List<CellInfoChangeListener> mListeners;
    private Handler mHandler;

    interface CellInfoChangeListener {
        void onCellInfoChanged();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);
            Log.d(TAG, "onCellLocationChanged");
            getCellIds();
            synchronized (CellIdProvider.this) {
                if (mListeners != null && mListeners.size() > 0) {
                    for (CellInfoChangeListener listener : mListeners) {
                        listener.onCellInfoChanged();
                    }
                }
            }
        }

        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            super.onCellInfoChanged(cellInfo);
            Log.d(TAG, "onCellInfoChanged");
            getCellIds();
            synchronized (CellIdProvider.this) {
                if (mListeners != null && mListeners.size() > 0) {
                    for (CellInfoChangeListener listener : mListeners) {
                        listener.onCellInfoChanged();
                    }
                }
            }
        }
    }

    private CellIdProvider(Context context){
        mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mHandler = new Handler(thread.getLooper()) {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                CellIdProvider.this.handleMessage(msg);
            }
        };
        mHandler.sendEmptyMessage(MSG_REGIST_PHONE_STATE_LISTENER);
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_REGIST_PHONE_STATE_LISTENER: {
                mTelephonyManager.listen(new MyPhoneStateListener(),
                        PhoneStateListener.LISTEN_CELL_LOCATION);
                /*mTelephonyManager.listen(new MyPhoneStateListener(),
                        PhoneStateListener.LISTEN_CELL_INFO);*/
                break;
            }
        }
    }

    public static CellIdProvider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CellIdProvider(context);
        }
        return sInstance;
    }

    public synchronized List<String> getCellId() {
        if (mCurrentCellIds.size() == 0) {
            getCellIds();
        }
        return mCurrentCellIds;
    }

    private synchronized void getCellIds() {
        mCurrentCellIds.clear();
        GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
        if (location != null) {
            int lac = location.getLac();
            int cellId = location.getCid();
            mCurrentCellIds.add(cellId + "");
            Log.d(TAG, "LAC = " + lac + "\t CID = " + cellId);
        } else {
            Log.d(TAG, "get current connect cell id fail");
        }

        List<NeighboringCellInfo> infos = mTelephonyManager.getNeighboringCellInfo();
        StringBuffer sb = new StringBuffer("NeighboringCellInfo size is : " + infos.size() + "\n");
        for (NeighboringCellInfo info : infos) { // 根据邻区总数进行循环
            sb.append(" LAC : " + info.getLac()); // 取出当前邻区的LAC
            sb.append(" CID : " + info.getCid()); // 取出当前邻区的CID
            mCurrentCellIds.add(info.getCid() + "");
        }
        Log.d(TAG, "NeighboringCellInfo:" + sb.toString());
    }

    public synchronized void addListener(CellInfoChangeListener listener) {
        if (listener != null) {
            if (mListeners == null) {
                mListeners = new ArrayList<>();
            }
            mListeners.add(listener);
        }
    }

    public synchronized void removeListener(CellInfoChangeListener listener) {
        if (listener != null) {
            if (mListeners != null) {
                mListeners.remove(listener);
            }
        }
    }
}
