package com.linghong.my.utils;

import org.apache.http.util.TextUtils;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Auther: luck_nhb
 * @Date: 2018/12/8 09:12
 * @Version 1.0
 * @Description: uu跑腿参数编码
 */
public class UUPTSignUtil {

    public static String CreateMd5Sign(Map<String, String> parameters, String AppKey) {

        Map<String, String> myParameters = SortMapByKey(parameters);
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
        String sing = string2MD5(result).toUpperCase();
        return sing;
    }

    private static Map<String, String> SortMapByKey(Map<String, String> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            throw new IllegalStateException("参数错误");
        }
        Map<String, String> sortedMap = new TreeMap<>((key1, key2) -> key1.compareTo(key2));
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

    private static String string2MD5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
