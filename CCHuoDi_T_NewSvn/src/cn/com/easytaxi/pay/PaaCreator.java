package cn.com.easytaxi.pay;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PaaCreator {
	public static JSONObject randomPaa() {
		int amount = 13900;
	    
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String timeStr = dateFormat.format(new Date());
//        timeStr = "20140318141315";
	    String orderStr = timeStr + "0000";
	    
	    
	    JSONObject paaParams = new JSONObject();
	    try {
			paaParams.put("inputCharset", "1");
			paaParams.put("receiveUrl", "http://www");
			paaParams.put("version", "v1.0");
			paaParams.put("signType", "1");
			paaParams.put("merchantId", "100020130912001"); 
			paaParams.put("orderNo", orderStr);
			paaParams.put("orderAmount", amount);
			paaParams.put("orderCurrency", "0");
			paaParams.put("orderDatetime", timeStr);
			paaParams.put("productName", "二维码");
			paaParams.put("ext1", "<USER>201406231006545</USER>");
			paaParams.put("payType", "0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
	    String[] paaParamsArray = {
			"1","inputCharset",
			"http://www","receiveUrl",
			"v1.0","version",
			"1","signType",
			"100020130912001","merchantId",
			orderStr,"orderNo",
			"" + amount,"orderAmount",
			"0","orderCurrency",
			timeStr,"orderDatetime",
			"二维码", "productName",
			"<USER>201406231006545</USER>","ext1",
			"0","payType",
			"1234567890","key",
	    };
	    
	    String paaStr = "";
	    for (int i = 0; i < paaParamsArray.length; i++) {
	    	paaStr += paaParamsArray[i+1] + "=" + paaParamsArray[i] + "&";
	        i++;
	    }
	    Log.d("PaaCreator", "PaaCreator " + paaStr.substring(0, paaStr.length() -1));
	    String md5Str = md5(paaStr.substring(0, paaStr.length() -1));
	    Log.d("PaaCreator", "PaaCreator md5Str " + md5Str);
	    try {
			paaParams.put("signMsg", md5Str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    return paaParams;
	}
	
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		return hexString(hash);
	}
	
	public static final String hexString(byte[] bytes)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++)
		{
			buffer.append(hexString(bytes[i]));
		}
		return buffer.toString();
	}
	
	public static final String hexString(byte byte0)
	{
		// 字节到十六进制的ASCII码转换
		char ac[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char ac1[] = new char[2];
		ac1[0] = ac[byte0 >>> 4 & 0xf];
		ac1[1] = ac[byte0 & 0xf];
		String s = new String(ac1);
		return s;
	}
	
}
