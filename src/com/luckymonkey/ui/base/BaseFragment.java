package com.luckymonkey.ui.base;

import java.io.Serializable;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.luckymonkey.R;
import com.luckymonkey.util.SpUtil;
import com.luckymonkey.util.Utils;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment {
	protected Context mContext;

	protected LayoutInflater mInflater = null;

	protected LinearLayout containtView;

	protected View contentView;

	protected boolean mSlideFinish = false;

	protected int mDownX = 0;

	protected int finishAnimId = 0;

	protected Intent mIntent;

	boolean isStartActivity = false;

	protected Toast mSToast = null;

	protected Toast mLToast = null;

	public SpUtil mSpUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		mInflater = LayoutInflater.from(mContext);
		mSpUtil = SpUtil.getInstance(mContext);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// 调用顺序
		onInit();
		onFindViews();
		onInitViewData();
		onBindListener();
	}
	
	/**
	 * 初始�?优先顺序�?br/>
	 * <font color=red>onInit();</font><br/>
	 * onFindViews();<br/>
	 * onInitViewData();<br/>
	 * onBindListener();<br/>
	 */
	public abstract void onInit();

	/**
	 * 查找控件 <br/>
	 * 优先顺序�?br/>
	 * onInit();<br/>
	 * <font color=red>onFindViews();</font><br/>
	 * onInitViewData();<br/>
	 * onBindListener();<br/>
	 */
	public abstract void onFindViews();

	/**
	 * 初始化控件内�?优先顺序�?br/>
	 * onInit();<br/>
	 * onFindViews();<br/>
	 * <font color=red>onInitViewData();</font><br/>
	 * onBindListener();<br/>
	 */
	public abstract void onInitViewData();

	/**
	 * 注册控件事件 优先顺序�?br/>
	 * onInit();<br/>
	 * onFindViews();<br/>
	 * onInitViewData();<br/>
	 * <font color=red>onBindListener();</font><br/>
	 */
	public abstract void onBindListener();
	
	public void setTitle(String title) {
	}

	/**
	 * 打开滑动�?��此Activity的功�?<功能详细描述>
	 * 
	 * @param isOpen
	 * @see [类�?�?方法、类#成员]
	 */
	public void setSlide2Finish(boolean isOpen) {
		mSlideFinish = isOpen;
	}

	/**
	 * 结束此activity时动�?从左边移到右�?<功能详细描述>
	 * 
	 * @see [类�?�?方法、类#成员]
	 */
	public void finishWithLeftAnim() {
		finishAnimId = R.anim.from_left_out;
	}

	/**
	 * 结束此activity时动�?从上�?��移动 <功能详细描述>
	 * 
	 * @see [类�?�?方法、类#成员]
	 */
	public void finishWithDownAnim() {
		finishAnimId = R.anim.from_up_out;
	}

	/**
	 * 直接启动�?��activity <功能详细描述>
	 * 
	 * @param cla
	 * @see [类�?�?方法、类#成员]
	 */
	public void startActivity(Class<? extends Activity> cla) {
		if (isStartActivity)
			return;
		isStartActivity = true;
		initIntent(cla);
		startActivity();
	}

	/**
	 * 启动带一个外部数据的activity <功能详细描述>
	 * 
	 * @param key
	 * @param value
	 * @param cla
	 * @see [类�?�?方法、类#成员]
	 */
	public void startActivity(String key, Object value, Class<? extends Activity> cla) {
		if (isStartActivity)
			return;
		isStartActivity = true;
		initIntent(cla);
		putExtra(key, value);
		startActivity();
	}

	public void initIntent(Class<? extends Activity> cla) {
		mIntent = new Intent(mContext, cla);
	}

	/**
	 * intent装载外部数据，可以使用int String boolean Serializable long double <功能详细描述>
	 * 
	 * @param key
	 * @param value
	 * @see [类�?�?方法、类#成员]
	 */
	public void putExtra(String key, Object value) {
		if (mIntent != null && key != null && value != null) {
			if (value instanceof Integer) {
				mIntent.putExtra(key, (Integer) value);
			} else if (value instanceof String) {
				mIntent.putExtra(key, (String) value);
			} else if (value instanceof Serializable) {
				mIntent.putExtra(key, (Serializable) value);
			} else if (value instanceof Boolean) {
				mIntent.putExtra(key, (Boolean) value);
			} else if (value instanceof Long) {
				mIntent.putExtra(key, (Long) value);
			} else if (value instanceof Double) {
				mIntent.putExtra(key, (Double) value);
			}
		}
	}

	public void startActivity() {
		if (mIntent != null)
			startActivity(mIntent);
	}

	/**
	 * 启动带一个外部数据的activity <功能详细描述>
	 * 
	 * @param key
	 * @param value
	 * @param cla
	 * @see [类�?�?方法、类#成员]
	 */
	public void startActivityForResult(String key, Object value, Class<? extends Activity> cla, int requestCode) {
		if (isStartActivity)
			return;
		isStartActivity = true;
		initIntent(cla);
		putExtra(key, value);
		startActivityForResult(requestCode);
	}

	/**
	 * 直接启动�?��activity <功能详细描述>
	 * 
	 * @param cla
	 * @see [类�?�?方法、类#成员]
	 */
	public void startActivityForResult(Class<? extends Activity> cla, int requestCode) {
		if (isStartActivity)
			return;
		isStartActivity = true;
		initIntent(cla);
		startActivityForResult(requestCode);
	}

	public void startActivityForResult(int requestCode) {
		if (mIntent != null)
			startActivityForResult(mIntent, requestCode);
	}

	/**
	 * Toast 短时间显�?<功能详细描述>
	 * 
	 * @param message
	 * @see [类�?�?方法、类#成员]
	 */
	public void showToastShort(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	public void showToastShort(int id) {
		Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast 长时间显�?<功能详细描述>
	 * 
	 * @param message
	 * @see [类�?�?方法、类#成员]
	 */
	public void showToastLong(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Toast 长时间显�?<功能详细描述>
	 * 
	 * @param message
	 * @see [类�?�?方法、类#成员]
	 */
	public void showToastLong(int id) {
		Toast.makeText(mContext, id, Toast.LENGTH_LONG).show();
	}

	/**
	 * Toast <功能详细描述>
	 * 
	 * @param str资源ID
	 * @see [类�?�?方法、类#成员]
	 */
	public void showToast(int strResID) {
		String msgStr = this.getResources().getString(strResID);
		showToastLong(msgStr);
	}

	/**
	 * 显示提示
	 * 
	 * @param message
	 */
	public void showToast(String message) {
		showToastLong(message);
	}

	// jiang add
	protected Dialog progressDialog;

	protected void runOnUiThreadSafety(Runnable runnable) {
		Utils.runOnUiThreadSafety(getActivity(), runnable);
	}

	protected void toCloseProgressMsg() {
		runOnUiThreadSafety(new Runnable() {
			@Override
			public void run() {
				closeProgressDialog();
			}
		});
	}

	protected Dialog getProgressDialog(String msg) {
		ProgressDialog fddProgressDialog = new ProgressDialog(getActivity());
		fddProgressDialog.setMessage(msg);
		return fddProgressDialog;
	}

	private void closeProgressDialog() {
		Utils.clossDialog(progressDialog);
	}

	protected void toShowProgressMsg(final String msg) {
		runOnUiThreadSafety(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null && progressDialog.isShowing()) {
					if (progressDialog instanceof ProgressDialog) {
						((ProgressDialog) progressDialog).setMessage(msg);
					} else {
						try {
							progressDialog.getClass().getMethod("setMessage", String.class).invoke(progressDialog, msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					progressDialog = getProgressDialog(msg);
					progressDialog.show();
				}
			}
		});
	}

	public void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		bitmap = null;
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(mContext);
	}

	@Override
	public void onResume() {
		super.onResume();
		isStartActivity = false;
		// MobclickAgent.onResume(mContext);

	}

	/**
	 * 友盟打点记录
	 * 
	 * @param record
	 */
	protected void mobClickEvent(String record) {
		MobclickAgent.onEvent(mContext, record);
	}

	protected void mobClickEvent(String id, String param) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", param);
		MobclickAgent.onEvent(mContext, id, map);
	}
}
