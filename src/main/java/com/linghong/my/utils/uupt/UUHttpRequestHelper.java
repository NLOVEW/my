package com.linghong.my.utils.uupt;


import com.linghong.my.utils.uupt.net.NetUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;


public class UUHttpRequestHelper {

	private static final String ENCODE = "UTF-8";

	public static String HttpPost(String Url, String postDataStr) {
		return PostData(Url, postDataStr, null);
	}

	private static String PostData(String Url, String postDataStr, Dictionary<String, String> parameters) {
		String result = "";

		HttpURLConnection conn = NetUtil.GetHttpURLConnection(Url, NetUtil.POST);
		if (conn != null) {
			try {
				conn.setRequestProperty("Charset", ENCODE);
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				conn.connect();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (conn != null) {
			OutputStream netOutputStream = null;
			DataOutputStream dataOutputStream = null;
			try {
				netOutputStream = conn.getOutputStream();
				dataOutputStream = new DataOutputStream(netOutputStream);
				// 文件上传协议体开头
				UploadData(netOutputStream, postDataStr, parameters);

				dataOutputStream.flush();
				netOutputStream.flush();
				dataOutputStream.close();
				netOutputStream.close();

				dataOutputStream = null;
				netOutputStream = null;

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dataOutputStream != null) {
					try {
						dataOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					dataOutputStream = null;
				}
				if (netOutputStream != null) {
					try {
						netOutputStream.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					netOutputStream = null;
				}
			}
		}

		if (conn != null) {
			InputStream netInputStream = null;
			try {
				netInputStream = conn.getInputStream();
				result = DownloadData(netInputStream);
				netInputStream.close();
				netInputStream = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (netInputStream != null) {
					try {
						netInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					netInputStream = null;
				}
			}
		}
		return result;
	}

	public static String HttpPost(String Url, Dictionary<String, String> parameters) {
		return PostData(Url, null, parameters);
	}

	private static void UploadData(OutputStream netOutputStream, String postDataStr, Dictionary<String, String> parameters) throws IOException {
		StringBuilder sbBuilder = new StringBuilder();
		if (parameters != null) {
			Iterator<String> iterator = parameters.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = parameters.get(key);
				sbBuilder.append(String.format("%s=%s", key, URLEncoder.encode(value, ENCODE)));
				if (iterator.hasNext()) {
					sbBuilder.append("&");
				}
			}
		} else {
			sbBuilder.append(postDataStr);
		}

		InputStream valuesInputStream = new ByteArrayInputStream(sbBuilder.toString().getBytes(ENCODE));
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = valuesInputStream.read(buffer)) != -1) {
			netOutputStream.write(buffer, 0, len);
		}
	}

	private static String DownloadData(InputStream netInputStream) throws IOException {
		String result = null;
		ByteArrayOutputStream bos = null;
		bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[40960];
		int len = 0;

		boolean isDownload = false;
		try {
			while ((len = netInputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.close();
			result = bos.toString(ENCODE);
			bos = null;
			isDownload = true;
		} catch (Exception e) {
			isDownload = false;
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				bos = null;
			}
		}
		if (!isDownload) {
			throw new IOException("下载数据出错了");
		}
		return result;
	}
}
