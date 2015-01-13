package com.framgia.gallery;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.framgia.gallery.ImageLoader.ImageLoadListener;

public class ImageAdapter extends BaseAdapter implements ImageLoadListener {

	private static final int PROGRESSBARINDEX = 0;
	private static final int IMAGEVIEWINDEX = 1;
	private ImageLoader imageLoader;
	private Context context;
	private Cursor imageCursor;
	private int count;
	private int image_column_index;
	private Handler handler = new Handler();
	private ThreadPoolExecutor threadPool;
	private static int NUMBER_OF_CORES = 5;
	private static final int KEEP_ALIVE_TIME = 1;
	private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
	private final BlockingQueue<Runnable> mDecodeWorkQueue;

	public ImageAdapter(Context context, Cursor imageCursor) {
		this.context = context;
		this.imageCursor = imageCursor;
		if (imageCursor != null) {
			this.image_column_index = imageCursor
					.getColumnIndex(MediaStore.Images.Media._ID);

			this.count = imageCursor.getCount();
		}

		mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();

		this.threadPool = new ThreadPoolExecutor(NUMBER_OF_CORES,
				NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,
				mDecodeWorkQueue);
		threadPool.allowsCoreThreadTimeOut();
	}

	public Bitmap getItem(int pos) {
		imageCursor.moveToPosition(pos);
		int id = imageCursor.getInt(image_column_index);
		return MediaStore.Images.Thumbnails.getThumbnail(
				context.getContentResolver(), id,
				MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, null);
	}

	public int getCount() {
		return this.count;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewSwitcher lViewSwitcher;

		if (convertView == null) {
			lViewSwitcher = new ViewSwitcher(context);

			ProgressBar lProgress = new ProgressBar(context);
			lProgress.setLayoutParams(new ViewSwitcher.LayoutParams(80, 80));
			lViewSwitcher.addView(lProgress);
			OptimizerImageView lImage = new OptimizerImageView(context);
			//lImage.setScaleType(ScaleType.FIT_XY);

			lViewSwitcher.addView(lImage);

		} else {
			lViewSwitcher = (ViewSwitcher) convertView;
		}

		final ImageView lImageView = (ImageView) lViewSwitcher.getChildAt(1);
		lViewSwitcher.setDisplayedChild(PROGRESSBARINDEX);
		try {
			imageLoader = new ImageLoader(context, ImageAdapter.this, position,
					imageCursor, lImageView, lViewSwitcher);

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
				aViewSwitcher.setDisplayedChild(IMAGEVIEWINDEX);
			}
		});
	}

	@Override
	protected synchronized void finalize() throws Throwable {
		super.finalize();

		Runnable[] runnableArray = new Runnable[mDecodeWorkQueue.size()];
		mDecodeWorkQueue.toArray(runnableArray);
		int len = runnableArray.length;

		synchronized (imageLoader) {
			for (int runnableIndex = 0; runnableIndex < len; runnableIndex++) {
				Thread thread = new Thread(runnableArray[runnableIndex]);
				if (null != thread) {
					thread.interrupt();
				}
			}
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateNotifyDatasetChanged(Cursor newImageCursor) {
		if (this.imageCursor == null) {
			this.image_column_index = newImageCursor
					.getColumnIndex(MediaStore.Images.Media._ID);

			this.count = newImageCursor.getCount();
		}
		this.imageCursor = newImageCursor;
		notifyDataSetChanged();
	}

}
