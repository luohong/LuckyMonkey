package com.luckymonkey.ui.image;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.luckymonkey.AppController;
import com.luckymonkey.R;
import com.luckymonkey.ui.base.BaseActivity;

public class BrowseImageActivity extends BaseActivity {
	public static final String EXTRA_IMAGW_FROM = "image_from";
	public static final String EXTRA_IMAGW_INDEX = "image_index";
	public static final String EXTRA_IMAGW_ARRAY = "image_array";
	public static final String EXTRA_IMAGW_DESC = "image_desc";

	private RelativeLayout rlTitleBar;
	private ScrollView svDesc;
	private TextView tvDesc;
	private TextView tvIndex;

	protected ViewPager vpImage;
	protected Integer mIndex;
	private ArrayList<String> mImages = new ArrayList<String>();
	private String mDesc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_image);
	}

	@Override
	public void onInit() {
		Intent intent = getIntent();
		if (intent != null) {
			mIndex = intent.getIntExtra(EXTRA_IMAGW_INDEX, 0);
			mImages = (ArrayList<String>) intent.getSerializableExtra(EXTRA_IMAGW_ARRAY);
			mDesc = intent.getStringExtra(EXTRA_IMAGW_DESC);
		}
		setUp();
	}

	@Override
	public void onFindViews() {
		rlTitleBar = (RelativeLayout) findViewById(R.id.rl_title_bar);
		svDesc = (ScrollView) findViewById(R.id.sv_desc);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		tvIndex = (TextView) findViewById(R.id.tv_index);
		
		vpImage = (ViewPager) findViewById(R.id.vpImage);
		vpImage.setOffscreenPageLimit(1);
		vpImage.setPageMargin(40);
		
		ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager());
		adapter.setListData(mImages);
		vpImage.setAdapter(adapter);
		vpImage.setCurrentItem(mIndex);
	}

	@Override
	public void onInitViewData() {
		if (mImages == null || mImages.size() == 0) {
			tvIndex.setVisibility(View.GONE);
			tvDesc.setVisibility(View.GONE);
		} else {
			tvIndex.setText("1/" + mImages.size());
			tvDesc.setText(mDesc);
			svDesc.setVisibility(!TextUtils.isEmpty(mDesc) ? View.VISIBLE : View.GONE); 
		}
	}

	@Override
	public void onBindListener() {
		vpImage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				tvIndex.setVisibility(View.VISIBLE);
				tvIndex.setText((position + 1) + "/" + mImages.size());
//					tvDesc.setText(((ImageVo) mImages.get(position)).getDescripe());
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	public int getCurrentPage() {
		return vpImage.getCurrentItem() + 1;
	}

	protected ArrayList<String> getImageList() {
		return mImages;
	}

	protected int getTotalPageSize() {
		return mImages != null ? mImages.size() : 0;
	}

	class ImagePagerAdapter extends FragmentStatePagerAdapter {
		
		private ArrayList<String> images;

		public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setListData(ArrayList<String> images) {
			this.images = images;
		}

		@Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance(images.get(position), position, getCount(), false);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageDetailFragment f = (ImageDetailFragment) super
                    .instantiateItem(container, position);
            return f;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

	}

	public void onImageClick(View v) {
		if (svDesc.getVisibility() == View.VISIBLE || rlTitleBar.getVisibility() == View.VISIBLE) {
			svDesc.setVisibility(View.GONE);
			rlTitleBar.setVisibility(View.GONE);
		} else {
			svDesc.setVisibility(View.VISIBLE);
			rlTitleBar.setVisibility(View.VISIBLE);
		}
	}

	public void onImageLongClick(View v) {
		boolean isCached = false;
		Integer position = (Integer) v.getTag();
		if (mImages != null && position < mImages.size()) {
			String image = mImages.get(position);
			isCached = AppController.getInstance().getImageLoader().isCached(image, 0, 0);
		}

		if (!isCached) {
			Toast.makeText(this, "保存图片未加载成功！", Toast.LENGTH_LONG).show();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
