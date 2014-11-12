package com.framgia.gallery;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

import com.framgia.gallery.SimpleGestureFilter.SimpleGestureListener;

public class ImageViewerActivity extends Activity implements
		SimpleGestureListener {

	private static final int PROGRESSBARINDEX = 0;
	private static final int IMAGEVIEWINDEX = 1;
	private ImageSwitcher imageSwitcher;
	private SimpleGestureFilter detector;
	private Animation inFade;
	private Animation outFade;
	private Animation inLeft, outLeft, inRight, outRight;
	private int id;
	private String[] paths;
	private ActionBar mActionBar;
	private ViewSwitcher viewSwitcher;
	private Handler handler = new Handler();
	private int length;
	private Dialog dialogInfor;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_viewer_layout);

		// Get extra data from previous activity
		Intent intent = getIntent();
		id = intent.getIntExtra("id", 0);
		paths = intent.getStringArrayExtra("paths");
		length = intent.getIntExtra("length", 0);
		detector = new SimpleGestureFilter(this, this);
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		dialogInfor = new Dialog(ImageViewerActivity.this);
		dialogInfor.setContentView(R.layout.popup_info);
		dialogInfor.setTitle(R.string.popup_title);

		inFade = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		outFade = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		inLeft = AnimationUtils.loadAnimation(this, R.anim.left_in);
		outLeft = AnimationUtils.loadAnimation(this, R.anim.left_out);
		inRight = AnimationUtils.loadAnimation(this, R.anim.right_in);
		outRight = AnimationUtils.loadAnimation(this, R.anim.right_out);

		imageSwitcher = new ImageSwitcher(getApplicationContext());

		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);

		imageSwitcher.setFactory(new ViewFactory() {

			@SuppressWarnings("deprecation")
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				return imageView;
			}
		});

		setImageView(id);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.image_viewer_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

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

	@Override
	public void onSwipe(int direction) {

		switch (direction) {

		case SimpleGestureFilter.SWIPE_RIGHT:
			if (--id < 0) {
				id = length - 1;
			}
			imageSwitcher.setInAnimation(inLeft);
			imageSwitcher.setOutAnimation(outRight);
			break;
		case SimpleGestureFilter.SWIPE_LEFT:
			if (++id > length - 1) {
				id = 0;
			}
			imageSwitcher.setInAnimation(inRight);
			imageSwitcher.setOutAnimation(outLeft);
			break;
		case SimpleGestureFilter.SWIPE_DOWN:
			break;
		case SimpleGestureFilter.SWIPE_UP:
			break;

		}
		setImageView(id);
	}

	private void setImageView(final int id) {
		// if (imageSwitcher)
		viewSwitcher = new ViewSwitcher(getApplicationContext());

		ProgressBar lProgress = new ProgressBar(getApplicationContext());
		lProgress.setLayoutParams(new ViewSwitcher.LayoutParams(80, 80));
		viewSwitcher.addView(lProgress);
		viewSwitcher.setDisplayedChild(PROGRESSBARINDEX);

		new Thread(new Runnable() {

			@Override
			public void run() {
				final Uri uri = Uri.fromFile(new File(paths[id]));
				handler.post(new Runnable() {

					@Override
					public void run() {

						// imageSwitcher.getDrawingCache().recycle();
						imageSwitcher.setImageURI(uri);
					}
				});
			}
		}).start();

	}

	@Override
	public void onDoubleTap() {
		// TODO Auto-generated method stub

	}

}
