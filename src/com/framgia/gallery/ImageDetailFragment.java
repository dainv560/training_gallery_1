/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.framgia.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.scaling.ScalingUtilities;
import com.framgia.scaling.ScalingUtilities.ScalingLogic;
import com.framgia.ui.Utils;

/**
 * This fragment will populate the children of the ViewPager from
 * {@link ImageDetailActivity}.
 */
public class ImageDetailFragment extends Fragment implements OnTouchListener {
	private static final String IMAGE_DATA_EXTRA = "extra_image_data";
	private String mImagePath;
	private ImageView mImageView;
	public static boolean showGallery = false;
	private static int imgWidth;
	private static int imgHeight;
	private Bitmap scaledBitmap = null;
	private static Activity activity;

	public static ImageDetailFragment newInstance(Activity act,
			String imagePath, int height, int width) {

		activity = act;
		imgHeight = height;
		imgWidth = width;

		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, imagePath);
		f.setArguments(args);

		return f;
	}

	public ImageDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImagePath = getArguments() != null ? getArguments().getString(
				IMAGE_DATA_EXTRA) : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate and locate the main ImageView
		final View v = inflater.inflate(R.layout.image_detail_fragment,
				container, false);
		mImageView = (ImageView) v.findViewById(R.id.imageView);
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		if (ImageViewerActivity.class.isInstance(getActivity())) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					Bitmap unscaledBitmap = ScalingUtilities.decodeResource(
							getResources(), mImagePath, imgWidth, imgHeight,
							ScalingLogic.FIT);

					scaledBitmap = ScalingUtilities.createScaledBitmap(
							unscaledBitmap, imgWidth, imgHeight,
							ScalingLogic.FIT);
					unscaledBitmap.recycle();
					//update ImageView
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mImageView.setImageBitmap(scaledBitmap);
						}
					});
				}
			}).start();

		}

		// Pass clicks on the ImageView to the parent activity to handle
		if (OnClickListener.class.isInstance(getActivity())
				&& Utils.hasHoneycomb()) {
			mImageView.setOnClickListener((OnClickListener) getActivity());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mImageView != null) {
			mImageView.setImageDrawable(null);
		}
	}

	@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		// showGallery = !showGallery;
		// Log.e("fragment", "ok" + showGallery);
		// ((ImageViewerActivity) getActivity()).showActionbar(showGallery);
		return false;
	}

}
