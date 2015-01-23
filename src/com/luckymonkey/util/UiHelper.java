package com.luckymonkey.util;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.luckymonkey.R;
import com.luckymonkey.ui.image.BrowseImageActivity;

public class UiHelper {

	public static final int REQUEST_PICK = 9162;
	public static final int REQUEST_TAKE = 9163;
	public static final int REQUEST_CROP = 9164;

	public static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * Utility method that starts an image picker since that often precedes a
	 * crop
	 * 
	 * @param activity
	 *            Activity that will receive result
	 */
	public static void pickImage(Activity activity) {
		
		Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*");
		intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		try {
			activity.startActivityForResult(intent, REQUEST_PICK);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity, R.string.picture_pick_error, Toast.LENGTH_SHORT).show();
		}
	}

	public static void takeImage(Activity activity, Uri photoUri) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		try {
			activity.startActivityForResult(intent, REQUEST_TAKE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity, R.string.picture_pick_error, Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void cropPickImage(Activity activity, Uri imageUri) {
		Intent intent = new Intent();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 256);
		intent.putExtra("outputY", 256);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		try {
			activity.startActivityForResult(intent, REQUEST_PICK);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity, R.string.picture_pick_error, Toast.LENGTH_SHORT).show();
		}
	}

	public static void cropTakeImage(Activity activity, Uri dataUri, Uri outputUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(dataUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 256);
		intent.putExtra("outputY", 256);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		try {
			activity.startActivityForResult(intent, REQUEST_CROP);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity, R.string.picture_pick_error, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 根据uri获取图片的真实路�?
	 * 
	 * @param activity
	 *            活动对象
	 * @param uri
	 *            uri
	 * @return 图片的真实路�?	 */
	public static String getPath(Context context, Uri source, Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		String brand = Build.BRAND;
		if (!isKitKat || (!TextUtils.isEmpty(brand) && (brand.equalsIgnoreCase("meizu") || brand.equalsIgnoreCase("xiaomi")))) {
			return getOldPath(context, source);
		} else {
			return getNewPath(context, uri);
		}
	}

	/**
	 * 根据uri获取图片的真实路�?
	 * 
	 * @param activity
	 *            活动对象
	 * @param uri
	 *            uri
	 * @return 图片的真实路�?	 */
	public static String getOldPath(Context context, Uri uri) {
		String path=uri.getPath();
		try{
			String[] proj = { MediaColumns.DATA };
			// 好像是android多媒体数据库的封装接口，具体的看Android文档
			Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
			// 按我个人理解 这个是获得用户�?择的图片的索引�?
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			// 将光标移至开�?，这个很重要，不小心很容易引起越�?			cursor.moveToFirst();
			// �?��根据索引值获取图片路�?			path = cursor.getString(columnIndex);
		}catch(Exception e){
			//Uri是一个文件类型�?不是�?��多媒体类型，也就是说已file结尾而不是content结尾
		}
		return path;
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getNewPath(final Context context, final Uri uri) {

	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }

	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };

	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {

	        // Return the remote address
	        if (isGooglePhotosUri(uri))
	            return uri.getLastPathSegment();

	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	
    public static void toBrowseImage(Context context, String imageUrl) {
    	if (!TextUtils.isEmpty(imageUrl)) {
	    	ArrayList<String> images = new ArrayList<String>();
			images.add(imageUrl);
			
			toBrowseImage(context, images, 0);
    	}
	}
    
    public static void toBrowseImage(Context context, ArrayList<String> images, int position) {
		toBrowseImage(context, images, position, null);
	}
	
	public static void toBrowseImage(Context context, ArrayList<String> images, int position, String desc) {
		Intent intent = new Intent();
		intent.setClass(context, BrowseImageActivity.class);
		intent.putStringArrayListExtra(BrowseImageActivity.EXTRA_IMAGW_ARRAY, images);
		intent.putExtra(BrowseImageActivity.EXTRA_IMAGW_INDEX, position);
		intent.putExtra(BrowseImageActivity.EXTRA_IMAGW_DESC, desc);
		context.startActivity(intent);
	}

}
