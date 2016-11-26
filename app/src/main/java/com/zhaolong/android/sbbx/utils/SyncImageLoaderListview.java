package com.zhaolong.android.sbbx.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import com.zhaolong.android.sbbx.services.DataService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;

public class SyncImageLoaderListview {

	private Object lock = new Object();

	private boolean mAllowLoad = true;

	private boolean firstLoad = true;

	private int mStartLoadLimit = 0;

	private int mStopLoadLimit = 0;

	final Handler handler = new Handler();

	private HashMap<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();   

	public interface OnImageLoadListener {
		public void onImageLoad(Integer t, Drawable drawable,Integer index);
		public void onError(Integer t);
	}

	public void setLoadLimit(int startLoadLimit,int stopLoadLimit){
		if(startLoadLimit > stopLoadLimit){
			return;
		}
		mStartLoadLimit = startLoadLimit;
		mStopLoadLimit = stopLoadLimit;
	}

	public void restore(){
		mAllowLoad = true;
		firstLoad = true;
	}

	public void lock(){
		mAllowLoad = false;
		firstLoad = false;
	}

	public void unlock(){
		mAllowLoad = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public void loadImage(final Context context,Integer t, String imageUrl,
			OnImageLoadListener listener,final Integer index) {
		final OnImageLoadListener mListener = listener;
		final String mImageUrl = imageUrl;
		final Integer mt = t;

		new Thread(new Runnable() {

			@Override
			public void run() {
				if(!mAllowLoad){
					//DebugUtil.debug("prepare to load");
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				if(mAllowLoad && firstLoad){
					loadImage(context,mImageUrl, mt, mListener,index);
				}

				if(mAllowLoad && mt <= mStopLoadLimit && mt >= mStartLoadLimit){
					loadImage(context,mImageUrl, mt, mListener,index);
				}
			}

		}).start();
	}
	
	
	public Drawable getSoftReferenceDrawable(String mImageUrl){
		
		if (imageCache.containsKey(mImageUrl)) { 
			SoftReference<Drawable> softReference = imageCache.get(mImageUrl);  
			final Drawable d = softReference.get();  
			if (d != null) { 
				return d;
			}
		}
		
		return null;
		
	}

	private void loadImage(Context context,final String mImageUrl,final Integer mt,final OnImageLoadListener mListener
			,final Integer index){

		if (imageCache.containsKey(mImageUrl)) {  
			SoftReference<Drawable> softReference = imageCache.get(mImageUrl);  
			final Drawable d = softReference.get();  
			if (d != null) {  
				if(mAllowLoad || (mt <= mStopLoadLimit && mt >= mStartLoadLimit)){
					mListener.onImageLoad(mt, d, index);
				}
				return;  
			}  
		}  
		try {
			final Drawable d = loadImageFromNetwork(context, mImageUrl);
			if(d != null){
				imageCache.put(mImageUrl, new SoftReference<Drawable>(d));
			}
			if(mAllowLoad || (mt <= mStopLoadLimit && mt >= mStartLoadLimit)){
				mListener.onImageLoad(mt, d, index);
			}
		} catch (Exception e) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					mListener.onError(mt);
				}
			});
			e.printStackTrace();
		}
	}

	public static Drawable loadImageFromNetwork(Context context, String path) throws Exception {
		Drawable drawable = null;
		byte[] data = DataService.getImage(path);
		if(data!=null){  
			final Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap  
			drawable = new BitmapDrawable(context.getResources(), mBitmap);
		}
		return drawable;
	}

	public static Drawable loadImageFromUrl(String url) throws IOException {
		//DebugUtil.debug(url);
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File f = new File(Environment.getExternalStorageDirectory()+"/TestSyncListView/"+/*MD5.getMD5*/(url));
			if(f.exists()){
				FileInputStream fis = new FileInputStream(f);
				Drawable d = Drawable.createFromStream(fis, "src");
				return d;
			}
			URL m = new URL(url);
			InputStream i = (InputStream) m.getContent();
			DataInputStream in = new DataInputStream(i);
			FileOutputStream out = new FileOutputStream(f);
			byte[] buffer = new byte[1024];
			int   byteread=0;
			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			in.close();
			out.close();
			//Drawable d = Drawable.createFromStream(i, "src");
			return loadImageFromUrl(url);
		}else{
			URL m = new URL(url);
			InputStream i = (InputStream) m.getContent();
			Drawable d = Drawable.createFromStream(i, "src");
			return d;
		}

	}


}
