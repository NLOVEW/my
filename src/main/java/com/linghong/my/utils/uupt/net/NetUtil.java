package com.linghong.my.utils.uupt.net;


import com.linghong.my.utils.uupt.Log;
import com.linghong.my.utils.uupt.TextUtils;

import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

public class NetUtil {

	public static final String TAG = "Finals";

	public static final String HTTPS = "https";

	public static final String HTTP = "http";

	public static final String GET = "GET";

	public static final String POST = "POST";

	public static int READTIMEOUT = 30 * 1000;

	public static int CONNECTTIMEOUT = 30 * 1000;

	// 这些是协议体
	public static String end = "\r\n";

	public static String twoHyphens = "--";

	public static String boundary = "----WebKitFormBoundaryabu2ewelZdmyik5w";

	public static Map<String, String> headsMap;

	/**
	 * 得到URL连接
	 *
	 * @param url
	 * @param type
	 * @return
	 */
	public static HttpURLConnection GetHttpURLConnection(String url, String type) {
		if (TextUtils.isEmpty(url)) {
			Log.e("Finals", "URL 为空");
			return null;
		}
		// 打开连接
		URL mUrl = null;
		try {
			mUrl = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Finals", "打开网络失败");
		}

		if (mUrl == null) {
			Log.e("Finals", "URL 打开失败");
			return null;
		}

		// 初始化连接
		HttpURLConnection connection = null;
		// 得到对应的连接
		if (url.startsWith(HTTPS)) {
			connection = GetHttpsURLConnection(mUrl);
		} else if (url.startsWith(HTTP)) {
			connection = GetHttpURLConnection(mUrl);
		}
		if (connection == null) {
			Log.e("Finals", "connection 为空");
			return null;
		}

		InitUrlConnection(connection, type);
		return connection;
	}

	/**
	 * 得到HTTPS连接
	 *
	 * @param url
	 */
	private static HttpURLConnection GetHttpsURLConnection(URL url) {
		HttpsURLConnection sconn = null;
		try {
			sconn = (HttpsURLConnection) url.openConnection();
		} catch (Exception e) {
			Log.e("Finals", "openConnection 出现异常");
			return sconn;
		}

		// 去掉证书验证
		NoTrustManager manager = new NoTrustManager();
		TrustManager[] managers = { manager };
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Log.e(TAG, "没有TLS算法");
			return sconn;
		}
		try {
			ctx.init(null, managers, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			e.printStackTrace();
			Log.e(TAG, "秘钥管理出错");
			return sconn;
		}
		SSLSocketFactory factory = ctx.getSocketFactory();
		sconn.setHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		sconn.setSSLSocketFactory(factory);
		return sconn;
	}

	/**
	 * 得到HTTP连接
	 *
	 * @param url
	 * @return
	 */
	private static HttpURLConnection GetHttpURLConnection(URL url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "出现IO异常");
			return conn;
		}
		return conn;
	}

	/**
	 * 初始化网络连接
	 *
	 * @param connection
	 */
	private static void InitUrlConnection(HttpURLConnection connection, String type) {
		connection.setConnectTimeout(CONNECTTIMEOUT);
		connection.setReadTimeout(READTIMEOUT);
		try {
			connection.setRequestMethod(type);
		} catch (ProtocolException e) {
			e.printStackTrace();
			Log.e("Finals", "设置方法失效");
		}
		if (type.equals(POST)) {
			connection.setDoOutput(true);
		}
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");

		// Finals 2016-7-29 更新Head
		if (headsMap != null) {
			Iterator<String> mIterator = headsMap.keySet().iterator();
			while (mIterator.hasNext()) {
				String itemName = mIterator.next();
				String value = headsMap.get(itemName);
				connection.setRequestProperty(itemName, value);
			}
		}
	}

	public static void InitHead(Map<String, String> headsMap) {
		NetUtil.headsMap = headsMap;
	}
}
