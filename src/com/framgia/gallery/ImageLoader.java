package com.framgia.gallery;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class ImageLoader implements Runnable {

	public interface ImageLoadListener {

		void handleImageLoaded(ViewSwitcher aViewSwitcher,
				ImageView aImageView, Bitmap aBitmap);
	}

	private ImageLoadListener mListener = null;
	private int pos;
	private ImageView aImageView;
	private ViewSwitcher aViewSwitcher;
	private Cursor imageCursor;
	private Context context;

	/**
	 * Image loader takes an object that extends ImageLoadListener
	 * 
	 * @param lListener
	 */
	ImageLoader(Context context, ImageLoadListener mListener, final int pos,
			Cursor imageCursor, final ImageView aImageView,
			final ViewSwitcher aViewSwitcher) {
		this.context = context;
		this.mListener = mListener;
		this.pos = pos;
		this.imageCursor = imageCursor;
		this.aImageView = aImageView;
		this.aViewSwitcher = aViewSwitcher;
	}

	@Override
	public void run() {

		try {

			synchronized (aImageView) {
				BitmapFactory.Options lOptions = new BitmapFactory.Options();
				lOptions.inSampleSize = 1;

				imageCursor.moveToPosition(pos);
				int id = imageCursor.getInt(imageCursor
						.getColumnIndex(MediaStore.Images.Media._ID));
				Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
						context.getContentResolver(), id,
						MediaStore.Images.Thumbnails.MICRO_KIND, null);

				if (mListener != null) {
					mListener.handleImageLoaded(aViewSwitcher, aImageView,
							bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
