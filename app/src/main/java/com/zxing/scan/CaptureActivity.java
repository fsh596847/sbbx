package com.zxing.scan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zhaolong.android.sbbx.R;
import com.zhaolong.android.sbbx.ui.DeviceDetailActivity;
import com.zhaolong.android.sbbx.ui.DoctorRepairsActivity;
import com.zhaolong.android.sbbx.ui.EngineerRepairActivity;
import com.zhaolong.android.sbbx.utils.mLog;

/**
 * 拍照的Activity
 * 
 * @author Baozi
 * 
 */
public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 1.0f;
	private boolean vibrate;
	
	public static final int COM_ACTIVATE = 1;
	public static final int COM_REPAIRS = 2;
	public static final int COM_SGIN = 3;
	private int TYPE;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mo_scanner_main);
		
		TYPE = getIntent().getIntExtra("type", -1);
		
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.mo_scanner_viewfinder_view);

		initView();

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

	}

	private void initView() {
		findViewById(R.id.mo_scanner_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

		findViewById(R.id.mo_scanner_light).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 闪光灯
						light();
					}
				});
		
		TextView tvTitle = (TextView) findViewById(R.id.mo_scanner_title);
		switch (TYPE) {
		case COM_ACTIVATE:
			tvTitle.setText("扫码激活");
			break;

		case COM_REPAIRS:
			tvTitle.setText("扫码报修");
			break;
			
		case COM_SGIN:
			tvTitle.setText("扫码签到");
			break;
		}
	}
	
	public void handleDecode(final Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String recode = recode(result.toString());
		mLog.d("http", "code："+recode);
		switch (TYPE) {
		case COM_ACTIVATE:
			startActivity(new Intent(CaptureActivity.this, DeviceDetailActivity.class)
			.putExtra("code", recode));
			break;

		case COM_REPAIRS:
			startActivity(new Intent(CaptureActivity.this, DoctorRepairsActivity.class)
			.putExtra("code", recode));
			break;
			
		case COM_SGIN:
			startActivity(new Intent(CaptureActivity.this, EngineerRepairActivity.class)
			.putExtra("code", recode));
			break;
		}
		finish();
	}

	boolean flag = true;
	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();

		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();

		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.mo_scanner_preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		// viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.mo_scanner_beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	/**
	 * 中文乱码
	 * 
	 * 暂时解决大部分的中文乱码 但是还有部分的乱码无法解决 .
	 * 
	 * 如果您有好的解决方式 请联系 2221673069@qq.com
	 * 
	 * 我会很乐意向您请教 谢谢您
	 * 
	 * @return
	 */
	private String recode(String str) {
		String formart = "";

		try {
			boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
					.canEncode(str);
			if (ISO) {
				formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
				Log.i("1234      ISO8859-1", formart);
			} else {
				formart = str;
				Log.i("1234      stringExtra", str);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return formart;
	}

	/**
	 * 将bitmap由RGB转换为YUV //TOOD: 研究中
	 * 
	 * @param bitmap
	 *            转换的图形
	 * @return YUV数据
	 */
	public byte[] rgb2YUV(Bitmap bitmap) {
		// 该方法来自QQ空间
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		int len = width * height;
		byte[] yuv = new byte[len * 3 / 2];
		int y, u, v;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int rgb = pixels[i * width + j] & 0x00FFFFFF;

				int r = rgb & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb >> 16) & 0xFF;

				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
				u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
				v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

				y = y < 16 ? 16 : (y > 255 ? 255 : y);
				u = u < 0 ? 0 : (u > 255 ? 255 : u);
				v = v < 0 ? 0 : (v > 255 ? 255 : v);

				yuv[i * width + j] = (byte) y;
				// yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
				// yuv[len + (i >> 1) * width + (j & ~1) + 1] = (byte) v;
			}
		}
		return yuv;
	}
}