package com.framgia.gallery;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

public class ImageAdapter extends ArrayAdapter<Bitmap> {

	private LayoutInflater mInflator;
	//private Context context;

	protected List<Bitmap> checkedList = new ArrayList<Bitmap>();

	public ImageAdapter(Context context, ArrayList<Bitmap> listImages) {
		super(context, R.layout.item_image, listImages);
		//this.context = context;

		mInflator = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	private static class ViewHolder {
		public ImageView imgViewItem;
		public CheckBox checkBox;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder view;

		if (convertView == null) {
			view = new ViewHolder();
			convertView = mInflator.inflate(R.layout.item_image, parent, false);

			view.imgViewItem = (ImageView) convertView
					.findViewById(R.id.imageView);
			view.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
//			int w = context.getResources().getDisplayMetrics().widthPixels;
//			
//			convertView.setLayoutParams(new GridView.LayoutParams(w/3-3, w/3-3));

			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}

		if (getItem(position) != null) {
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inPurgeable = true;

			Bitmap bitmap = getItem(position);
			view.imgViewItem.setImageBitmap(bitmap);
		} else {
			view.imgViewItem.setImageResource(R.drawable.ic_launcher);
		}

		view.checkBox.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				if (!cb.isChecked()) {
					checkedList.remove(getItem(position));
				} else {
					checkedList.add(getItem(position));
				}
			}
		});

		view.checkBox.setChecked(checkedList.contains(getItem(position)));
		view.checkBox.setVisibility(View.INVISIBLE);

		// notifyDataSetChanged();
		return convertView;
	}

}
