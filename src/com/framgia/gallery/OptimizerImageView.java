package com.framgia.gallery;


import com.framgia.ui.RecyclingBitmapDrawable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class OptimizerImageView extends ImageView {

	public OptimizerImageView(Context context) {
		super(context);
	}

	public OptimizerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OptimizerImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDetachedFromWindow() {
		// This has been detached from Window, so clear the drawable
		setImageDrawable(null);

		super.onDetachedFromWindow();
	}
	
	 @Override
	    public void setImageDrawable(Drawable drawable) {
	        // Keep hold of previous Drawable
	        final Drawable previousDrawable = getDrawable();

	        // Call super to set new Drawable
	        super.setImageDrawable(drawable);

	        // Notify new Drawable that it is being displayed
	        notifyDrawable(drawable, true);

	        // Notify old Drawable so it is no longer being displayed
	        notifyDrawable(previousDrawable, false);
	    }
	 
	 private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
	        if (drawable instanceof RecyclingBitmapDrawable) {
	            // The drawable is a CountingBitmapDrawable, so notify it
	            ((RecyclingBitmapDrawable) drawable).setIsDisplayed(isDisplayed);
	        } else if (drawable instanceof LayerDrawable) {
	            // The drawable is a LayerDrawable, so recurse on each layer
	            LayerDrawable layerDrawable = (LayerDrawable) drawable;
	            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
	                notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
	            }
	        }
	    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}

}
