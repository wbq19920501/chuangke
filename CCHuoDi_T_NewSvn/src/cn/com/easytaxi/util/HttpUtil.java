package cn.com.easytaxi.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * HTTP工具类
 * <ul>
 * 	<li>依赖的jar包有：httpcore-4.2.4.jar、httpclient-4.2.4.jar、httpmime-4.2.4.jar、commons-logging-1.1.2.jar</li>
 * </ul>
 */
public class HttpUtil {
	
	/** post无参数 */
	public static String post(String remoteUrl) {
		return post(remoteUrl, null);
	}
	
	/** post单参数，支持String值 */
	public static String post(String remoteUrl, String paramName, String paramValue) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(paramName, paramValue);
		return post(remoteUrl, paramMap);
	}
	
	/** post单参数，支持String[]值 */
	public static String post(String remoteUrl, String paramName, String... paramValue) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(paramName, paramValue);
		return post(remoteUrl, paramMap);
	}
	
	/** post多参数，支持String值、String[]值 */
	public static String post(String remoteUrl, Map<String, Object> paramMap) {
		try {
			//构建post
			HttpPost httpPost = new HttpPost(new URL(remoteUrl).toURI());
			if (paramMap != null && paramMap.size() > 0) {	//post参数
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (String key : paramMap.keySet()) {
					Object value = paramMap.get(key);
					if (value instanceof String) {			//单值
						String text = (String)value;
						params.add(new BasicNameValuePair(key, text));
					} else if (value instanceof String[]) {	//数组值
						String[] texts = (String[])value;
						for (String text : texts) {
							params.add(new BasicNameValuePair(key, text));
						}
					}
				}
				httpPost.setEntity(new UrlEncodedFormEntity(params, "utf"));
			}
			
			//执行post
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != 200) {
			}
			return EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return remoteUrl;
	}
	
	
	
//	String[] rstArr = "_input_charset=utf-8&body=充值&it_b_pay=30m&notify_url=http%3A%2F%2F121.42.180.59%2FYdRecharge%2Fnotify%2Falipay%2Frecharge%2F&out_trade_no=3515cc99132d43d6bad2851e281536c1&partner=2088221727003495&payment_type=1&seller_id=mdjcycp@163.com&service=mobile.securitypay.pay&subject=充值&total_fee=1&success=true&sign_type=RSA&sign=AVPYSVrtOkpXfr3VHMgBHd7N2JxKzlqy0Tk6Rbk9ltLl2PF8FH9qenAglrJgwq/NJ02R066aWyw6MQVLPlY7KdWsBVoD0sqnw4xUnVfQ4DgdybSwDdaGq/JyN41Pxw+dC7cvlJ/lHJs//XS+RlvBAPoLtQb1S1Mhrwz9G8SgM8M=".split("&");
//	
//	String rstStr = "";
//	
//	for(int i=0;i<rstArr.length;i++){
//		
//		if(i<rstArr.length-1){
//			rstStr += rstArr[i].split("=")[0]+"="+URLEncoder.encode(rstArr[i].split("=")[1],"utf-8")+"&" ;
//		}else{
//			rstStr +="sign="+URLEncoder.encode(rstArr[i].split("sign=")[1],"utf-8");
//		}
//	}

	
	
	 public static String get(String url) {  
	        String body = null;  
	        try {  
	            // Get请求  
	            HttpGet httpget = new HttpGet(url);  
	            httpget.setURI(new URI(httpget.getURI().toString()));  
	            // 发送请求  
	        	DefaultHttpClient httpclient = new DefaultHttpClient();
	            HttpResponse httpresponse = httpclient.execute(httpget);  
	            // 获取返回数据  
	            HttpEntity entity = httpresponse.getEntity();  
	            body = EntityUtils.toString(entity);  
	            if (entity != null) {  
	                entity.consumeContent();  
	            }  
	        } catch (ParseException e) {  
	            e.printStackTrace();  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } catch (URISyntaxException e) {  
	            e.printStackTrace();  
	        }  
	        return body;  
	    }  
	
	
	/** post多参数，支持File值 */
	public static String filePost(String remoteUrl, String paramName, File paramValue) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(paramName, paramValue);
		return filePost(remoteUrl, paramMap);
	}
	
	/** post多参数，支持File[]值 */
	public static String filePost(String remoteUrl, String paramName, File... paramValue) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(paramName, paramValue);
		return filePost(remoteUrl, paramMap);
	}
	
	/** post多参数，支持String值、String[]值、File值，File[]值 */
	public static String filePost(String remoteUrl, Map<String, Object> paramMap) {
		try {
			//构建post
			HttpPost httpPost = new HttpPost(new URL(remoteUrl).toURI());
			MultipartEntity entity = new MultipartEntity();
			for (String key : paramMap.keySet()) {
				Object value = paramMap.get(key);
				if (value instanceof File) {
					File file = (File)value;
					entity.addPart(key, new FileBody(file));
				} else if (value instanceof String) {
					String text = (String)value;
					entity.addPart(key, new StringBody(text));
				} else if (value instanceof File[]) {
					File[] files = (File[])value;
					for (File file : files) {
						entity.addPart(key, new FileBody(file));
					}
				} else if (value instanceof String[]) {
					String[] texts = (String[])value;
					for (String text : texts) {
						entity.addPart(key, new StringBody(text));
					}
				}
			}
			httpPost.setEntity(entity);
			
			//执行post
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() != 200) {
				
				
				
			}
			return EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return remoteUrl;
	}

}