package com.example.yuanpeiyu.vpntest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressLint("HandlerLeak")
public class LogcatHelper {

    private static final String TAG = "GSMCellLocationActivity";
    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;
    private static boolean mLogcatHelperState = false;
    private final String mFileName = "cellInfo";
    private boolean mFileBlock = true;
    private String SaveFilePath;
    private int saveTime = 1 * 50 * 1000;
    private String filePath = "";
    private static final int DELETE_FILE = 1;
    private Handler mDeleteFileHandle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELETE_FILE:
                    stop();
                    deleteFile(filePath);
                    start();
                    break;
            }
            super.handleMessage(msg);
        }

    };

    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        if (flag) {
            Log.d(TAG, "delete ok");
        }
        return flag;
    }

    public void init(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + mFileName;
        } else {
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + mFileName;
        }
        Log.d(TAG, "PATH_LOGCAT " + PATH_LOGCAT);
        chackFileExist();
    }

    private void chackFileExist() {
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static LogcatHelper getInstance(Context context) {
        Log.d(TAG, "save log getInstance --------");
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
        Log.d(TAG, "save log mPId --------" + mPId);
    }

    public void start() {
        Log.d(TAG, "save log start --------");
        /*if (mLogcatHelperState == true) {
            return;
        }*/
        chackFileExist();
        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        }
        mLogcatHelperState = true;
        mLogDumper.start();
        /*if (mFileBlock == true) {
            mDeleteFileHandle.sendEmptyMessageDelayed(DELETE_FILE, saveTime);
        }*/
    }

    public void stop() {
        Log.d(TAG, "save log stop --------");
        if (mLogcatHelperState == false) {
            return;
        }
        mLogcatHelperState = false;
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
        if (mFileBlock == true) {
            mDeleteFileHandle.removeMessages(DELETE_FILE);
        }
    }

    public String getSaveFilePath() {
        return SaveFilePath;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-s");
        String date = format.format(new Date(System.currentTimeMillis()));
        Log.e("", "save log date  " + date);
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format1.format(new Date(System.currentTimeMillis()));
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            Log.d(TAG, "save log  LogDumper");
            mPID = pid;
            try {
                File mFile = new File(dir, "" + getFileName() + ".log");
                if (!mFile.exists()) {
                    try {
                        mFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                SaveFilePath = PATH_LOGCAT + "/" + getFileName() + ".log";
                filePath = mFile.getPath();
                Log.d(TAG, "save log filePath  " + filePath);
                out = new FileOutputStream(mFile);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            cmds = "logcat  -v threadtime | grep \"(" + mPID + ")\"";// 打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            // cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";

            Log.d(TAG, "save log  cmds  " + cmds);
        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            Log.d(TAG, "save log  run");
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()));
                out.write("log record begin \n".getBytes());
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((/*getDateEN() + "  " + */line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }
}
