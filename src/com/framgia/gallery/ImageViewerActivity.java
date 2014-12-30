package com.framgia.gallery;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.framgia.scaling.ScalingUtilities;
import com.framgia.scaling.ScalingUtilities.ScalingLogic;

public class ImageViewerActivity extends FragmentActivity {

	public static final String ID = "id";
	public static final String PATH = "paths";
	public static final String LENGTH = "length";
	public static final String TYPE = "type";
	private int id;
	private String[] paths;
	private static ActionBar mActionBar;
	private Handler handler = new Handler();
	private int length;
	private Dialog dialogInfor;
	@SuppressWarnings("deprecation")
	private static Gallery gallery;
	private String typeOrder;
	private Cursor imageCursor;
	private Resources resources;
	public static int GALLERY_HEIGHT_RATIO = 6;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private static boolean showGallery = true;
	private int screenHeight = 0;
	private int screenWidth = 0;
	private static float xCor = 0;
	private static float yCor = 0;
	private Bitmap scaledBitmap = null;
	final ColorDrawable cd = new ColorDrawable(Color.rgb(68, 74, 83));

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.image_viewer_layout);

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenHeight = displayMetrics.heightPixels;
		screenWidth = displayMetrics.widthPixels;

		// Get extra data from previous activity
		Intent intent = getIntent();
		id = intent.getIntExtra(ID, 0);
		paths = intent.getStringArrayExtra(PATH);
		length = intent.getIntExtra(LENGTH, 0);
		typeOrder = intent.getStringExtra(TYPE);

		

		// setting actionBar
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setBackgroundDrawable(cd);
		cd.setAlpha(50);
		
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

		final String[] columns = new String[] { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media._ID };
		imageCursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, typeOrder);

		dialogInfor = new Dialog(ImageViewerActivity.this);
		dialogInfor.setContentView(R.layout.popup_info);
		dialogInfor.setTitle(R.string.popup_title);

		setBottomGallery();

		// setting viewPager
		setViewPager();

		// mActionBar.hide();
		// gallery.setVisibility(View.GONE);

		// gallery.setOnItemSelectedListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.image_viewer_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Setup the bottom Gallery in ImageViewer Activity
	 */
	public void setBottomGallery() {
		resources = getResources();
		gallery = (Gallery) findViewById(R.id.galleryView);
		DisplayMetrics metrics = new DisplayMetrics();
		metrics = resources.getDisplayMetrics();

		int height = metrics.heightPixels;
		gallery.getLayoutParams().height = (int) height / GALLERY_HEIGHT_RATIO;
		gallery.setAdapter(new BottomImageAdapter(ImageViewerActivity.this,
				imageCursor, resources));
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPager.setCurrentItem(position);
			}
		});
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent upIntent = new Intent(this, MainActivity.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.from(this).addNextIntent(upIntent)
						.startActivities();
				finish();
			} else {
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		case R.id.action_info:
			try {
				ExifInterface exif = new ExifInterface(paths[id]);
				setTextView((TextView) dialogInfor.findViewById(R.id.imgName),
						(new File(paths[id])).getName());
				setTextView((TextView) dialogInfor.findViewById(R.id.imgDate),
						exif.getAttribute(ExifInterface.TAG_DATETIME));
				setTextView((TextView) dialogInfor.findViewById(R.id.imgIso),
						exif.getAttribute(ExifInterface.TAG_ISO));
				setTextView((TextView) dialogInfor.findViewById(R.id.imgMaker),
						exif.getAttribute(ExifInterface.TAG_MAKE));
				setTextView((TextView) dialogInfor.findViewById(R.id.imgPath),
						paths[id]);
				setTextView(
						(TextView) dialogInfor.findViewById(R.id.imgResolution),
						exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
								+ " x "
								+ exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
				String str = " bytes";
				float size = (float) (new File(paths[id])).length();
				if (size > 1000) {
					size = size / 1024;
					str = " KB";
				}
				if (size > 1000) {
					size = size / 1024;
					str = " MB";
				}
				size = Math.round(size * 100) / 100f;
				setTextView((TextView) dialogInfor.findViewById(R.id.imgSize),
						size + str);
				setTextView((TextView) dialogInfor.findViewById(R.id.imgType),
						exif.getAttribute(ExifInterface.TAG_ORIENTATION));

			} catch (IOException e) {
				e.printStackTrace();
			}
			dialogInfor.show();
			return true;
		case R.id.action_share:
			showGallery = !showGallery;
			if (showGallery) {
				mActionBar.show();
				gallery.setVisibility(View.VISIBLE);
			} else {
				mActionBar.hide();
				gallery.setVisibility(View.GONE);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setTextView(TextView tv, String str) {
		if (str == null) {
			tv.setText("< null >");
		} else {
			tv.setText(str);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// -------------------
	// private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
	// private final int size;
	//
	// public ScreenSlidePagerAdapter(FragmentManager fm, int size) {
	// super(fm);
	// this.size = size;
	// }
	//
	// @Override
	// public int getCount() {
	// return size;
	// }
	//
	// @Override
	// public Fragment getItem(int position) {
	//
	// return ImageDetailFragment.newInstance(ImageViewerActivity.this,
	// paths[position], screenHeight, screenWidth);
	// }
	// }

	@SuppressLint("NewApi")
	public void showActionbar(boolean showGallery) {
		if (showGallery) {
			mActionBar.show();
			gallery.setVisibility(View.VISIBLE);
		} else {
			mActionBar.hide();
			gallery.setVisibility(View.GONE);
		}
	}

	public void setViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewPager);
		mPagerAdapter = new TouchImageAdapter(length);
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(id);
		mPager.setPageMargin((int) getResources().getDimension(
				R.dimen.horizontal_page_margin));
		mPager.setOffscreenPageLimit(2);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@SuppressLint("NewApi")
			@Override
			public void onPageSelected(int position) {
				invalidateOptionsMenu();
			}
		});

		// mPager.setOnTouchListener(new OnTouchListener() {
		//
		// @SuppressLint("ClickableViewAccessibility")
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		//
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// xCor = event.getX();
		// yCor = event.getY();
		// }
		//
		// if (event.getAction() == MotionEvent.ACTION_MOVE) {
		// return false;
		// }
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// if ((event.getX() - xCor) > screenWidth * 0.02
		// || (event.getY() - yCor) > screenHeight * 0.02) {
		// return false;
		// }
		// showGallery = !showGallery;
		// showActionbar(showGallery);
		// }
		//
		// return false;
		// }
		// });
	}

	private class TouchImageAdapter extends PagerAdapter {
		private final int size;

		public TouchImageAdapter(int size) {
			this.size = size;
		}

		@Override
		public int getCount() {
			return size;
		}

		@Override
		public View instantiateItem(ViewGroup container, final int position) {
			final TouchImageView img = new TouchImageView(
					container.getContext());

			new Thread(new Runnable() {

				@Override
				public void run() {
					Bitmap unscaledBitmap = ScalingUtilities.decodeResource(
							getResources(), paths[position], screenWidth,
							screenWidth, ScalingLogic.FIT);

					scaledBitmap = ScalingUtilities.createScaledBitmap(
							unscaledBitmap, screenWidth, screenHeight,
							ScalingLogic.FIT);
					unscaledBitmap.recycle();
					// update ImageView
					ImageViewerActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							img.setImageBitmap(scaledBitmap);
						}
					});
				}
			}).start();

			container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			return img;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

}
