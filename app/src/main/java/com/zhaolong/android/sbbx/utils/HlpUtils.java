package com.zhaolong.android.sbbx.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class HlpUtils {
	
	
	public static Drawable getCompoundDrawable(Context context,int id){
		Drawable drawable = context.getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		return drawable;
	}

	static public SimpleDateFormat dateIntFormatter = new SimpleDateFormat(
			"yyyyMMdd", Locale.CHINESE);// EEE HH:mm
	static public SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINESE);// EEE HH:mm
	static public SimpleDateFormat dateFormatterYyyy_MM_dd = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINESE);// EEE HH:mm
	static public SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Locale.CHINESE);// EEE
	static public SimpleDateFormat datetimeFormatters = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.CHINESE);// EEE
	static public SimpleDateFormat datetimeFormatter4FileName = new SimpleDateFormat(
			"yyyy-MM-dd HH.mm.ss", Locale.CHINESE);// EEE
	static public SimpleDateFormat datetimeFormatterHHmm = new SimpleDateFormat(
			"HH:mm", Locale.CHINESE);// EEE
	static public SimpleDateFormat datetimeFormatterMMdd = new SimpleDateFormat(
			"MM-dd", Locale.CHINESE);// EEE
	static public SimpleDateFormat datetimeFormatterM_d = new SimpleDateFormat(
			"M-d", Locale.CHINESE);// EEE
	static public SimpleDateFormat datetimeFormatterd = new SimpleDateFormat(
			"d", Locale.CHINESE);//一位或者2位日期
	static public SimpleDateFormat datetimeFormatterEEE = new SimpleDateFormat(
			"EEE", Locale.CHINESE);//中文，星期日

	static public void setText(TextView v, String s){
		if(!isEmpty(s)){
			v.setText(s);
		}
	}
	
	static public String getSerialNo(Context context) {
		//2014-4-8,编号从下发的参数中获取
		
		String ret = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if ("SERIAL".equalsIgnoreCase(field.getName())) {
					ret = field.get(null).toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	static public boolean isEmpty(Object s){
		if (s instanceof String){
			return s==null || "".equals(s) || "null".equalsIgnoreCase((String)s);
		}else{
			return s==null;
		}
	}
	/**
	 * 字符串转换到日期，不含时间部分
	 * 
	 * @param txt
	 * @return
	 */
	static public Date str2Date(String txt) {
		Date d = null;
		try {
			if (txt != null && !"".equals(txt.trim())) {
				d = dateFormatter.parse(txt);
			}
		} catch (Exception e) {

		}
		return d;
	}

	/**
	 * 字符串转换到日期，含时间部分
	 * 
	 * @param txt
	 * @return
	 */
	static public Date str2DateTime(String txt) {
		Date d = null;
		try {
			if (txt != null && !"".equals(txt.trim())) {
				d = datetimeFormatter.parse(txt);
			}
		} catch (Exception e) {

		}
		return d;
	}
	
	/**
	 * 手机号验证
	 * @param number
	 * @return
	 */
	public static boolean checkPhone(String number) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[6-8]))\\d{8}$");
		Matcher m = p.matcher(number);
		return m.matches();
	}

	/**
	 * copy 文件
	 * 
	 * @param source
	 *            源文件全路径
	 * @param dest
	 *            目标文件路径
	 * @param overWrite
	 *            是否覆盖
	 * @throws IOException
	 */
	public static void copyFile(String source, String dest, boolean overWrite)
			throws IOException {
		if (new File(dest).exists() && !overWrite) {
			throw new IOException("文件已存在");
		}
		byte[] iobuff = new byte[1024];
		int bytes;
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(dest);
		try {
			bytes = fis.read(iobuff, 0, 1024);
			while (bytes != -1) {
				fos.write(iobuff, 0, bytes);
				bytes = fis.read(iobuff, 0, 1024);
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 将asset下的目录copy到sd卡
	 * @param context
	 * @param assetDir
	 * @param dir
	 * @throws IOException
	 */
	static public void CopyAssets(Context context,String assetDir, String dir) throws IOException {   
        String[] files = context.getAssets().list(assetDir); 
        File mWorkingPath = new File(dir);     
        if (!mWorkingPath.exists()) {   
            if (!mWorkingPath.mkdirs()) {   
            	throw new IOException("创建目录失败");
            }   
        }   
        for (int i = 0; i < files.length; i++) {  
        	FileOutputStream out = null;
            try {   
                String fileName = files[i];   
                // we make sure file name not contains '.' to be a folder.   
                if (!fileName.contains(".")) {   
                    if (0 == assetDir.length()) {   
                        CopyAssets(context,fileName, dir + fileName + "/");   
                    } else {   
                        CopyAssets(context,assetDir + "/" + fileName, dir + fileName   
                                + "/");   
                    }   
                    continue;   
                }   
                File outFile = new File(mWorkingPath, fileName);   
                if (outFile.exists())   
                    outFile.delete();   
                InputStream in = null;   
                if (0 != assetDir.length()) {   
                    in = context.getAssets().open(assetDir + "/" + fileName);   
                } else {   
                    in = context.getAssets().open(fileName);   
                }   
                out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];   
                int len;   
                while ((len = in.read(buf)) > 0) {   
                    out.write(buf, 0, len);   
                }   
            }finally{
            	if (out != null){
            		try{
            			out.close();
            		}catch (Exception e) {
						e.printStackTrace();
					}
            	}
            }
  
        }   
    }  

	public static void createWatermarkBitmap(String srcFileName,
			String destFileName, String watermark) {
		Bitmap src = BitmapFactory.decodeFile(srcFileName);
		Bitmap dest = createWatermarkBitmap(src, watermark);
		File myCaptureFile = new File(destFileName);
		try {// 将照片存放在预设的文件中
			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			dest.compress(Bitmap.CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (src != null) {
				src.recycle();
			}
			if (dest != null) {
				dest.recycle();
			}
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap getSample(String FileName, int maxNumOfPixels) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(FileName, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels);
		opts.inJustDecodeBounds = false;
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeFile(FileName, opts);
		} catch (OutOfMemoryError err) {
		}
		return bmp;
	}
	public static Bitmap getBitmap4Length(String FileName, int maxLen) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(FileName, opts);
		if (opts.outHeight>opts.outWidth){
			opts.inSampleSize = opts.outHeight/maxLen;
		}else{
			opts.inSampleSize = opts.outWidth/maxLen;
		}
		opts.inJustDecodeBounds = false;
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeFile(FileName, opts);
		} catch (OutOfMemoryError err) {
		}
		return bmp;
	}
	/**
	 * 
	 * @param src
	 *            原图片
	 * @param watermark
	 *            要打的水印图片
	 * @return Bitmap 打好水印的图片
	 */
	public static Bitmap createWatermarkBitmap(Bitmap src, String watermark) {
		if (src == null) {
			return null;
		}
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();

		// create the new blank bitmap
		Bitmap newb = Bitmap
				.createBitmap(srcWidth, srcHeight, Config.ARGB_8888);// 创建一个新的和src长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);// 在0,0坐标开始画入src
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(20);
		if (watermark != null) {
			cv.drawText(watermark, 10, 50, paint);// 这是画入水印文字，在画文字时，需要指定paint
		}
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		cv = null;
		return newb;
	}

	/**
	 * 
	 * @param bm 原图
	 * @param newWidth 新宽度
	 * @param newHeight 新高度
	 * @return
	 */
	static public Bitmap getXYPic(Bitmap bm,int newWidth,int newHeight){
		try{
			// 获得图片的宽高
		    int width = bm.getWidth();
		    int height = bm.getHeight();
		    // 计算缩放比例
		    float scaleWidth = ((float) newWidth) / width;
		    float scaleHeight = ((float) newHeight) / height;
		    // 取得想要缩放的matrix参数
		    Matrix matrix = new Matrix();
		    matrix.postScale(scaleWidth, scaleHeight);
		    // 得到新的图片
		    Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
		      true);
		    return newbm;
		}catch (Exception e) {
			return bm;
		}
	}
	/**
	 * 根据源文件生成缩略图
	 * 
	 * @param srcFileName
	 * @param destFileName
	 */
	static public void genSmallPic(String srcFileName, String destFileName) {
		Bitmap bm = null;
		BufferedOutputStream bos = null;
		FileOutputStream fout = null;
		File f = null;
		try {
			bm = BitmapFactory.decodeFile(srcFileName);
			if (bm == null) {
				return;
			}
			bm = ThumbnailUtils.extractThumbnail(bm, 100, 100);
			f = new File(destFileName);
			if (f != null && !f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			fout = new FileOutputStream(destFileName);
			bos = new BufferedOutputStream(fout);
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据源文件生成缩略图
	 * 
	 * @param srcFileName
	 * @param destFileName
	 */
	static public void genSmallPic21(String srcFileName, String destFileName) {
		Bitmap bm = null;
		Options op = new Options();
		op.inSampleSize = 72;
		op.inJustDecodeBounds = false;
		BufferedOutputStream bos = null;
		FileOutputStream fout = null;
		File f = null;
		try {
			bm = BitmapFactory.decodeFile(srcFileName, op);
			if (bm == null) {
				return;
			}
			f = new File(destFileName);
			if (f != null && !f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			fout = new FileOutputStream(destFileName);
			bos = new BufferedOutputStream(fout);
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeString2File(String fileName, String s) {
		File f = new File(fileName);
		if (f != null) {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			out.write(s.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	    /**  
	     * 追加文件：使用RandomAccessFile  
	     *   
	     * @param fileName 文件名  
	     * @param content 追加的内容  
	     */  
	    public static void appendString2File(String fileName, String content) { 
	    	RandomAccessFile randomFile = null;
	        try {   
	            // 打开一个随机访问文件流，按读写方式   
	            randomFile = new RandomAccessFile(fileName, "rw");   
	            // 文件长度，字节数   
	            long fileLength = randomFile.length();   
	            // 将写文件指针移到文件尾。   
	            randomFile.seek(fileLength);   
	            randomFile.writeBytes(content);    
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } finally{
	        	if(randomFile != null){
	        		try {
						randomFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        	}
	        }
	    }  

	static public void writeContextFileData(Context context,String fileName,String message,int mode){ 
		FileOutputStream fout = null;
		try {
			fout = context.openFileOutput(fileName, mode);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
   }    
	static public String readContextFileData(Context context,String fileName){ 
		String res = "";
		FileInputStream fin = null;
		try {
			fin = context.openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = new String(buffer, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return res;

    }   

	public static long getFileSize(String fileName) {
		File f = new File(fileName);
		if (f.exists()) {
			return f.length();
		} else {
			return 0;
		}
	}
	public static String readFileContentFromAsset(Context context,String fileName) throws IOException {
		InputStream in  = null;
		StringBuffer sb = new StringBuffer();
		byte[] buf = null;
		try{
			//Long len = context.getAssets().openFd(fileName).getLength();
			in = context.getAssets().open(fileName);
			buf = new byte[1024];
			int count = in.read(buf, 0, buf.length);
			while (count != -1){
				sb.append(new String(buf,0,count,"UTF-8"));
				count = in.read(buf, 0, buf.length);
			}
		}finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	public static String readFileContent(String fileName) {
		File f = new File(fileName);
		if (!f.exists()) {
			return "";
		}
		byte[] buf = new byte[(int) f.length()];
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
			int count = in.read(buf, 0, (int) f.length());
			if (count != (int) f.length()) {
				System.out
						.println("Read content from file is not equale the file length.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new String(buf);
	}

	/**
	 * 打开文件
	 * 
	 * @param file
	 */
	static public Intent openFile(File file) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		// 设置intent的data和Type属性。
		intent.setDataAndType(/* uri */Uri.fromFile(file), type);
		// 跳转
		return intent;
	}

	/**
	 * 根据文件名判断文件是否图片文件
	 * 
	 * @param fileName
	 * @return
	 */
	static public boolean isImage(String fileName) {
		String mimeType = getMIMEType(fileName);
		if (mimeType != null && mimeType.startsWith("image")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据文件名判断文件是否视频文件
	 * 
	 * @param fileName
	 * @return
	 */
	static public boolean isVideo(String fileName) {
		String mimeType = getMIMEType(fileName);
		if (mimeType != null && mimeType.startsWith("video")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据文件名判断文件是否视频文件
	 * 
	 * @param fileName
	 * @return
	 */
	static public boolean isVoice(String fileName) {
		String mimeType = getMIMEType(fileName);
		if (mimeType != null && mimeType.startsWith("audio")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	static public String getMIMEType(File file) {
		String fName = file.getName();
		return getMIMEType(fName);
	}

	static public String getMIMEType(String fName) {
		String type = "*/*";
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	static final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".amr", "audio/amr" }, { ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };

	/**
	 * compareStr：对String类型排序
	 * 
	 * @param @return 设定文件
	 * @return String 对象类型
	 * @throws
	 */
	static public int compareStr(String o1, String o2) {
		String s1 = (String) o1;
		String s2 = (String) o2;
		int len1 = s1.length();
		int len2 = s2.length();
		int n = Math.min(len1, len2);
		char v1[] = s1.toCharArray();
		char v2[] = s2.toCharArray();
		int pos = 0;
		while (n-- != 0) {
			char c1 = v1[pos];
			char c2 = v2[pos];

			if (c1 != c2) {
				return c1 - c2;
			}
			pos++;
		}
		return len1 - len2;
	}

	/**
	 * compareInt：对int类型排序
	 * 
	 * @param @return 设定文件
	 * @return String 对象类型
	 * @throws
	 * @since CodingExample　Ver 1.1
	 */
	static public int compareInt(Integer o1, Integer o2) {
		int val1 = o1.intValue();
		int val2 = o2.intValue();
		return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
	}

	static class CompratorByFileName implements Comparator<File> {
		public int compare(File file1, File file2) {
			return compareStr(file1.getAbsolutePath(), file2.getAbsolutePath());
		}
	}

	static class CompratorByFileSize implements Comparator<File> {
		public int compare(File file1, File file2) {
			long diff = file1.length() - file2.length();
			if (diff > 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		}
	}

	static class CompratorByFileTime implements Comparator<File> {
		public int compare(File file1, File file2) {
			long diff = file1.lastModified() - file2.lastModified();
			if (diff > 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		}
	}

	/**
	 * 根据含路径和文件名的字符串获取路径部分
	 * 
	 * @param filePathName
	 * @return
	 */
	static public String getFilePath(String filePathName) {
		if (filePathName == null) {
			return null;
		}
		int pos = filePathName.lastIndexOf("/");
		if (pos > -1) {
			return filePathName.substring(0, pos);
		} else {
			return "/";
		}
	}

	/**
	 * 根据含路径和文件名的字符串获取文件名部分
	 * 
	 * @param filePathName
	 * @return
	 */
	static public String getFileName(String filePathName) {
		if (filePathName == null) {
			return null;
		}
		int pos = filePathName.lastIndexOf("/");
		if (pos > -1 && pos < filePathName.length() - 1) {
			return filePathName.substring(pos + 1);
		} else {
			return filePathName;
		}
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	static public boolean fileExists(String fileName) {
		File f = new File(fileName);
		return f.exists();
	}

	/**
	 * 删除指定文件路径下面的所有文件和文件夹
	 * 
	 * @param file
	 *            文件或文件夹的File对象
	 * @return 删除操作是否成功
	 */
	public static boolean delFiles(File file) {
		boolean flag = false;
		try {
			if (file.exists()) {
				if (file.isDirectory()) {
					String[] contents = file.list();
					for (int i = 0; i < contents.length; i++) {
						File file2X = new File(file.getAbsolutePath() + "/"
								+ contents[i]);
						if (file2X.exists()) {
							if (file2X.isFile()) {
								flag = file2X.delete();
							} else if (file2X.isDirectory()) {
								delFiles(file2X);
							}
						} else {
							throw new RuntimeException("File not exist!");
						}
					}
				}
				flag = file.delete();
				if (!flag) {
					Thread.sleep(1000);
					flag = file.delete();
				}
				if (!flag) {
					Thread.sleep(1000);
					flag = file.delete();
				}
			} else {
				throw new RuntimeException("File not exist!");
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	static public Bitmap LoadBitmapFromFile(String fileName) {
		Bitmap result = null;
		if (fileName == null) {
			return result;
		}
		// File f = new File(fileName);
		// InputStream is = null;

		try {
			// is = new FileInputStream(f);
			boolean hasError = false;
			BitmapFactory.Options opts = null;
			do {
				try {
					if (hasError) {
						if (opts == null) {
							opts = new BitmapFactory.Options();
							opts.inSampleSize = 2;
						} else {
							opts.inSampleSize = 2 * opts.inSampleSize;
						}
					}
					result = BitmapFactory.decodeFile(fileName, opts);
					hasError = false;
				} catch (OutOfMemoryError e) {
					hasError = true;
				}
			} while (hasError);
			/*
			 * int length = (int)f.length(); byte[] buf = new byte[length];
			 * is.read(buf, 0, length); result =
			 * BitmapFactory.decodeByteArray(buf, 0, length);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/*
			 * if (is != null){ try { is.close(); } catch (IOException e) {
			 * e.printStackTrace(); } }
			 */
		}
		return result;
	}

	static public long getFolderSize(String fileName) {
		File f = new File(fileName);
		long length = 0l;
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (File af : fs) {
				if (af.isFile()) {
					length += af.length();
				} else {
					length += getFolderSize(af.getAbsolutePath());
				}
			}
		} else {
			length += f.length();
		}
		return length;
	}

	static public String formatSize(long size) {
		String suffix = null;

		if (size >= 1024) {
			suffix = "KiB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MiB";
				size /= 1024;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}

		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}

	public static String toMd5(byte[] bytes) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(bytes);
			return toHexString(algorithm.digest(), "");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static String toHexString(byte[] bytes, String separator) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			hexString.append(Integer.toHexString(0xFF & b)).append(separator);
		}
		return hexString.toString();
	}
	/**
	 * 根据url获取图片文件路径
	 * @param context
	 * @param fileUrl
	 * @return
	 */
	public static String getImagePathFromUri(Context context,Uri fileUrl)
    {
       String fileName = null;
       Uri filePathUri = fileUrl;
       if (fileUrl != null)
       {
           if (fileUrl.getScheme().toString().compareTo("content") == 0)
           {
               // content://开头的uri
              Cursor cursor = context.getContentResolver().query(fileUrl, null, null, null, null);
              if (cursor != null && cursor.moveToFirst())
              {
                  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                  fileName = cursor.getString(column_index); // 取出文件路径
                  //2013-08-12,在三星平板中，fileName不是/mnt开头，加了mnt后反而无法找到该文件，所以在需要的是否，先检查该文件是否存在，不存在在加mnt
  				  File file = new File(fileName);
  				  if (!file.exists()){
  					fileName = "/mnt" + fileName;
  				  }
                  /*if (!fileName.startsWith("/mnt"))
                  {
                     // 检查是否有”/mnt“前缀
                     fileName = "/mnt" + fileName;
                  }*/
                  cursor.close();
              }
           }
           else if (fileUrl.getScheme().compareTo("file") == 0) // file:///开头的uri
           {
              fileName = filePathUri.getPath().replace("file://", "");// 替换file://
              int index = fileName.indexOf("/sdcard");
              fileName  = index == -1 ? fileName : fileName.substring(index);
              File file = new File(fileName);
			  if (!file.exists()){
				fileName = "/mnt" + fileName;
			  }
           }
       }

       return fileName;

    }

	static public Drawable getDrawableFromFile(String fullFileName){
		Drawable drawable = null;
		try{
			File f =  new File(fullFileName);
			if (f.exists()){
				drawable = new BitmapDrawable(fullFileName );
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return drawable;
	}
	static public Drawable getDrawbleFromAssets(Context context,String fileName){
		InputStream is  = null;
		BitmapDrawable bd = null;
		try{
			is = context.getAssets().open(fileName);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			bd = new BitmapDrawable(bmp);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bd;
	}
	static public Bitmap getBitmapFromAssets(Context context,String fileName){
		InputStream is  = null;
		Bitmap bmp = null;
		try{
			is = context.getAssets().open(fileName);
			bmp = BitmapFactory.decodeStream(is);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bmp;
	}
	static public void showToast(Context context,String msg){
		Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
	static public void showToastLong(Context context,String msg){
		Toast t = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
	/**
	 * 网络是否可用
	 * @param context
	 * @return
	 */
	static public boolean isNetworkAvailable(Context context) {
        boolean active = false;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //check the networkInfos numbers
        if (cm != null){
	        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
	        for (int i = 0; i<networkInfos.length; i++) {
	            if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
	            	active = true;
	            }
	        }
        }
        return active;
    }
	public static boolean isNumeric(String str){ 
		for (int i = str.length();--i>=0;){ 
			if (!Character.isDigit(str.charAt(i))){
				return false; 
			} 
		}
		return true; 
	} 
	static public void showDailog(Activity context, String msg) {
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(msg).setCancelable(true).
				setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			}).show();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 提取字符串中的url，截取原则：a）http://开头  b）空白或者非中文结尾
	 * @param s
	 * @return
	 */
	static public List<String> getUrl(String s) {
		Pattern pattern = Pattern.compile("http://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(s);
        List<String> list = new ArrayList<String>();
        while(matcher.find()){              
            list.add(matcher.group());         
        }   
        return list;
	}

	static public void sendTextSms(String phoneNumber,String msg){  	
		if (phoneNumber != null && !"".equals(phoneNumber.trim()) ){
			SmsManager.getDefault().sendTextMessage(phoneNumber,null, msg, null, null);
		}
	}
	/**
	 * 设置手机飞行模式
	 * @param context
	 * @param enabling true:设置为飞行模式	false:取消飞行模式
	 */
	public static void setAirplaneModeOn(Context context,boolean enabling) {
		Settings.System.putInt(context.getContentResolver(),
                          Settings.System.AIRPLANE_MODE_ON,enabling ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enabling);
		context.sendBroadcast(intent);
	}
	/**
	 * 判断手机是否是飞行模式
	 * @param context
	 * @return
	 */
	public static boolean getAirplaneMode(Context context){
		int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                           Settings.System.AIRPLANE_MODE_ON, 0) ;
		return (isAirplaneMode == 1)?true:false;
	}
	//sim卡是否可读 
	static public boolean isCanUseSim(Context context) { 
	     try { 
	         TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
	  
	         return TelephonyManager.SIM_STATE_READY == mgr 
	                 .getSimState(); 
	     } catch (Exception e) { 
	         e.printStackTrace(); 
	     } 
	     return false; 
	 } 
	static public Date getNextDate(Date date,int days){
		if (date==null){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}
	static public Date getTheDate(Date date){
		Date date1 = date;
		try {
			date1 = dateFormatter.parse(dateFormatter.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date1;
	}
	/*
	 * 对Bitmap裁剪，使其变成圆形，这步最关键,
	 * newWidth最大为500
	 */
	public static Bitmap getCroppedBitmap(Bitmap bmp, int newWidth) {
		if (bmp == null){
			return null;
		}
		Bitmap sbmp = null;
		Bitmap output = null;
		try {
			if (newWidth>500){
				newWidth = 500;
			}
			if (bmp.getWidth() != newWidth || bmp.getHeight() != newWidth)
				sbmp = Bitmap.createScaledBitmap(bmp, newWidth, newWidth, false);
			else
				sbmp = bmp;

			output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
					Bitmap.Config.ARGB_8888);
			final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setDither(true);
			paint.setColor(Color.parseColor("#BAB399"));

			Canvas c = new Canvas(output);
			c.drawARGB(0, 0, 0, 0);
			c.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
					sbmp.getWidth() / 2 + 0.1f, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			c.drawBitmap(sbmp, rect, rect, paint);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (output == null){
			output = bmp;
		}

		return output;
	}
	/*
	 * 对Bitmap裁剪，使其变成圆形，这步最关键,
	 * newWidth最大为500
	 */
	public static Bitmap getSmallBitmap(Bitmap bmp, int newWidth) {
		if (bmp == null){
			return null;
		}
		Bitmap sbmp = null;
		try {
			if (bmp.getWidth() != newWidth || bmp.getHeight() != newWidth)
				sbmp = Bitmap.createScaledBitmap(bmp, newWidth, newWidth, false);
			else
				sbmp = bmp;

			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sbmp;
	}
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    /**
     * 获取中文星期几
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    /**
     * 获取中文星期几(短）
     * @param dt
     * @return
     */
    public static String getWeekOfDateShort(Date dt) {
        String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    
    public static boolean isValidatePicturePath(String picturePath)
	{
		Pattern p = Pattern.compile(".+(.JPEG|.jpeg|.JPG|.jpg|.BMP|.bmp|.PNG|.png)$");
		Matcher m = p.matcher(picturePath);
		return m.matches();
	}
}


