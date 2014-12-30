package com.framgia.gallery;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends ActionBarActivity {

	private GridView gridViewImage;
	private ImageAdapter adapter;
	private ArrayList<Bitmap> listImages = new ArrayList<Bitmap>();
	private int count;
	private String[] arrPath;
	private ActionBar mActionBar;
	private String orderBy;
	private Cursor imageCursor;
	private static final String TYPE_NAME = "name";
	private static final String TYPE_DATE = "date";
	private static final String TYPE_SIZE = "size";
	private static String current_order = TYPE_NAME;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler.post(new Runnable() {

			@Override
			public void run() {
				setGridViewOrder(current_order);
				adapter.updateNotifyDatasetChanged(imageCursor);
			}
		});

		adapter = new ImageAdapter(this, imageCursor);

		// setup Gridview
		setGridView();

		// setup ActionBar
		setActionBar();

	}

	@SuppressWarnings("deprecation")
	public void setGridViewOrder(String order) {
		final String[] columns;
		// int image_column_index;

		listImages.clear();

		switch (order) {
		case TYPE_NAME:
			columns = new String[] { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			orderBy = MediaStore.Images.Media.TITLE;
			imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			break;
		case TYPE_DATE:
			columns = new String[] { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			orderBy = MediaStore.Images.Media.DATE_MODIFIED;
			imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			break;
		case TYPE_SIZE:
			columns = new String[] { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			orderBy = MediaStore.Images.Media.SIZE;
			imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);

			break;
		default:
			columns = new String[] { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			orderBy = MediaStore.Images.Media.TITLE;
			imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			break;
		}
		// image_column_index = imageCursor
		// .getColumnIndex(MediaStore.Images.Media._ID);

		this.count = imageCursor.getCount();
		this.arrPath = new String[this.count];
		for (int i = 0; i < this.count; i++) {
			imageCursor.moveToPosition(i);
			// int id = imageCursor.getInt(image_column_index);
			int dataColumnIndex = imageCursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			// listImages.add(MediaStore.Images.Thumbnails.getThumbnail(
			// getApplicationContext().getContentResolver(), id,
			// MediaStore.Images.Thumbnails.MICRO_KIND, null));
			arrPath[i] = imageCursor.getString(dataColumnIndex);
		}

		// adapter.updateNotifyDatasetChanged(imageCursor);
	}

	public void setGridView() {

		// set custom adapter for gridview
		gridViewImage = (GridView) findViewById(R.id.gridViewImage);
		gridViewImage.setPadding(3, 3, 3, 3);
		gridViewImage.setAdapter(adapter);
		// imagecursor.close();

		// Implement on Item click listener for gridview
		gridViewImage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),
						ImageViewerActivity.class);
				intent.putExtra("id", position);
				intent.putExtra("paths", arrPath);
				intent.putExtra("length", count);
				switch (current_order) {
				case TYPE_NAME:
					intent.putExtra("type", MediaStore.Images.Media.TITLE);
					break;
				case TYPE_DATE:
					intent.putExtra("type", MediaStore.Images.Media.DATE_MODIFIED);
					break;
				case TYPE_SIZE:
					intent.putExtra("type", MediaStore.Images.Media.SIZE);
					break;
				}

				startActivityForResult(intent, 100);
				imageCursor.close();
				listImages.clear();
				finish();
			}
		});
	}

	public void setActionBar() {
		// create ActionBar for activity
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected boolean onPrepareOptionsPanel(View view, Menu menu) {
		MenuItem item;
		switch (current_order) {
		case TYPE_NAME:
			item = menu.findItem(R.id.order_name);
			break;
		case TYPE_DATE:
			item = menu.findItem(R.id.order_date);
			break;
		case TYPE_SIZE:
			item = menu.findItem(R.id.order_size);
			break;
		default:
			item = menu.findItem(R.id.order_name);
			break;
		}
		item.setChecked(true);
		return super.onPrepareOptionsPanel(view, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.order_date:
			item.setChecked(true);
			setGridViewOrder(TYPE_DATE);
			current_order = TYPE_DATE;
			adapter.updateNotifyDatasetChanged(imageCursor);
			return true;
		case R.id.order_name:
			item.setChecked(true);
			setGridViewOrder(TYPE_NAME);
			current_order = TYPE_NAME;
			adapter.updateNotifyDatasetChanged(imageCursor);
			return true;
		case R.id.order_size:
			setGridViewOrder(TYPE_SIZE);
			current_order = TYPE_SIZE;
			adapter.updateNotifyDatasetChanged(imageCursor);
			item.setChecked(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public Cursor getImageCursor() {
		return imageCursor;
	}

}
