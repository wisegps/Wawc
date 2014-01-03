package com.wise.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetThread {
	static String TAG = "NetThread";
	public static class GetDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		public GetDataThread(Handler handler,String url,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
		}
		@Override
		public void run() {
			super.run();
			try {
				URL myURL = new URL(url);
				URLConnection httpsConn = (URLConnection) myURL.openConnection();
				if (httpsConn != null) {
					httpsConn.setConnectTimeout(20*1000);
					httpsConn.setReadTimeout(20*1000);
					InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
					BufferedReader br = new BufferedReader(insr, 1024);
					String data = "";
					String line = "";
					while ((line = br.readLine()) != null) {
						data += line;
					}
					insr.close();
					Message message = new Message();
					message.what = what;
					message.obj = data;
					handler.sendMessage(message);
				}else{
					Log.d(TAG, "URLConnection");
					Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
				}
			}catch (SocketTimeoutException e) {
				e.printStackTrace();
				Message message = new Message();
				message.what = what;
				message.obj = "SocketTimeoutException";
				handler.sendMessage(message);
			}catch (SocketException e) {
				e.printStackTrace();
				Message message = new Message();
				message.what = what;
				message.obj = "SocketTimeoutException";
				handler.sendMessage(message);
			}
			catch (Exception e) {
				e.printStackTrace();
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
			}
		}
	}
	
	public static class putDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		List<NameValuePair> parms;
		public putDataThread(Handler handler,String url,List<NameValuePair> parms,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
			this.parms = parms;
		}
		@Override
		public void run() {
			super.run();
			try {
				HttpClient client = new DefaultHttpClient();
		        HttpPut httpPut = new HttpPut(url);	
		        if(parms != null){
		        	httpPut.setEntity(new UrlEncodedFormEntity(parms,HTTP.UTF_8));
		        }
		        HttpResponse response = client.execute(httpPut); 
		        if (response.getStatusLine().getStatusCode()  == 200){
		        	HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					Message message = new Message();
					message.what = what;
					message.obj = sb.toString();
					handler.sendMessage(message);
		        }else{
		        	Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
		        }
			} catch (Exception e) {
				e.printStackTrace();	
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
			}
		}
	}
	
	public static class DeleteThread extends Thread{
		Handler handler;
		String url;
		int what;		
		public DeleteThread(Handler handler,String url,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
		}
		@Override
		public void run() {
			super.run();
			try {
				HttpClient client = new DefaultHttpClient();
				HttpDelete httpDelete = new HttpDelete(url);
		        HttpResponse response = client.execute(httpDelete); 
		        if (response.getStatusLine().getStatusCode()  == 200){
		        	HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					Message message = new Message();
					message.what = what;
					message.obj = sb.toString();
					handler.sendMessage(message);
		        }else{
		        	Message message = new Message();
					message.what = what;
					message.obj = "";
					handler.sendMessage(message);
		        }
			} catch (Exception e) {
				e.printStackTrace();	
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
			}
		}
	}
	
	public static class postDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		List<NameValuePair> params;
		public postDataThread(Handler handler,String url,List<NameValuePair> params,int what){
			this.handler = handler;
			this.url = url;
			this.what = what;
			this.params = params;
		}
		@Override
		public void run() {
			super.run();
			Log.d(TAG, url);
			HttpPost httpPost = new HttpPost(url);
			try {
				 httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				 HttpClient client = new DefaultHttpClient();
				 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
				 HttpResponse httpResponse = client.execute(httpPost);
				 if(httpResponse.getStatusLine().getStatusCode() == 200){
					 String strResult = EntityUtils.toString(httpResponse.getEntity());
					 Log.d(TAG, strResult);
					 Message message = new Message();
					 message.what = what;
					 message.obj = strResult;
					 handler.sendMessage(message);
				 }else{
					 Log.d(TAG, "״̬" +httpResponse.getStatusLine().getStatusCode());
				 }
			} catch (Exception e) {
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
				e.printStackTrace();
			}
		}
	}
}