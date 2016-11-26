package com.zhaolong.android.sbbx.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class ImgUtils {
	
	/**
	 * 图片转化成base64字符串  
	 * @param imgFile //待处理的图片  
	 * @return
	 */
	public static String getImageToBase64Str(String imgFile)  
	{//将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
		if(imgFile == null){
			return null;
		}
		InputStream in = null;  
		byte[] data = null;  
		//读取图片字节数组  
		try   
		{  
			in = new FileInputStream(imgFile);          
			data = new byte[in.available()];  
			in.read(data);  
			in.close();  
		}   
		catch (IOException e)   
		{  
			e.printStackTrace();  
		}  
		//对字节数组Base64编码  
		String s = Base64.encode(data);
		return s;//返回Base64编码过的字节数组字符串  
	}
	
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
	{
		if (bitmap == null) {
			return null;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	
	/**
	 * 
	 * @param angle
	 * @param bitmap
	 * @param type 根据宽（1）/高（2）缩放图片
	 * @param value
	 * @return
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap, int type, int value)
	{
		if (bitmap == null) {
			return null;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		switch (type) {
		case 1:
			matrix.postScale(value/(float)bitmap.getWidth(),value/(float)bitmap.getWidth()); 
			break;

		case 2:
			matrix.postScale(value/(float)bitmap.getHeight(),value/(float)bitmap.getHeight()); 
			break;
		}
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	
	/**
	 * 
	 * @param bitmap
	 * @param type 根据宽（1）/高（2）缩放图片
	 * @param value
	 * @return
	 */
	public static Bitmap rotaingImageView(Bitmap bitmap, int type, int value)
	{
		if (bitmap == null || value <= 0) {
			return null;
		}

		Matrix matrix = new Matrix();
		switch (type) {
		case 1:
			matrix.postScale(value/(float)bitmap.getWidth(),value/(float)bitmap.getWidth()); 
			break;

		case 2:
			matrix.postScale(value/(float)bitmap.getHeight(),value/(float)bitmap.getHeight()); 
			break;
		}
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	
	public static int readPictureDegree(String path)
	{
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt("Orientation", 1);

			switch (orientation) {
			case 6:
				degree = 90;
				break;
			case 3:
				degree = 180;
				break;
			case 8:
				degree = 270;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 图片选区裁剪
	 * @param activity
	 * @param uri 裁剪后的图片路径
	 * @param olduri 原图
	 * @param requestCode  onActivityResult()
	 */
	public static void cropImageUri(Activity activity,boolean isFromCamera, Uri uri, Uri olduri, int requestCode, int outputX, int outputY)
	  {
	    if (isFromCamera) {
	      int indexColon = olduri.toString().indexOf(":");
	      if (-1 == indexColon) {
	        return;
	      }
	      String pathType = olduri.toString().substring(0, indexColon);
	      if ("content".equals(pathType)) {
	        String[] proj = { "_data" };
	        @SuppressWarnings("deprecation")
			Cursor actualimagecursor = activity.managedQuery(olduri, proj, null, null, null);
	        //int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow("_data");
	        actualimagecursor.moveToFirst();
	        String img_path = getPath(activity, olduri);
	        File file = new File(img_path);
	        olduri = Uri.fromFile(file);
	        if (null == olduri)
	          return;
	      }
	      else if ("file".equals(pathType)) {
	        Boolean isValidatePicturePath = Boolean.valueOf(HlpUtils.isValidatePicturePath(olduri.toString()));

	        if (!isValidatePicturePath.booleanValue())
	          return;
	      }
	      else {
	        return;
	      }
	    }
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setDataAndType(olduri, "image/*");

	    intent.putExtra("crop", "true");
	    if(outputX == outputY){
	    	intent.putExtra("aspectX", 1);
	    	intent.putExtra("aspectY", 1);
	    	
	    	intent.putExtra("outputX", outputX);
	    	intent.putExtra("outputY", outputY);
	    }
	    //intent.putExtra("scale", true);

	    intent.putExtra("output", uri);

	    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    intent.putExtra("return-data", false);
	    intent.putExtra("noFaceDetection", true);
	    activity.startActivityForResult(intent, requestCode);
	  }
	
	@SuppressLint("NewApi")
	public static String getPath(Context context, Uri uri)
	{
		boolean isKitKat = Build.VERSION.SDK_INT >= 19;

		if ((isKitKat) && (DocumentsContract.isDocumentUri(context, uri)))
		{
			if (isExternalStorageDocument(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			else
			{
				if (isDownloadsDocument(uri))
				{
					String id = DocumentsContract.getDocumentId(uri);
					Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id).longValue());

					return getDataColumn(context, contentUri, null, null);
				}

				if (isMediaDocument(uri)) {
					String docId = DocumentsContract.getDocumentId(uri);
					String[] split = docId.split(":");
					String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type))
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					else if ("video".equals(type))
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					//String selection = "_id=?";
					String[] selectionArgs = { split[1] };

					return getDataColumn(context, contentUri, "_id=?", selectionArgs);
				}
			}
		} else {
			if ("content".equalsIgnoreCase(uri.getScheme()))
			{
				if (isGooglePhotosUri(uri)) {
					return uri.getLastPathSegment();
				}
				return getDataColumn(context, uri, null, null);
			}

			if ("file".equalsIgnoreCase(uri.getScheme())) {
				return uri.getPath();
			}
		}
		return null;
	}
	
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs)
	{
		Cursor cursor = null;
		//String column = "_data";
		String[] projection = { "_data" };
		try
		{
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

			if ((cursor != null) && (cursor.moveToFirst())) {
				int index = cursor.getColumnIndexOrThrow("_data");
				String str1 = cursor.getString(index);
				return str1;
			}
		}
		finally
		{
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri)
	{
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri)
	{
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri)
	{
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static boolean isGooglePhotosUri(Uri uri)
	{
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
