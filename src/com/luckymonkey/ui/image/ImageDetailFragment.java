package com.luckymonkey.ui.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.luckymonkey.AppController;
import com.luckymonkey.R;
import com.luckymonkey.util.UiHelper;
import com.luckymonkey.widget.ZoomableImageView;
import com.luckymonkey.widget.ZoomableImageView.OnSingleClickListener;

public class ImageDetailFragment extends Fragment {
	private static final String IMAGE_EXTRA = "image_extra";
	private String imageUrl;
	public ZoomableImageView imageView;
	int img_position = 0, img_count = 0;

	private Bundle arguments;
	private int mDisplayWidth;
	private int mDisplayHeight;

	private boolean savable = false;

	public static ImageDetailFragment newInstance(String imageUrl, int position, int count, boolean savable) {
		final ImageDetailFragment f = new ImageDetailFragment();
		final Bundle args = new Bundle();
		args.putSerializable(IMAGE_EXTRA, imageUrl);
		args.putInt("pos", position);
		args.putInt("count", count);
		args.putBoolean("savable", savable);
		f.setArguments(args);

		return f;
	}

	public ImageDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		arguments = getArguments();
		if (arguments != null) {
			imageUrl = arguments.getString(IMAGE_EXTRA);
			img_position = arguments.getInt("pos");
			img_count = arguments.getInt("count");
			savable  = arguments.getBoolean("savable");
		}
		
		DisplayMetrics displayMetrics = UiHelper.getDisplayMetrics(getActivity());
		mDisplayWidth = displayMetrics.widthPixels;
		mDisplayHeight = displayMetrics.heightPixels;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View convertView = inflater.inflate(R.layout.fragment_image_detail, container, false);
		imageView = (ZoomableImageView) convertView.findViewById(R.id.image);
		imageView.setTag(img_position);
		imageView.setPos(img_position, img_count);
		imageView.setOnSingleClickListener(new OnSingleClickListener() {
			
			@Override
			public boolean onSingleClick(MotionEvent e) {
				FragmentActivity activity = getActivity();
				if (activity != null && !activity.isFinishing()) {
					((BrowseImageActivity)activity).onImageClick(imageView);
				}
				return false;
			}

			@Override
			public boolean onLongClick(MotionEvent e) {
				FragmentActivity activity = getActivity();
				if (savable && activity != null && !activity.isFinishing()) {
					((BrowseImageActivity)getActivity()).onImageLongClick(imageView);
				}
				return false;
			}
		});

		final View spinner = convertView.findViewById(R.id.loading);

		String imgUrl = imageUrl;
		if (!TextUtils.isEmpty(imgUrl)) {
			spinner.setVisibility(View.VISIBLE);
			AppController.getInstance().getImageLoader().get(imgUrl, new ImageListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					spinner.setVisibility(View.GONE);
				}
				
				@Override
				public void onResponse(ImageContainer response, boolean isImmediate) {
					Bitmap bitmap = response.getBitmap();
					if (bitmap != null) {
						spinner.setVisibility(View.GONE);
						imageView.setImageBitmap(bitmap);
					}
				}
			});
		} else {
			spinner.setVisibility(View.GONE);
		}

		return convertView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
