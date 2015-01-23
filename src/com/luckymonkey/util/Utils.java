package com.luckymonkey.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Utils {
	public static final String TAG = "PushDemoActivity";
	public static final String RESPONSE_METHOD = "method";
	public static final String RESPONSE_CONTENT = "content";
	public static final String RESPONSE_ERRCODE = "errcode";
	protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
	public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
	public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
	protected static final String EXTRA_ACCESS_TOKEN = "access_token";
	public static final String EXTRA_MESSAGE = "message";
	
	public static final String DATE_FORMAT_YMDMH = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";

	public static String logStringCache = "";

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private static long lastClickTime;

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}
	
	/**
	 * 获取当前app包信息对�?
	 * 
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	private static PackageInfo getCurrentAppPackageInfo(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			String packageName = context.getPackageName();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			return info;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取当前app的版本号.
	 * 
	 * @param context
	 * @return
	 */
	public static int getCurrentAppVersionCode(Context context) {
		PackageInfo info = getCurrentAppPackageInfo(context);
		int versionCode = info.versionCode;
		return versionCode;
	}

	/**
	 * 获取当前app的版本名�?
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentAppVersionName(Context context) {
		PackageInfo info = getCurrentAppPackageInfo(context);
		String version = info.versionName;
		return version;
	}

	/**
	 * 获取手机的imei.
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return imei;
	}

	// 用share preference来实现是否绑定的�?��。在ionBind且成功时设置true，unBind且成功时设置false
	public static boolean hasBind(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
	}

	public static void setBind(Context context, boolean flag) {
		String flagStr = "not";
		if (flag) {
			flagStr = "ok";
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("bind_flag", flagStr);
		editor.commit();
	}

	public static List<String> getTagsList(String originalText) {
		if (originalText == null || originalText.equals("")) {
			return null;
		}
		List<String> tags = new ArrayList<String>();
		int indexOfComma = originalText.indexOf(',');
		String tag;
		while (indexOfComma != -1) {
			tag = originalText.substring(0, indexOfComma);
			tags.add(tag);

			originalText = originalText.substring(indexOfComma + 1);
			indexOfComma = originalText.indexOf(',');
		}

		tags.add(originalText);
		return tags;
	}

	public static String getLogText(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString("log_text", "");
	}

	public static void setLogText(Context context, String text) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("log_text", text);
		editor.commit();
	}

	/**
	 * 判断网络是否可用.
	 * 
	 * @param context
	 *            上下�?	 * @return true代表网络可用，false代表网络不可�?
	 */
	public static boolean isNetworkValid(Context context) {
		boolean result = false;
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			result = false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null) {
				result = false;
			} else {
				if (info.isAvailable()) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * 显示（时间是短暂的）土司消息
	 * 
	 * @param context
	 *            上下�?	 * @param msg
	 *            消息内容
	 */
	public static void showMsg(Context context, String msg) {
		if (context != null && msg != null) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 外部存储是否可写(也可�?，true代表可写，false代表不可�?
	 * 
	 * @return
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
	
	public static File getOutputFile(Context context) {
		File file = null;
		if (isExternalStorageWritable()) {
			file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		} else {
			file = getOutputInternalFile(context, MEDIA_TYPE_IMAGE);
		}
		return file;
	}

	private static File getOutputInternalFile(Context context, int mediaType) {
		FileOutputStream openFileOutput = null;
		try {
			String mediaFileName = getMediaFileName(mediaType);
			openFileOutput = context.openFileOutput(mediaFileName, Context.MODE_WORLD_WRITEABLE);
			File fileStreamPath = context.getFileStreamPath(mediaFileName);
			return fileStreamPath;
		} catch (Exception e) {
			e.printStackTrace();
			File fileDir = context.getCacheDir();
			File mediaFile = getMediaFile(mediaType, fileDir.getAbsolutePath());
			return mediaFile;
		} finally {
			close(openFileOutput);
		}
	}
	
	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static File getOutputMediaFile(int mediaType) {
		String fileDir = getExternalStoragePublicDirectory("tdaoj");
		File mediaFile = getMediaFile(mediaType, fileDir);
		return mediaFile;
	}

	public static File getMediaFile(int mediaType, String fileDir) {
		File mediaFile = new File(fileDir + File.separator + getMediaFileName(mediaType));
		return mediaFile;
	}

	public static String getMediaFileName(int mediaType) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		if (mediaType == MEDIA_TYPE_IMAGE) {
			return "IMG_" + timeStamp + ".jpg";
		} else if (mediaType == MEDIA_TYPE_VIDEO) {
			return "VID_" + timeStamp + ".mp4";
		}
		throw new RuntimeException("媒体类型错误");
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	private static String getExternalStoragePublicDirectory(String dirName) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dirName);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String fileDir = mediaStorageDir.getPath();
		return fileDir;
	}

	/**
	 * 根据uri获取图片的真实路�?
	 * 
	 * @param activity
	 *            活动对象
	 * @param uri
	 *            uri
	 * @return 图片的真实路�?	 */
	public static String getPath(Activity activity, Uri uri) {
		String path=uri.getPath();
		try{
			String[] proj = { MediaColumns.DATA };
			// 好像是android多媒体数据库的封装接口，具体的看Android文档
			Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
			// 按我个人理解 这个是获得用户�?择的图片的索引�?
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			// 将光标移至开�?，这个很重要，不小心很容易引起越�?			cursor.moveToFirst();
			// �?��根据索引值获取图片路�?			path = cursor.getString(columnIndex);
		}catch(Exception e){
			//Uri是一个文件类型�?不是�?��多媒体类型，也就是说已file结尾而不是content结尾
		}
		return path;
	}

	/**
	 * 安全执行runnable的UI事件.
	 * 
	 * @param activity
	 * @param runnable
	 */
	public static void runOnUiThreadSafety(Activity activity,
			final Runnable runnable) {
		if (activity == null) {
			return;
		}
		if (runnable == null) {
			return;
		}
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 关闭处理中的对话�?
	 * 
	 * @param progressDialog
	 *            处理对话�?	 */
	public static void clossDialog(Dialog dialog) {
		if (dialog != null && dialog.isShowing() && dialog.getWindow() != null) {
			dialog.dismiss();
		}
	}

	public static String getFileName(String filePath) {
		String fileName = null;
		if (!TextUtils.isEmpty(filePath)) {
			int lastBackslashIndex = filePath.lastIndexOf("/");
			if (lastBackslashIndex >= 0) {
				fileName = filePath.substring(lastBackslashIndex + 1);
			}
		}
		if (TextUtils.isEmpty(fileName)) {
			fileName = System.currentTimeMillis() + ".jpg";
		}
		return fileName;
	}

	public static void call(Context context, String phone) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phone));
		context.startActivity(intent);
	}

	public static String formatTwoFractionDigits(double d) {
		return formatFractionDigits(d, 2);
	}

	public static String formatFractionDigits(double d, int count) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(count);
		return nf.format(d);
	}
	
	public static String encode(String str) {
		try {
			str = URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static String formatTime(long time) {
		return formatDate(new Date(time));
	}
	
	public static String formatDate(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YMDMH);
		return format.format(Date.parse(dateStr));
	}
	
	public static String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YMDMH);
		return format.format(date);
	}
	
	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	public static String formatTime(long time, String pattern) {
		return formatDate(new Date(time), pattern);
	}

	public static boolean checkEmail(String email) {
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return email.matches(EMAIL_REGEX);
	}
	
	public static boolean checkUsername(String username) {
		String USERNAME_REGEX = "^[a-zA-Z]{1}[a-zA-Z0-9|_]{3,15}$";
		return username.matches(USERNAME_REGEX);
	}

	public static void hideSoft(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void hideSoft(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static String formatSafetyPhone(String phone) {
		if (!TextUtils.isEmpty(phone) && phone.length() >= 11) {
			phone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
		}
		return phone;
	}

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

}
