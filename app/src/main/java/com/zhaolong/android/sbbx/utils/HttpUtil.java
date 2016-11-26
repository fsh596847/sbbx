package com.zhaolong.android.sbbx.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zhaolong.android.sbbx.ConfigSbbx;

public class HttpUtil {
	
	/**
     * 检测网络是否可用
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        boolean isNetworkConnected = true;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			isNetworkConnected = ni != null && ni.isConnectedOrConnecting();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return isNetworkConnected;
    }

	/**
	 * http访问数据，默认返回的数据为UTF-8编码
	 * 
	 * @param url
	 *            url地址
	 * @param method
	 *            提交方式
	 * @return
	 * @throws IOException
	 */

	public static String getUrl(Context context,HttpClient httpClient, String url)
			throws IOException {
		return getUrl(context,httpClient, url, "UTF-8");
	}

	public static String getUrl(Context context,HttpClient httpClient, String url,
			String encoding) throws IOException {
		url = addDeviceId(url,context);
		HttpGet request = new HttpGet(url);
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return EntityUtils.toString(response.getEntity(), encoding);
		} else {
			return "";
		}
	}

	/**
	 * http访问数据，默认返回的数据为UTF-8编码
	 * 
	 * @param url
	 *            url地址
	 * @param method
	 *            提交方式
	 * @return
	 * @throws IOException
	 */
	public static String getUrl(Context context,String url) throws IOException {
		url = addDeviceId(url,context);
		HttpGet request = new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			return "";
		}
	}

	/**
	 * post提交数据
	 * 
	 * @param url
	 *            提交地址
	 * @param params
	 *            参数
	 * @return
	 * @throws Exception
	 */
	public static String postUrl(Context context,String url, Map<String, String> params)
			throws Exception {
		if(!isNetworkConnected(context)){
			throw new Exception("请检查网络是否正常！");
		}
		return postUrl(context,url, params, "UTF-8",0);
	}

	/**
	 * post提交数据
	 * 
	 * @param url
	 *            提交地址
	 * @param params
	 *            参数
	 * @param encoding
	 *            参数编码
	 * @return
	 * @throws Exception
	 */
	public static String postUrl(Context context,String url, Map<String, String> params,
			String encoding,int times) throws Exception {
		List<NameValuePair> param = new ArrayList<NameValuePair>(); // 参数
		// param.add(new BasicNameValuePair("par", "request-post"));
		// //这个参数我不知道是不是非要加这个
		if (params != null) {
			// 添加参数
			Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();

				param.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
//		url = addDeviceId(url,context);
		//System.out.println("url:" + url);
		HttpPost request = new HttpPost(url);

		HttpEntity entity = new UrlEncodedFormEntity(param, encoding);
		request.setEntity(entity);

		DefaultHttpClient client = null;// ApplicationTool.getInstance().httpClient ;//new DefaultHttpClient();
		if (client==null){
			HttpParams httpParams = new BasicHttpParams(); 
			httpParams.setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpParams.setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000);
			 SchemeRegistry schemeRegistry = new SchemeRegistry();  
		        schemeRegistry.register(  
		                 new Scheme("http", PlainSocketFactory.getSocketFactory(),ConfigSbbx.serverPort)); 
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
//			ApplicationTool.getInstance().httpClient = new DefaultHttpClient(ccm,httpParams);
			client = new DefaultHttpClient(ccm,httpParams);
		}


		HttpResponse response = client.execute(request);
		int status = response.getStatusLine().getStatusCode();
		if (status == HttpStatus.SC_OK) {
			String s = EntityUtils.toString(response.getEntity(),"UTF-8");
//			if (s.contains("\"ret\":0") && times == 0){
//				//表示需要重新登录
//				String loginUrl =  Config.server + "?method="
//						+ Parameter.PM_Value_LoginWebUser;
//				if (!url.startsWith(loginUrl)){
//					HttpResult hr = ApplicationTool.getInstance().autoLoginCzbbb();
//					if (hr != null && hr.getRet().intValue() == HttpResult.SUCCESS){
//						times++;
//						s = postUrl(context,url, params,encoding,times);
//					}
//				}
//			}
//			if (s.contains("\"ret\":-3")){
//				//表示被踢出
//				ApplicationTool.getInstance().showKickedOutDialog();
//			}
			return s;
		} else {
			throw new Exception("无法获取数据，请检查网络是否正常！");
		}
	}

 
	
	/**
	 * post提交文件及文本信息
	 * 
	 * @param url
	 * @param params
	 * @param filePaths
	 * @param encoding
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String postMultipartUrl(String url,
			Map<String, String> params, Map<String, String> filePaths,
			String encoding) throws ClientProtocolException, IOException {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		MultipartEntity mulentity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		if (params != null) {
			// 添加参数
			Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				mulentity.addPart(entry.getKey(), new StringBody((String) entry
						.getValue()));
			}
		}

		// 添加图片表单数据
		if (filePaths != null) {
			// 添加参数
			Iterator<Entry<String, String>> iterator = filePaths.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				String key = entry.getKey();
				String fileName = entry.getValue();
				if (!HlpUtils.fileExists(fileName)) {
					continue;
				}
				FileBody filebody = new FileBody(new File(fileName));
				mulentity.addPart(key, filebody);
			}
		}
		httpPost.setEntity(mulentity);
		HttpResponse response = httpclient.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return EntityUtils.toString(response.getEntity(),"UTF-8");
		} else {
			return null;
		}
	}

	public static String postData(String urlString, String data) {
		OutputStream outStream = null;
		InputStream in = null;
		StringBuffer buf = new StringBuffer();
		try {
			// 创建一个 URL 对象
			URL url = new URL(urlString);

			// 创建一个 URL 连接，如果有代理的话可以指定一个代理。
			// URLConnection connection = url.openConnection(Proxy_yours);
			// 对于 HTTP 连接可以直接转换成 HttpURLConnection，这样就可以使用一些 HTTP 连接特定的方法，如
			// setRequestMethod() 等:
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("POST");
			// 在开始和服务器连接之前，可能需要设置一些网络参数
			conn.setConnectTimeout(10000);

			// conn.addRequestProperty("User-Agent","J2me/MIDP2.0");

			// 连接到服务器
			conn.connect();

			// 与服务器交互:
			outStream = conn.getOutputStream();
			outStream.write(data.getBytes());
			/*
			 * ObjectOutputStream objOutput = new ObjectOutputStream(outStream);
			 * objOutput.writeObject(data);
			 */
			outStream.flush();
			outStream.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				buf.append(line);
			}
			reader.close();
			conn.disconnect();
		} catch (Exception e) {
			// 网络读写操作往往会产生一些异常，所以在具体编写网络应用时
			// 最好捕捉每一个具体以采取相应措施
		} finally {
		}
		return buf.toString();
	}
	
	/**
	 * 在url上添加deviceId参数
	 * @param url
	 * @param c
	 * @return
	 */
	public static String addDeviceId(String url,Context c) {
		if (url.contains("&deviceId=")) {//已经加deviceId,直接返回
			return url;
		}else {//未加入deviceId
			if (url.contains("?method=")) {
				return url = url.concat("&deviceId=").concat(HlpUtils.getSerialNo(c));
			}else {
				return url;
			}	
		}
	}
	
	/**
	 * 发送应用注销消息
	 */
	public static void broadcastKickOutMessage(Context context) {
//		Intent i = new Intent(MainActivity.ACTION_EXIT);
//		i.putExtra("KickOut", true);
//		i.putExtra("CMD", "CMD_STOP_SERVICE");
//		context.sendBroadcast(i, MainActivity.PERM_PRIVATE);		
	}
	

//	public static String httpsGet(String url){
//		HttpClient client = new DefaultHttpClient();
//		Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
//		Protocol.registerProtocol("https", myhttps);
//		GetMethod getMethod = new GetMethod(url);
//		getMethod.setFollowRedirects(true);
//		getMethod.addRequestHeader("Content-Type","text/html;charset=UTF-8"); 
//		HttpMethodParams params = new HttpMethodParams();
//		params.setContentCharset("UTF-8");
//		getMethod.setParams(params);
//		try {
//			client.executeMethod(getMethod);
//			return getMethod.getResponseBodyAsString();
//		} catch (HttpException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
}