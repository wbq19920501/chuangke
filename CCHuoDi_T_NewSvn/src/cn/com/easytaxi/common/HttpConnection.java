package cn.com.easytaxi.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;  
import java.util.List;  
  
import org.apache.http.Header;
import org.apache.http.HttpResponse;  
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;  
import org.apache.http.client.HttpClient;  
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.params.BasicHttpParams;  
import org.apache.http.params.HttpConnectionParams;  
import org.apache.http.params.HttpParams;  
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;  

import android.os.Bundle;  
import android.os.Handler;  
import android.os.Message;  
import android.util.Log;
  
/** 
 * Asynchronous HTTP connections 
 *  
 * @author Greg Zavitz & Joseph Roth 
 */  
public class HttpConnection implements Runnable {  
  
    public static final int DID_START = 0;  
    public static final int DID_ERROR = 1;  
    public static final int DID_SUCCEED = 2;  
  
    private static final int GET = 0;  
    private static final int POST = 1;  
    private static final int PUT = 2;  
    private static final int DELETE = 3;  
    private static final int BITMAP = 4;  
    private static final int IPOST = 5 ;
   
    private String url;  
    private int method;  
    private String data;  
    public static Callback<String> listener;    
  
    private HttpClient httpClient;  
  
    // public HttpConnection() {  
    // this(new Handler());  
    // }  
  
    public void create(int method, String url, String data, Callback<String> listener) {  
        this.method = method;  
        this.url = url;  
        this.data = data;  
        this.listener = listener;  
        ConnectionManager.getInstance().push(this);  
    }  
  
    public void get(String url, Callback<String> listener) {  
        create(GET, url, null, listener);  
    }  
  
    public void post(String url, String data, Callback<String> listener) {  
        create(POST, url, data, listener);  
    }  
    
    public void post(String url, String data, Callback<String> listener , int i) {  
        create(IPOST, url, data, listener);  
    }  
  
    public void put(String url, String data) {  
        create(PUT, url, data, listener);  
    }  
  
    public void delete(String url) {  
        create(DELETE, url, null, listener);  
    }  
  
