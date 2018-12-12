package com.linghong.my.utils.uupt;

import java.util.*;

public class UUCommonFun {

	public static String CreateMd5Sign(Dictionary<String, String> Parameters, String AppKey) {

		Map<String, String> myParameters = SortMapByKey(Parameters);
		StringBuffer data = new StringBuffer();
		Iterator<String> iterator = myParameters.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = myParameters.get(key);
			if (key.toUpperCase() != "SIGN" && !TextUtils.isEmpty(value)) {
				data.append(String.format("%s=%s&", key, value));
			}
		}
		data.append("key=" + AppKey);

		String result = data.toString().toUpperCase();
		String sing = MD5Utils.string2MD5(result).toUpperCase();
		return sing;
	}

	private static Map<String, String> SortMapByKey(Dictionary<String, String> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			throw new IllegalStateException("����Ϊ��");
		}
		Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String key1, String key2) {
				return key1.compareTo(key2);
			}
		});
		sortedMap.putAll(oriMap.getHashMap());
		return sortedMap;
	}

	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis()/1000);
	}

	public static String NewGuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
