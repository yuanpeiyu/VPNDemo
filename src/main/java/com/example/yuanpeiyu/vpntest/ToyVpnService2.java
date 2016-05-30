/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.yuanpeiyu.vpntest;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import com.jianjia.firewall.service.FirewallVpnService;

import java.io.IOException;

public class ToyVpnService2 extends VpnService implements Runnable {

    private Thread mThread;
    private ParcelFileDescriptor mDescriptor;

    static {
        System.loadLibrary("Firewall");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new Thread(this);
        mThread.start();
        return START_STICKY;
    }

    private void configure() {
        Builder builder = new Builder();
        builder.setMtu(1500);
        builder.addAddress("26.26.26.26", 24);
        builder.addRoute("0.0.0.0", 0);
        builder.setSession("ypy");
        mDescriptor = builder.establish();
    }

    @Override
    public void run() {
        configure();
        FirewallVpnService.getInstance().startTunnel2(mDescriptor.getFd());
    }

    public void stop() {
        if (mDescriptor != null) {
            try {
                mDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirewallVpnService.getInstance().stopTunnel2();
        try {
            mThread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRevoke() {
        super.onRevoke();
        stop();
    }
}