    public void bitmap(String url) {  
        create(BITMAP, url, null, listener);  
    }  
  
  
    private static final Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message message) {  
            switch (message.what) {  
                case HttpConnection.DID_START: {  
                    break;  
                }  
                case HttpConnection.DID_SUCCEED: {  
                    Object data = message.getData();  
                    if (listener != null) {  
                        if(data != null) {  
                            Bundle bundle = (Bundle)data;  
                            String result = bundle.getString("callbackkey");  
                            listener.handle(result);  
                        }  
                    } 
                    break;  
                }  
                case HttpConnection.DID_ERROR: {  
                    break;  
                }  
            }  
        }  
    };  
  
	public void run() {  
//      handler.sendMessage(Message.obtain(handler, HttpConnection.DID_START));  
        httpClient = getHttpClient();  
        try {  
            HttpResponse httpResponse = null;  
            switch (method) {  
            case GET:  
//            	Log.e("请求信息", "准备GET请求URL:：" + url );
            	
            	HttpGet request = new HttpGet( url );
//                Log.e("请求信息", "正在GET请求URL:：" + url );
                
                request.addHeader("Content-Type" , "application/json" );
    	        request.addHeader("Accept" , "application/json" );
    	        
    	        httpResponse = httpClient.execute( request );  
    	        
    	        Header header = httpResponse.getFirstHeader("APIKey");
    	        
    	        if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){  
                	String result = EntityUtils.toString(httpResponse.getEntity());  
//                	Log.e( "请求信息", "请求返回的数据结果为："+result);
                	this.sendMessage( result );  
                }else{
                	this.sendMessage("fail");  
//                    Log.e("请求信息", "请求发生错误");
                }
    	        
                break;  
            case POST:  
//            	Log.e("请求信息", "准备POST请求URL:：" + url );
                HttpPost httpPost = new HttpPost( url );  
                List<NameValuePair> params = new ArrayList<NameValuePair>();  
                BasicNameValuePair valuesPair = new BasicNameValuePair( "args", data );  
                params.add(valuesPair);  
                
                httpPost.setEntity(new UrlEncodedFormEntity( params, "UTF-8" ) );  
                
                httpPost.addHeader("Content-Type" , "application/json" );
                httpPost.addHeader("Accept" , "application/json" );
   			 
                httpResponse = httpClient.execute( httpPost );  
                
//                Log.e("请求信息", "正在POST请求URL:：" + url );
                if (isHttpSuccessExecuted(httpResponse)) {  
                    String result = EntityUtils.toString( httpResponse.getEntity() );  
                    this.sendMessage( result );  
//                    Log.e("请求信息", "得到数据:：" + result );
                } else {  
                    this.sendMessage("fail");  
//                    Log.e("请求信息", "请求发生错误");
                }  
                break;
                
            case IPOST:  
//            	Log.e("请求信息", "准备POST请求URL:：" + url );
            	
            	 URL mUrl=new URL( url );  
    		     HttpURLConnection httpConn = (HttpURLConnection)mUrl.openConnection();  
    		     
    		     httpConn.setConnectTimeout(3000);
    		     httpConn.setReadTimeout(300000);
    		     httpConn.setDoOutput(true);
    		     httpConn.setDoInput(true);  
    		     httpConn.setUseCaches(false);
    		     httpConn.setRequestMethod("POST");
    		     
    	         byte[] requestStringBytes = data.getBytes( HTTP.UTF_8 );  
    	         httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);  
    	         httpConn.setRequestProperty("Content-Type", "application/json"); 
    	         httpConn.setRequestProperty("Accept" , "application/json"); 
    	         httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接  
    	         httpConn.setRequestProperty("Charset", "UTF-8");  
    	         
    	         OutputStream outputStream = httpConn.getOutputStream();  
	   	         outputStream.write(requestStringBytes);  
	   	         outputStream.close();  
	   	         
//	   	         Log.e("请求信息", "正在POST请求URL:：" + url );
	   	         
	   	         int responseCode = httpConn.getResponseCode(); 
	   	         
	   	         if(HttpURLConnection.HTTP_OK == responseCode){//连接成功  
		             
		           StringBuffer sb = new StringBuffer();  
		              String readLine;  
		              BufferedReader responseReader;  
		       
		              responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), HTTP.UTF_8 ));  
		              while ((readLine = responseReader.readLine()) != null) {  
		               sb.append(readLine).append("\n");  
		              }  
		              responseReader.close();  

		              this.sendMessage( sb.toString() );
		              
		              Log.e("请求信息", "得到数据:：" + sb.toString()  );
		              
		          }  
            	
            	break;
            }  
        } catch (Exception e) {  
            this.sendMessage("fail"); 
//            Log.e("请求信息", "请求发生错误");
        }  
        ConnectionManager.getInstance().didComplete(this);  
    }  
  
    // private void processBitmapEntity(HttpEntity entity) throws IOException {  
    // BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);  
    // Bitmap bm = BitmapFactory.decodeStream(bufHttpEntity.getContent());  
    // handler.sendMessage(Message.obtain(handler, DID_SUCCEED, bm));  
    // }  
  
    private void sendMessage(String result) {  
        Message message = Message.obtain(handler, DID_SUCCEED,  
                listener);  
        Bundle data = new Bundle();  
        data.putString("callbackkey", result);  
        message.setData(data);  
        handler.sendMessage(message);  
          
    }  
  
    public static DefaultHttpClient getHttpClient() {  
        HttpParams httpParams = new BasicHttpParams();  
        HttpConnectionParams.setConnectionTimeout(httpParams, 20000);  
        HttpConnectionParams.setSoTimeout(httpParams, 20000);  
        // HttpConnectionParams.setSocketBufferSize(httpParams, 8192);  
  
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);  
        return httpClient;  
    }  
  
    public static boolean isHttpSuccessExecuted(HttpResponse response) {  
        int statusCode = response.getStatusLine().getStatusCode();  
        return (statusCode > 199) && (statusCode < 400);  
    }  
  
}  