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

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ToyVpnService extends VpnService implements Handler.Callback, Runnable {
    private static final String TAG = "ypy";

    private String mServerAddress = "10.0.8.1";
    private String mServerPort = "32";
    private byte[] mSharedSecret = "yuanpeiyu".getBytes();
    private PendingIntent mConfigureIntent;

    private Handler mHandler;
    private Thread mThread;

    private ParcelFileDescriptor mInterface;
    private String mParameters;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            mThread.interrupt();
        }

        // Extract information from the intent.
//        String prefix = getPackageName();
//        mServerAddress = intent.getStringExtra(prefix + ".ADDRESS");
//        mServerPort = intent.getStringExtra(prefix + ".PORT");
//        mSharedSecret = intent.getStringExtra(prefix + ".SECRET").getBytes();

        // Start a new session by creating a new thread.
        Log.d(TAG, "=========== onStartCommand ");
        mThread = new Thread(this, "ToyVpnThread");
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public synchronized void run() {
        /*try {
            Log.i(TAG, "Starting");

            // If anything needs to be obtained using the network, get it now.
            // This greatly reduces the complexity of seamless handover, which
            // tries to recreate the tunnel without shutting down everything.
            // In this demo, all we need to know is the server address.
            InetSocketAddress server = new InetSocketAddress(
                    mServerAddress, Integer.parseInt(mServerPort));

            // We try to create the tunnel for several times. The better way
            // is to work with ConnectivityManager, such as trying only when
            // the network is avaiable. Here we just use a counter to keep
            // things simple.
            for (int attempt = 0; attempt < 10; ++attempt) {
                mHandler.sendEmptyMessage(R.string.connecting);

                // Reset the counter if we were connected.
                if (run(server)) {
                    attempt = 0;
                }

                // Sleep for a while. This also checks if we got interrupted.
                Thread.sleep(3000);
            }
            Log.i(TAG, "Giving up");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Got " + e.toString());
        } finally {
            try {
                mInterface.close();
            } catch (Exception e) {
                // ignore
            }
            mInterface = null;
            mParameters = null;

            mHandler.sendEmptyMessage(R.string.disconnected);
            Log.i(TAG, "Exiting");
        }*/
        configure2();
        while (true) {
            readData();
            SystemClock.sleep(1000);
        }

    }

    private void configure2() {
        Builder builder = new Builder();
        builder.setMtu(15000);
        builder.addAddress(mServerAddress, 32);
        builder.addRoute("0.0.0.0", 0);
        builder.setSession("ypy");
        mInterface = builder.establish();
    }

    private void readData() {
        FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
        byte[] buffer = new byte[1024 * 100];
        try {
            int length = in.read(buffer);
            if (length > 0) {
                byte[] buffer2 = new byte[length];
                for (int i = 0; i < length; i++ ) {
                    buffer2[i] = buffer[i];
                }
                logData(buffer2, true);
//                sendData(buffer2);
                decodeData(buffer2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decodeData(byte[] data) {
        FileOutputStream outputStream = null;
        DatagramChannel channel = null;
        try {
            outputStream = new FileOutputStream(mInterface.getFileDescriptor());
            channel = DatagramChannel.open();
            protect(channel.socket());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("IP version : " + Integer.toHexString(data[0]).charAt(0));
        int ipHeader = Integer.parseInt(Integer.toHexString(data[0]).substring(1, 2)) * 4;
        log("IP header length : " + ipHeader);
        log("Type Of Service : " + toHexString(data[1]));
        int ipDataLength = ((data[2] & 0xff) << 8) + (data[3] & 0xff);
        log("IP 报文长度 : " + ipDataLength);
        log("Identification : " + toHexString(data[4]) + toHexString(data[5]));
        log("DF MF Fragmeng Offset : " + Integer.toBinaryString(data[6]) + Integer.toBinaryString(data[7]));
        log("Time Of Live : " + toHexString(data[8]));
        log("TCP/UDP : " + toHexString(data[9]));
        log("Header Checksum : " + toHexString(data[10]) + toHexString(data[11]));
        log("Src IP : " + (data[12] & 0xFF) + "." + (data[13] & 0xFF) + "." + (data[14] & 0xFF) + "." + (data[15] & 0xFF));
        log("Dest IP : " + (data[16] & 0xFF) + "." + (data[17] & 0xFF) + "." + (data[18] & 0xFF) + "." + (data[19] & 0xFF));
        log("Src port : " + (((data[20] & 0xFF) << 8) + (data[21] & 0xFF)));
        log("Dest port : " + (((data[22] & 0xFF) << 8) + (data[23] & 0xFF)));

        String src = (data[12] & 0xFF) + "." + (data[13] & 0xFF) + "." + (data[14] & 0xFF) + "." + (data[15] & 0xFF);
        String dest = (data[16] & 0xFF) + "." + (data[17] & 0xFF) + "." + (data[18] & 0xFF) + "." + (data[19] & 0xFF);
        int srcPort = ((data[20] & 0xFF) << 8) + (data[21] & 0xFF);
        int destPort = ((data[22] & 0xFF) << 8) + (data[23] & 0xFF);
        int protocol =  data[9];
        int seq = ((data[24] & 0xFF) << 24) + ((data[25] & 0xFF) << 16) + ((data[26] & 0xFF) << 8) + ((data[27] & 0xFF));
        int ack = ((data[28] & 0xFF) << 24) + ((data[29] & 0xFF) << 16) + ((data[30] & 0xFF) << 8) + ((data[31] & 0xFF));
        log("seq num : " + seq);
        log("ack num : " + ack);
        int tcpHeader = ((data[32] & 240) >> 4) * 4;
        log("TCP header length : " + tcpHeader);
        int URG = (data[33] & 32) >> 5;
        int ACK = (data[33] & 16) >> 4;
        int PSH = (data[33] & 8) >> 3;
        int RST = (data[33] & 4) >> 2;
        int SYN = (data[33] & 2) >> 1;
        int FIN = (data[33] & 1);
        log("URG is : " + URG);
        log("ACK is : " + ACK);
        log("PSH is : " + PSH);
        log("RST is : " + RST);
        log("SYN is : " + SYN);
        log("FIN is : " + FIN);
/*        try {
//            channel.configureBlocking(false);
            if (SYN == 1 && ACK == 0) {
//                data[33] = Byte.parseByte("00010010", 2);
//                outputStream.write(data);
                channel.connect(new InetSocketAddress(dest, destPort));
                channel.send(ByteBuffer.wrap(data, ipHeader, data.length - ipHeader), new InetSocketAddress(dest, destPort));
            } else if (SYN == 1 && ACK == 1) {
//                channel.send(ByteBuffer.wrap(data, ipHeader, data.length - ipHeader), new InetSocketAddress(dest, destPort));
            }
            *//*byte[] buffer = new byte[1024 * 100];
            int length = channel.read(ByteBuffer.wrap(buffer));
            if (length > 0) {
                log("length is : " + length);
                try {
                    log(new String(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log("length is 0");
            }*//*
//            if (6 == protocol) {
                *//*if (ipHeader + tcpHeader == ipDataLength) {
                    log("deal tcp protocol");
                    socket.setSoTimeout(30000);
                    log("deal tcp protocol 1");
//                    socket.connect(new InetSocketAddress(dest, destPort));
                    log("deal tcp protocol 2");
                } else {
                    log("deal tcp protocol 3");
                    OutputStream out = socket.getOutputStream();
                    log("deal tcp protocol 4");
                    out.write(data);
                    log("deal tcp protocol 5");
                    InputStream in = socket.getInputStream();
                    log("deal tcp protocol 6");
                    byte[] buffer = new byte[1024 * 100];
                    int length = in.read(buffer);
                    if (length > 0) {
                        try {
                            Log.d(TAG, "receive Data : "+ new String(buffer, 0 , length, "UTF-8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    socket.close();
                    log("deal tcp protocol 7");
                }*//*


            *//*} else if (17 == protocol) {
                log("deal udp protocol");
                DatagramSocket datagramSocket = new DatagramSocket();
                protect(datagramSocket);
                log("deal udp protocol 1");
                datagramSocket.connect(new InetSocketAddress(dest, destPort));
                log("deal udp protocol 2");
                datagramSocket.send(new DatagramPacket(data, data.length));
                log("deal udp protocol 3");
                byte[] buffer = new byte[1024 * 100];
                datagramSocket.receive(new DatagramPacket(buffer, buffer.length));
                log("deal udp protocol 4");
            } else {
                log("unsupport protocol " + protocol);
            }*//*
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private String toHexString(int i) {
        return Integer.toHexString(i & 0xff);
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }

    private void sendData(byte[] buffer) {
        /*Socket socket = new Socket();
        socket.connect();
        protect(socket);*/
    }

    private void logData(byte[] buffer, boolean send) {
        Log.d(TAG, "buffer.length is " + buffer.length);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        InetAddress address = packet.getAddress();
//        logData2(buffer);
        try {
//            Log.d(TAG, (send ? "sendData : " : "receive Data : ") + address.toString());
            for (int i = 0; i < buffer.length; i++) {
                Log.d(TAG, "buffer[" + i + "] = " + (buffer[i] & 0xFF));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            Log.d(TAG, (send ? "sendData : " : "receive Data : ") + new String(packet.getData(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logData2(byte[] buffer) {
        int srcPort = getSrcPort(buffer);
        int desPort = getDesPort(buffer);
        Log.d(TAG, "srcIp is " + (buffer[96]&0x0FF) + "." + (buffer[97]&0x0FF)
                + "." + (buffer[98]&0x0FF) + "." + (buffer[99]&0x0FF));
        Log.d(TAG, "dest is " + (buffer[100]&0x0FF) + "." + (buffer[101]&0x0FF)
                + "." + (buffer[102]&0x0FF) + "." + (buffer[103]&0x0FF));
        try {
            Log.d(TAG, "srcPort is " + srcPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.d(TAG, "desPort is " + desPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getInt(byte[] bytes) {
        int value= 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            System.out.println("ypy byte " + i + " is " + bytes[i]);
            int shift= (4 - 1 - i) * 8;
            value +=(bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    public int getSrcPort(byte[] buffer) {
        byte[] bytes = new byte[4];
        bytes[2] = buffer[0];
        bytes[3] = buffer[1];
        bytes[0] = bytes[1] = 0;
        int value= 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    public int getDesPort(byte[] buffer) {
        byte[] bytes = new byte[4];
        bytes[2] = buffer[2];
        bytes[3] = buffer[3];
        bytes[0] = bytes[1] = 0;
        int value= 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    private boolean run(InetSocketAddress server) throws Exception {
        DatagramChannel tunnel = null;
        boolean connected = true;
        try {
            // Create a DatagramChannel as the VPN tunnel.
            tunnel = DatagramChannel.open();

            // Protect the tunnel before connecting to avoid loopback.
            if (!protect(tunnel.socket())) {
                throw new IllegalStateException("Cannot protect the tunnel");
            }

            // Connect to the server.
            tunnel.connect(server);

            // For simplicity, we use the same thread for both reading and
            // writing. Here we put the tunnel into non-blocking mode.
            tunnel.configureBlocking(false);

            // Authenticate and configure the virtual network interface.
            handshake(tunnel);

            // Now we are connected. Set the flag and show the message.
            connected = true;
            mHandler.sendEmptyMessage(R.string.connected);

            // Packets to be sent are queued in this input stream.
            FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());

            // Packets received need to be written to this output stream.
            FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());

            // Allocate the buffer for a single packet.
            ByteBuffer packet = ByteBuffer.allocate(32767);

            // We use a timer to determine the status of the tunnel. It
            // works on both sides. A positive value means sending, and
            // any other means receiving. We start with receiving.
            int timer = 0;

            // We keep forwarding packets till something goes wrong.
            while (true) {
                // Assume that we did not make any progress in this iteration.
                boolean idle = true;

                // Read the outgoing packet from the input stream.
                int length = in.read(packet.array());
                if (length > 0) {
                    // Write the outgoing packet to the tunnel.
                    packet.limit(length);
                    tunnel.write(packet);
                    packet.clear();

                    // There might be more outgoing packets.
                    idle = false;

                    // If we were receiving, switch to sending.
                    if (timer < 1) {
                        timer = 1;
                    }
                }

                // Read the incoming packet from the tunnel.
                length = tunnel.read(packet);
                if (length > 0) {
                    // Ignore control messages, which start with zero.
                    if (packet.get(0) != 0) {
                        // Write the incoming packet to the output stream.
                        out.write(packet.array(), 0, length);
                    }
                    packet.clear();

                    // There might be more incoming packets.
                    idle = false;

                    // If we were sending, switch to receiving.
                    if (timer > 0) {
                        timer = 0;
                    }
                }

                // If we are idle or waiting for the network, sleep for a
                // fraction of time to avoid busy looping.
                if (idle) {
                    Thread.sleep(100);

                    // Increase the timer. This is inaccurate but good enough,
                    // since everything is operated in non-blocking mode.
                    timer += (timer > 0) ? 100 : -100;

                    // We are receiving for a long time but not sending.
                    if (timer < -15000) {
                        // Send empty control messages.
                        packet.put((byte) 0).limit(1);
                        for (int i = 0; i < 3; ++i) {
                            packet.position(0);
                            tunnel.write(packet);
                        }
                        packet.clear();

                        // Switch to sending.
                        timer = 1;
                    }
                    // We are sending for a long time but not receiving.
                    /*if (timer > 20000) {
                        throw new IllegalStateException("Timed out");
                    }*/
                }
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Got " + e.toString());
        } finally {
            try {
                tunnel.close();
            } catch (Exception e) {
                // ignore
            }
        }
        return connected;
    }



    private void handshake(DatagramChannel tunnel) throws Exception {
        // To build a secured tunnel, we should perform mutual authentication
        // and exchange session keys for encryption. To keep things simple in
        // this demo, we just send the shared secret in plaintext and wait
        // for the server to send the parameters.

        // Allocate the buffer for handshaking.
        ByteBuffer packet = ByteBuffer.allocate(1024);

        // Control messages always start with zero.
        packet.put((byte) 0).put(mSharedSecret).flip();

        // Send the secret several times in case of packet loss.
        for (int i = 0; i < 3; ++i) {
            packet.position(0);
            tunnel.write(packet);
        }
        packet.clear();

        // Wait for the parameters within a limited time.
        for (int i = 0; i < 50; ++i) {
            Thread.sleep(100);

            // Normally we should not receive random packets.
            int length = tunnel.read(packet);
            if (length > 0 && packet.get(0) == 0) {
                configure(new String(packet.array(), 1, length - 1).trim());
                return;
            }
        }
//        throw new IllegalStateException("Timed out");
    }

    private void configure(String parameters) throws Exception {
        // If the old interface has exactly the same parameters, use it!
        if (mInterface != null && parameters.equals(mParameters)) {
            Log.i(TAG, "Using the previous interface");
            return;
        }

        // Configure a builder while parsing the parameters.
        Builder builder = new Builder();
        for (String parameter : parameters.split(" ")) {
            String[] fields = parameter.split(",");
            try {
                switch (fields[0].charAt(0)) {
                    case 'm':
                        builder.setMtu(Short.parseShort(fields[1]));
                        break;
                    case 'a':
                        builder.addAddress(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case 'r':
                        builder.addRoute(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case 'd':
                        builder.addDnsServer(fields[1]);
                        break;
                    case 's':
                        builder.addSearchDomain(fields[1]);
                        break;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Bad parameter: " + parameter);
            }
        }

        // Close the old interface since the parameters have been changed.
        try {
            mInterface.close();
        } catch (Exception e) {
            // ignore
        }

        // Create a new interface using the builder and save the parameters.
        mInterface = builder.setSession(mServerAddress)
                .setConfigureIntent(mConfigureIntent)
                .establish();
        mParameters = parameters;
        Log.i(TAG, "New interface: " + parameters);
    }
}
