package com.example.yuanpeiyu.vpntest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;


import android.os.AsyncTask;
import android.util.Log;

import com.sangfor.ssl.service.utils.network.HttpConnect;

public class DownloadApkTask extends AsyncTask<Void, Integer, Integer> {
	private static final String TAG = DownloadApkTask.class.getSimpleName();
	private static final int RESULT_OK = 0;
	private static final int RESULT_CANCELED = 1;
	private static final int RESULT_ERROR = -1;

	private String mUrl = null;
	private String mLocalPath = null;
	private volatile boolean mCancelled = false;

	public DownloadApkTask(String url, String localPath) {
		mUrl = url;
		mLocalPath = localPath;
	}

	/**
	 * 初始化下载APK的https连接
	 * 
	 * @param url
	 *            下载APK地址
	 * @return 成功初始化返回https连接，失败返回null
	 */
	private HttpsURLConnection initHttpsURLConnection(String url) {
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			TrustManager tm = new HttpConnect.TrustAnyTrustManager();
			context.init(null, new TrustManager[] { tm }, new SecureRandom());

			HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
			conn.setSSLSocketFactory(context.getSocketFactory());
			conn.setHostnameVerifier(new HttpConnect.TrustAnyHostnameVerifier());
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(60000);
			conn.setInstanceFollowRedirects(true);

			return conn;
		} catch (IOException e) {
			Log.e(TAG, "1Failed init https connection: " + e.getMessage());
			e.printStackTrace();
		} catch (KeyManagementException e) {
			Log.e(TAG, "2Failed init https connection: " + e.getMessage());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "3Failed init https connection: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "4Failed init https connection: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * HTTPS连接请求，有3次请求机会
	 * 
	 * @param conn
	 *            HTTPS连接
	 * @return 成功返回true，失败返回false
	 */
	private boolean httpsConnect(HttpsURLConnection conn) {
		boolean result = false;

		for (int i = 0; i < 3; i++) {
			try {
				if (mCancelled) {
					break;
				}

				conn.connect();
				int code = conn.getResponseCode();
				if (code == HttpsURLConnection.HTTP_OK) {
					result = true;
					break;
				}

				conn.disconnect();
				Log.d(TAG, "HTTP response code: " + code);
			} catch (IOException e) {
				Log.e(TAG, "Failed request apk file: " + e.getMessage());
			}
		}

		return result;
	}

	/**
	 * 下载APK安装包的本地
	 * 
	 * @param conn
	 *            HTTPS连接
	 * @return 成功返回RESULT_OK，失败返回RESULT_ERROR，取消返回RESULT_CANCELED
	 */
	private Integer downloadAPK(HttpsURLConnection conn) {
		//FileOutputStream fos = null;
		InputStream is = null;

		int length = conn.getContentLength();
		if (length <= 0) {
			// 取不到长度，直接按1.5MB算，此举会让进度条不准确，但没有太大影响。
			length = 1500 * 1024 * 1024;
		}
		publishProgress(0, length);

		try {
//			fos = new FileOutputStream(mLocalPath);
			is = conn.getInputStream();

			int total = 0;
			int read = 0;
			int i = 0;
			byte[] buffer = new byte[2048];

			while ((read = is.read(buffer)) != -1) {
				Log.w(TAG, "=========================" + new String(buffer));
				//fos.write(buffer, 0, read);
				total += read;
				if (++i == 10) {
					publishProgress(total, length);
					i = 0;
				}

				if (mCancelled) {
					return RESULT_CANCELED;
				}
			}
			publishProgress(total, length);
		} catch (IOException e) {
			Log.e(TAG, "Cannot download apk file: " + e.getMessage());
			return RESULT_ERROR;
		} finally {
//			if (fos != null) {
//				try {
//					fos.close();
//					fos = null;
//				} catch (Exception e) {
//				}
//			}
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
				}
			}
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}

		return RESULT_OK;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Integer doInBackground(Void... params) {
		HttpsURLConnection conn = initHttpsURLConnection(mUrl);
		if (conn == null) {
			Log.e(TAG, "init https url connection fail.");
			return RESULT_ERROR;
		}

		if (!httpsConnect(conn)) {
			Log.e(TAG, "https connection fail.");
			return mCancelled ? RESULT_CANCELED : RESULT_ERROR;
		}

		return downloadAPK(conn);
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		int prog = progress[0] * 100 / progress[1];
		prog = (prog <= 100) ? prog : 100;
		Log.d(TAG, "onProgressUpdate:" + prog);
	}

	@Override
	protected void onPostExecute(Integer result) {
		Log.d(TAG, "onPostExecute: " + result);
		if (result == RESULT_ERROR) {
		} else if (result == RESULT_OK) {
		}
	}

	@Override
	protected void onCancelled() {
		mCancelled = true;
	}
}
