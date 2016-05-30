//package com.example.yuanpeiyu.vpntest.uusafe;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.util.Log;
//
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.util.HashMap;
//import java.util.Set;
//
///**
// * Created by yuanpeiyu on 2016/4/22.
// */
//public class UuSafeManager {
//    private static UuSafeManager sInstance = new UuSafeManager();
//    private HashMap<String, Set<String>> mKeyWords = new HashMap<>();
//
//    private UuSafeManager() {}
//
//    public static UuSafeManager getInstance() {
//        return sInstance;
//    }
//
//    //this method must call in Application.attatchBaseContext()
//    public void initSdk(Context base) {
//        UUSandboxSdk.Init.setCallback(base, new UUSandboxSdk.SdkCallback() {
//            @Override
//            public void doLogin() {
//            }
//
//            @Override
//            public void reloadSettings(Set<String> set) {
//            }
//
//            @Override
//            public void onError(String s) {
//            }
//        });
//
//    }
//
//    public void start() {
//        UUSandboxSdk.Login.onLogin("aaa");
//        setKeyWord(null, "com.tencent.mobileqq");
//        setKeyWord(null, "com.tencent.mm");
//    }
//
//    public boolean setKeyWord(Set<String> keyWords, String pkgName) {
//        mKeyWords.put(pkgName, keyWords);
//        boolean ret = KeyWordManager.getInstance().addDefaultPerm(pkgName);
//        Log.d("ypy", "setKeyword ret  is " + ret);
//        return false;
//    }
//
//
//    public String getIMRecord() throws FileNotFoundException {
//        String path = UUSandboxSdk.Record.getLatestChatRecord("com.tencent.mobileqq");
//        if (!TextUtils.isEmpty(path)) {
//            return FileUtils.readStream(new FileInputStream(new File(path)));
//        } else {
//            Log.d("ypy", "getIMRecord return path is null" );
//        }
//        return null;
//    }
//}
