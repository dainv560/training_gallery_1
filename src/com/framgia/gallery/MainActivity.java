package com.framgia.gallery;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		adapter = new ImageAdapter(this, listImages);

		// final String[] columns = { MediaStore.Images.Media.DATA,
		// MediaStore.Images.Media.TITLE };
		// orderBy = MediaStore.Images.Media.TITLE;
		// imageCursor = managedQuery(
		// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
		// null, orderBy);
		// int image_column_index = imageCursor
		// .getColumnIndex(MediaStore.Images.Media._ID);
		//
		// this.count = imageCursor.getCount();
		// this.arrPath = new String[this.count];
		// for (int i = 0; i < this.count; i++) {
		// imageCursor.moveToPosition(i);
		// int id = imageCursor.getInt(image_column_index);
		// int dataColumnIndex = imageCursor
		// .getColumnIndex(MediaStore.Images.Media.DATA);
		// listImages.add(MediaStore.Images.Thumbnails.getThumbnail(
		// getApplicationContext().getContentResolver(), id,
		// MediaStore.Images.Thumbnails.MICRO_KIND, null));
		// arrPath[i] = imageCursor.getString(dataColumnIndex);
		// }

		setGridViewOrder(TYPE_NAME);
		// setup Gridview
		setGridView();

		// setup ActionBar
		setActionBar();

	}

	@SuppressWarnings("deprecation")
	public void setGridViewOrder(String order) {
		final String[] columns;
		int image_column_index;

		listImages.clear();

		switch (order) {
		case TYPE_NAME:
			columns = new String[] { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			orderBy = MediaStore.Images.Media.TITLE;
			imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			image_column_index = imageCursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			break;
		case TYPE_DATE:
			columns = new String[] { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			orderBy = MediaStore.Images.Media.DATE_MODIFIED;
			imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			image_column_index = imageCursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			break;
		case TYPE_SIZE:
			columns = new String[] { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			orderBy = MediaStore.Images.Media.SIZE;
			imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			image_column_index = imageCursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			break;
		default:
			columns = new String[] { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			orderBy = MediaStore.Images.Media.TITLE;
			imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			image_column_index = imageCursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			break;
		}

		this.count = imageCursor.getCount();
		this.arrPath = new String[this.count];
		for (int i = 0; i < this.count; i++) {
			imageCursor.moveToPosition(i);
			int id = imageCursor.getInt(image_column_index);
			int dataColumnIndex = imageCursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			listImages.add(MediaStore.Images.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), id,
					MediaStore.Images.Thumbnails.MICRO_KIND, null));
			arrPath[i] = imageCursor.getString(dataColumnIndex);
		}
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
						ImageViewer.class);
				intent.putExtra("id", position);
				intent.putExtra("path", arrPath[position]);
				setResult(100, intent);

				finish();

			}
		});
	}

	public void setActionBar() {
		// create ActionBar for activity
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		// mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// LayoutInflater mInflater = LayoutInflater.from(this);

		// SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(
		// mActionBar.getThemedContext(), R.array.order_list,
		// android.R.layout.simple_spinner_dropdown_item);
		//
		// ActionBar.OnNavigationListener navigationListener = new
		// OnNavigationListener() {
		//
		// @Override
		// public boolean onNavigationItemSelected(int itemPosition,
		// long itemId) {
		// Toast.makeText(getBaseContext(),
		// "You selected : " + order_list[itemPosition],
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }
		// };
		//
		// mActionBar.setListNavigationCallbacks(mSpinnerAdapter,
		// navigationListener);
		// View mCustomView = mInflater.inflate(R.layout.custom_actionbar,
		// null);
		// TextView mTitleTextView = (TextView) mCustomView
		// .findViewById(R.id.title_text);
		// mTitleTextView.setText("Gallery");
		//
		// ImageButton imageButton = (ImageButton) mCustomView
		// .findViewById(R.id.imageButton);
		// imageButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View view) {
		// Toast.makeText(getApplicationContext(), "Refresh Clicked!",
		// Toast.LENGTH_LONG).show();
		// }
		// });

		// mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			return true;
		case R.id.order_date:
			item.setChecked(true);
			setGridViewOrder(TYPE_DATE);
			adapter.notifyDataSetChanged();
			return true;
		case R.id.order_name:
			item.setChecked(true);
			setGridViewOrder(TYPE_NAME);
			adapter.notifyDataSetChanged();
			return true;
		case R.id.order_size:
			setGridViewOrder(TYPE_SIZE);
			adapter.notifyDataSetChanged();
			item.setChecked(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
