package com.jianjia.firewall.service;

import android.util.Log;

/**
 * Created by yuanpeiyu on 2016/5/26.
 */
public class FirewallVpnService {

    private native void startTunnel(int fd);

    private native void stopTunnel();

    private static FirewallVpnService sInstance = new FirewallVpnService();

    public static FirewallVpnService getInstance() {
        return sInstance;
    }

    public void stopTunnel2() {
        stopTunnel();
    }

    public void startTunnel2(int fd) {
        startTunnel(fd);
    }

    public void log(String msg) {
        Log.d("ypy", msg);
    }

    public void accessWebsite(String str) {
        log("call accessWebsite str is " + str);
    }

    public int[] isPermitAccess(int i) {
        log("call isPermitAccess i is " + i);
        return new int[]{i, 2};
    }

    public void onTimeout(int i) {
        log("call onTimeout i is " + i);
    }

    public int matchDomain(String str) {
        log("call matchDomain str is " + str);
        return 1;
    }

    public int matchIp(int ip) {
        log("call matchIp ip is " + ip);
        return 1;
    }
}

