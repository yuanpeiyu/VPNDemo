package com.example.yuanpeiyu.vpntest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sangfor.ssl.IVpnDelegate;
import com.sangfor.ssl.SFException;
import com.sangfor.ssl.SangforAuth;
import com.sangfor.ssl.common.VpnCommon;
import com.sangfor.ssl.service.setting.SystemConfiguration;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends Activity implements View.OnClickListener, IVpnDelegate {
	private static final String TAG = MainActivity.class.getSimpleName();
	private static TunObserver tunObserver = null;
	private static final String VPN_IP = "10.254.253.254";
	private static final int VPN_PORT = 443;
	private static final String USER = "pekall";
	private static final String PASSWD = "peka11vpn";
	private static final String HTTP_RES = "http://192.168.1.100";
	private static final String CERT_PATH = "/sdcard/hml_test.p12";
	private static final String CERT_PASS = "1";

	private InetAddress m_iAddr = null;

	// View
	private RememberEditText edt_ip = null;
	private RememberEditText edt_user = null;
	private RememberEditText edt_passwd = null;
	private RememberEditText edt_certName = null;
	private RememberEditText edt_certPasswd = null;
	private RememberEditText edt_sms = null;
	private RememberEditText edt_url = null;
	private EditText edt_rnd_code = null;
	private ImageButton imgbtn_rnd_code = null;
	private WebView webView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
		try {
			com.sangfor.ssl.service.utils.logger.Log.init(getApplicationContext());
			com.sangfor.ssl.service.utils.logger.Log.LEVEL = com.sangfor.ssl.service.utils.logger.Log.DEBUG;
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setContentView(R.layout.activity_main);
		initView();
		SangforAuth sfAuth = SangforAuth.getInstance();
		try {
//			 sfAuth.init(this, this, SangforAuth.AUTH_MODULE_EASYAPP);
			sfAuth.init(this, this, SangforAuth.AUTH_MODULE_L3VPN);
//			sfAuth.setLoginParam(AUTH_CONNECT_TIME_OUT, String.valueOf(30 * 1000));
		} catch (SFException e) {
			e.printStackTrace();
		}

		initAD();
	}

	@Override
	protected void onDestroy() {
		// SangforAuth.getInstance().vpnQuit();
		super.onDestroy();
	}

	private void initView() {
		edt_ip = RememberEditText.bind(this, R.id.edt_ip, "IP");
		edt_user = RememberEditText.bind(this, R.id.edt_user, "USER");
		edt_passwd = RememberEditText.bind(this, R.id.edt_passwd, "PASSWD");
		edt_certName = RememberEditText.bind(this, R.id.edt_certName, "CERTNAME");
		edt_certPasswd = RememberEditText.bind(this, R.id.edt_certPasswd, "CERTPASSWD");
		edt_sms = RememberEditText.bind(this, R.id.edt_sms, "SMS");
		edt_url = RememberEditText.bind(this, R.id.edt_url, "URL");
		edt_rnd_code = (EditText) findViewById(R.id.edt_rnd_code);
		imgbtn_rnd_code = (ImageButton) findViewById(R.id.imgbtn_rnd_code);
		webView = (WebView) findViewById(R.id.webView);
		edt_ip.setText(VPN_IP);
		edt_user.setText(USER);
		edt_passwd.setText(PASSWD);
		edt_url.setText(HTTP_RES);
		edt_certName.setText(CERT_PATH);
		edt_certPasswd.setText(CERT_PASS);
		webView.setWebViewClient(new WebViewClient() {
	        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
	}

	@Override
	public void onClick(View v) {
		String tempString = "no content";
		switch (v.getId()) {
			case R.id.btn_login:
				initSslVpn();
				break;
			case R.id.btn_logout:
				SangforAuth.getInstance().vpnLogout();
				break;
			case R.id.btn_cancel:
				SangforAuth.getInstance().vpnCancelLogin();
				break;
			case R.id.btn_sms:
				doVpnLogin(IVpnDelegate.AUTH_TYPE_SMS);
				break;
			case R.id.btn_reget_sms:
				doVpnLogin(IVpnDelegate.AUTH_TYPE_SMS1);
				break;
			case R.id.imgbtn_rnd_code:
//				SangforAuth.getInstance().vpnGetRndCode();
//				http_content.setText(tempString);
				break;

			case R.id.btn_test_http:
//				new TestThread().start();
//				 new TestHttpsThread().start();
				loadPage();
				break;
			default:
				Log.w(TAG, "onClick no process");
		}
	}



	private void loadPage() {
		if (webView == null) {
			return;
		}
		webView.loadUrl(edt_url.getText().toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 开始初始化VPN，该初始化为异步接口，后续动作通过回调函数通知结果
	 *
	 * @return 成功返回true，失败返回false，一般情况下返回true
	 */
	private boolean initSslVpn() {
		SangforAuth sfAuth = SangforAuth.getInstance();

		m_iAddr = null;
		final String ip = edt_ip.getText().toString();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					m_iAddr = InetAddress.getByName(ip);
					Log.i(TAG, "ip Addr is : " + m_iAddr.getHostAddress());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (m_iAddr == null || m_iAddr.getHostAddress() == null) {
			Log.d(TAG, "vpn host error");
			return false;
		}
		long host = VpnCommon.ipToLong(m_iAddr.getHostAddress());
		int port = VPN_PORT;

		if (sfAuth.vpnInit(host, port) == false) {
			Log.d(TAG, "vpn init fail, errno is " + sfAuth.vpnGeterr());
			return false;
		}

		return true;
	}

	/**
	 * 处理认证，通过传入认证类型（需要的话可以改变该接口传入一个hashmap的参数用户传入认证参数）.
	 * 也可以一次性把认证参数设入，这样就如果认证参数全满足的话就可以一次性认证通过，可见下面屏蔽代码
	 *
	 * @param authType
	 *            认证类型
	 * @throws SFException
	 */
	private void doVpnLogin(int authType) {
		Log.d(TAG, "doVpnLogin authType " + authType);

		boolean ret = false;
		SangforAuth sfAuth = SangforAuth.getInstance();

		switch (authType) {
			case IVpnDelegate.AUTH_TYPE_CERTIFICATE:
				String certPasswd = edt_certPasswd.getText().toString();
				String certName = edt_certName.getText().toString();
				sfAuth.setLoginParam(IVpnDelegate.CERT_PASSWORD, certPasswd);
				sfAuth.setLoginParam(IVpnDelegate.CERT_P12_FILE_NAME, certName);
				ret = sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_CERTIFICATE);
				break;
			case IVpnDelegate.AUTH_TYPE_PASSWORD:
				String user = edt_user.getText().toString();
				String passwd = edt_passwd.getText().toString();
				String rndcode = edt_rnd_code.getText().toString();
				sfAuth.setLoginParam(IVpnDelegate.PASSWORD_AUTH_USERNAME, user);
				sfAuth.setLoginParam(IVpnDelegate.PASSWORD_AUTH_PASSWORD, passwd);
//				sfAuth.setLoginParam(IVpnDelegate.SET_RND_CODE_STR, rndcode);
				ret = sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_PASSWORD);
				break;
			case IVpnDelegate.AUTH_TYPE_SMS:
				String smsCode = edt_sms.getText().toString();
				sfAuth.setLoginParam(IVpnDelegate.SMS_AUTH_CODE, smsCode);
				ret = sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_SMS);
				break;
			case IVpnDelegate.AUTH_TYPE_SMS1:
				ret = sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_SMS1);
				break;
			default:
				Log.w(TAG, "default authType " + authType);
				break;
		}

		if (ret == true) {
			Log.i(TAG, "success to call login method");
		} else {
			Log.i(TAG, "fail to call login method");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SangforAuth.getInstance().onActivityResult(requestCode, resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void displayToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	@Override
	public void vpnCallback(int vpnResult, int authType) {
		SangforAuth sfAuth = SangforAuth.getInstance();

		switch (vpnResult) {
			case IVpnDelegate.RESULT_VPN_INIT_FAIL:
				/**
				 * 初始化vpn失败
				 */
				Log.i(TAG, "RESULT_VPN_INIT_FAIL, error is " + sfAuth.vpnGeterr());
				displayToast("RESULT_VPN_INIT_FAIL, error is " + sfAuth.vpnGeterr());
				break;

			case IVpnDelegate.RESULT_VPN_INIT_SUCCESS:
				/**
				 * 初始化vpn成功，接下来就需要开始认证工作了
				 */
				Log.i(TAG,
						"RESULT_VPN_INIT_SUCCESS, current vpn status is " + sfAuth.vpnQueryStatus());
				displayToast("RESULT_VPN_INIT_SUCCESS, current vpn status is "
						+ sfAuth.vpnQueryStatus());
				Log.i(TAG, "vpnResult===================" + vpnResult  + "\nauthType ==================" + authType);
				// 初始化成功，进行认证操作
				doVpnLogin(IVpnDelegate.AUTH_TYPE_PASSWORD);
//				doVpnLogin(IVpnDelegate.AUTH_TYPE_CERTIFICATE)
//				doVpnLogin(authType);
//				String hardidString = sfAuth.vpnQueryHardID();
//				Log.w(TAG, "vpn hardid ============================ " + hardidString);
				break;

			case IVpnDelegate.RESULT_VPN_AUTH_FAIL:
				/**
				 * 认证失败，有可能是传入参数有误，具体信息可通过sfAuth.vpnGeterr()获取
				 */
				String errString = sfAuth.vpnGeterr();
				Log.i(TAG, "RESULT_VPN_AUTH_FAIL, error is " + sfAuth.vpnGeterr());
				displayToast("RESULT_VPN_AUTH_FAIL, error is " + sfAuth.vpnGeterr());
				break;

			case IVpnDelegate.RESULT_VPN_AUTH_SUCCESS:
				/**
				 * 认证成功，认证成功有两种情况，一种是认证通过，可以使用sslvpn功能了，另一种是前一个认证（如：用户名密码认证）通过，
				 * 但需要继续认证（如：需要继续证书认证）
				 */
				if (authType == IVpnDelegate.AUTH_TYPE_NONE) {
					Log.i(TAG, "welcom to sangfor sslvpn!");
					displayToast("welcom to sangfor sslvpn!");

                    // 若为L3vpn流程，认证成功后开启自动开启l3vpn服务
                    if (SangforAuth.getInstance().getModuleUsed() == SangforAuth.AUTH_MODULE_EASYAPP) {
                        // EasyApp流程，认证流程结束，可访问资源。
                        doResourceRequest();
                    }

				} else {
					Log.i(TAG, "auth success, and need next auth, next auth type is " + authType);
					displayToast("auth success, and need next auth, next auth type is " + authType);

					if (authType == IVpnDelegate.AUTH_TYPE_SMS) {
						// 输入短信验证码
						Toast.makeText(this, "you need send sms code.", Toast.LENGTH_LONG).show();
					} else {
						doVpnLogin(authType);
					}
				}
				break;
			case IVpnDelegate.RESULT_VPN_AUTH_CANCEL:
				Log.i(TAG, "RESULT_VPN_AUTH_CANCEL");
				displayToast("RESULT_VPN_AUTH_CANCEL");
				break;
			case IVpnDelegate.RESULT_VPN_AUTH_LOGOUT:
				/**
				 * 主动注销（自己主动调用logout接口）或者被动注销（通过控制台把用户踢掉）均会调用该接口
				 */
				tunObserver.stopWatching();
				Log.i(TAG, "RESULT_VPN_AUTH_LOGOUT");
				displayToast("RESULT_VPN_AUTH_LOGOUT");
				break;
			case IVpnDelegate.RESULT_VPN_L3VPN_FAIL:
				/**
				 * L3vpn启动失败，有可能是没有l3vpn资源，具体信息可通过sfAuth.vpnGeterr()获取
				 */
				Log.i(TAG, "RESULT_VPN_L3VPN_FAIL, error is " + sfAuth.vpnGeterr());
				displayToast("RESULT_VPN_L3VPN_FAIL, error is " + sfAuth.vpnGeterr());
				break;
			case IVpnDelegate.RESULT_VPN_L3VPN_SUCCESS:
				/**
				 * L3vpn启动成功
				 */
//				registerNetWorkBroadcasts(); //注册网络监听广播
				if (tunObserver == null) {
					tunObserver = new TunObserver();
				}
				Log.i(TAG, "start tun0 observe");
				tunObserver.startWatching();
				Log.i(TAG, "RESULT_VPN_L3VPN_SUCCESS ===== " + SystemConfiguration.getInstance().getSessionId() );
				displayToast("RESULT_VPN_L3VPN_SUCCESS");
                // L3vpn流程，认证流程结束，可访问资源。
                doResourceRequest();
				break;
			case IVpnDelegate.VPN_STATUS_ONLINE:
				/**
				 * 与设备连接建立
				 */
				Log.i(TAG, "online");
				displayToast("online");
				break;
			case IVpnDelegate.VPN_STATUS_OFFLINE:
				/**
				 * 与设备连接断开
				 */
				Log.i(TAG, "offline");
				displayToast("offline");
				break;
			default:
				/**
				 * 其它情况，不会发生，如果到该分支说明代码逻辑有误
				 */
				Log.i(TAG, "default result, vpn result is " + vpnResult);
				displayToast("default result, vpn result is " + vpnResult);
				break;
		}

	}

	/**我们EasyConnect有网络检测功能，当网络断开的时候直接会logout，然后网络连上的时候重新有login
	 * 但是使用sdk的应用不能保证这种机制，长时间断网的时候，如果服务端发生session切换的操作，会导致
	 * 客户端用了一个老的session，去和服务端握手，导致网络联通后仍然不能访问内网资源
	 *
	 * 在此函数里面，请检测下网络是否联通，如果联通请调用一下logout，然后再重新进行一次login
	 */
	private void processL3vpnFatalErr() {
	}

    private void doResourceRequest() {
        // TODO Auto-generated method stub
        // 认证结束，可访问资源。
    }

	/**
	 * 认证过程若需要图形校验码，则回调通告图形校验码位图，
	 *
	 * @param data
	 *            图形校验码位图
	 */
	@Override
	public void vpnRndCodeCallback(byte[] data) {
		Log.d(TAG, "vpnRndCodeCallback data: " + Boolean.toString(data==null));
		if (data != null) {
			Drawable drawable = Drawable.createFromStream(new ByteArrayInputStream(data),
					"rand_code");
			imgbtn_rnd_code.setBackgroundDrawable(drawable);
		}
	}

	@Override
	public void reloginCallback(int status, int result) {
		switch (status){

		case IVpnDelegate.VPN_START_RELOGIN:
			Log.e(TAG, "relogin callback start relogin start ...");
			break;
		case IVpnDelegate.VPN_END_RELOGIN:
			Log.e(TAG, "relogin callback end relogin ...");
			if (result == IVpnDelegate.VPN_RELOGIN_SUCCESS){
				Log.e(TAG, "relogin callback, relogin success!");
			} else {
				Log.e(TAG, "relogin callback, relogin failed");
			}
			break;
		}

	}

	/** 测试资源，打开浏览器 */
	private void luanchWebBrowser(String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);

			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			displayToast("Luanch Web Browser is error. " + url);
		}
	}

	/** 测试HTTP资源 */
	private class TestThread extends Thread {
		@Override
		public void run() {
//
			/*SangforAuth sfAuth = SangforAuth.getInstance();
			Log.d(TAG, "vpn status ===================== " + sfAuth.vpnQueryStatus());

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			Log.i(TAG, "url =======" + edt_url.getText().toString());
			HttpPost post = new HttpPost(edt_url.getText().toString());
			DefaultHttpClient client = new DefaultHttpClient(params);
			try {
				StringBuffer sb = new StringBuffer();
				HttpResponse response = client.execute(post);
				InputStream is = response.getEntity().getContent();
				int b = 0;
				byte[] buffer = new byte[4096];
				// 顺序读取文件text里的内容并赋值给整型变量b,直到文件结束为止。
				while ((b = is.read()) != -1) {
					sb.append((char) b);
				}
//				while (is.read(buffer) != -1) {
//					Log.w(TAG, "reading");
//				}
				String string = sb.toString();
				Log.w(TAG, string);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}

	/** 测试HTTPS资源 */
	private class TestHttpsThread extends Thread {
		@Override
		public void run() {
			String path = "/sdcard/tmp/Test.apk";
			Log.i("download", path);
//			String url = "https://" + "200.200.75.38" + "/com/" + "EasyConnectPhone.apk";
			String url = HTTP_RES;
			AsyncTask task = new DownloadApkTask(url, path);
//			task.execute((Void[]) null);
		}
	}
	public static class CrashHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			//打印未捕获的异常堆栈
			Log.d(TAG, "UnHandledException: ");
			ex.printStackTrace();
		}
	}

	private NetWorkBroadcastReceiver mNetWorkReceiver = null;
	/**
	 * 注册网络状态变化广播接收器
	 */
	private void registerNetWorkBroadcasts() {
		Log.d(TAG, "registerNetWorkBroadcasts.");

		// 注册网络广播接收器
		if (mNetWorkReceiver == null) {
			// 创建IntentFilter对象
			IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
			// 注册Broadcast Receiver
			mNetWorkReceiver = new NetWorkBroadcastReceiver();
			registerReceiver(mNetWorkReceiver, networkFilter);
		}
	}


	/**
	 * 取消注册网络状态变化广播接收器
	 */
	private void unRegisterNetWorkBroadcasts() {
		Log.d(TAG, "unRegisterBroadcasts.");

		// 取消注册Broadcast Receiver
		if (mNetWorkReceiver != null) {
			unregisterReceiver(mNetWorkReceiver);
			mNetWorkReceiver = null;
		}
	}


	/** 接收网络状态广播消息 **/
	private class NetWorkBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if ((mobNetInfo == null || !mobNetInfo.isConnected()) && (wifiInfo == null || !wifiInfo.isConnected())) {
				// 网络断开
				onEthStateChanged(false);   //再此函数里面做判断，如果网络断开做注销操作
				Log.d(TAG, "Network is disconnected.");
			} else if ((mobNetInfo != null && mobNetInfo.isConnected()) || (wifiInfo != null && wifiInfo.isConnected())) {
				// 网络恢复
				onEthStateChanged(true);  //判断正常的话，重新登陆
				Log.d(TAG, "Network is connected.");
			}
		}
	}


	/**
	 * 当网络发生变化时通告函数，这个地方无需处理离线情况，因为离线情况下不会注册监听网络的广播接收器
	 */
	private void onEthStateChanged(boolean connected) {
		if (connected) {
			initSslVpn();//登录
		} else {
			SangforAuth.getInstance().vpnLogout(); //注销
		}
	}

	private class TunObserver extends FileObserver {
		private static final String tunPath = "/sys/class/net/tun0";
		public TunObserver() {
			super(tunPath, FileObserver.DELETE | FileObserver.DELETE_SELF);
			Log.i(TAG, "observe tun0 ==========================");
		}

		@Override
		public void onEvent(int event, String path) {
			int eventListen = event & FileObserver.ALL_EVENTS;
			switch (eventListen) {
			case FileObserver.DELETE:

			case FileObserver.DELETE_SELF:
				Log.i(TAG, "tun device removed ------------------------------------");
				SangforAuth.getInstance().vpnLogout();
				break;
			default:
				break;
			}

		}

	}

	private void initAD() {
		AdManager.getInstance(this).init("201d1458ba69c494", "7e1115f62d148839", false);
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
		layout.addView(adView);
	}

}
