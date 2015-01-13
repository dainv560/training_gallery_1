package com.framgia.gallery;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.framgia.gallery.ImageLoader.ImageLoadListener;

@SuppressWarnings("deprecation")
public class BottomImageAdapter extends BaseAdapter implements
		ImageLoadListener {

	private Context context;
	public Cursor imageCursor;
	private Bitmap bitmap = null;
	private ThreadPoolExecutor threadPool;
	private Handler handler = new Handler();
	private static int NUMBER_OF_CORES = 5;
	private static final int KEEP_ALIVE_TIME = 1;
	private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
	private final BlockingQueue<Runnable> mDecodeWorkQueue;
	private ImageLoader imageLoader;
	private Resources resources;

	public BottomImageAdapter(Context context, Cursor imageCursor,
			Resources resources) {
		this.context = context;
		this.imageCursor = imageCursor;

		mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();

		this.threadPool = new ThreadPoolExecutor(NUMBER_OF_CORES,
				NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,
				mDecodeWorkQueue);
		threadPool.allowsCoreThreadTimeOut();

		this.resources = resources;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageCursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewSwitcher lViewSwitcher;

		if (convertView == null) {
			lViewSwitcher = new ViewSwitcher(context);

			DisplayMetrics metrics = new DisplayMetrics();
			metrics = resources.getDisplayMetrics();

			int height_progress = metrics.heightPixels
					/ ImageViewerActivity.GALLERY_HEIGHT_RATIO / 2;
			int height_image = metrics.heightPixels
					/ ImageViewerActivity.GALLERY_HEIGHT_RATIO;

			ProgressBar lProgress = new ProgressBar(context);
			lProgress.setLayoutParams(new ViewSwitcher.LayoutParams(
					height_progress, height_progress));
			lViewSwitcher.addView(lProgress);
			OptimizerImageView lImage = new OptimizerImageView(context);
			lImage.setScaleType(ScaleType.FIT_CENTER);
			lImage.setLayoutParams(new Gallery.LayoutParams(height_image,
					height_image));

			lViewSwitcher.addView(lImage);

		} else {
			lViewSwitcher = (ViewSwitcher) convertView;
		}

		final OptimizerImageView lImageView = (OptimizerImageView) lViewSwitcher
				.getChildAt(1);
		lImageView.setScaleType(ScaleType.FIT_CENTER);
		lViewSwitcher.setDisplayedChild(0);
		try {
			imageLoader = new ImageLoader(context, BottomImageAdapter.this,
					position, imageCursor, lImageView, lViewSwitcher);

			threadPool.execute(imageLoader);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lViewSwitcher;
	}

	@Override
	public void handleImageLoaded(final ViewSwitcher aViewSwitcher,
			final ImageView aImageView, final Bitmap aBitmap) {
		handler.post(new Runnable() {
			public void run() {
				aImageView.setImageBitmap(aBitmap);
				aViewSwitcher.setDisplayedChild(1);
			}
		});
	}

}