package com.kuxhausen.huemore;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.kuxhausen.huemore.persistence.DatabaseDefinitions;
import com.kuxhausen.huemore.persistence.DatabaseDefinitions.InternalArguments;
import com.kuxhausen.huemore.persistence.DatabaseDefinitions.NetBulbColumns;

public class BulbListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final int BULBS_LOADER = 0;
	private static final String[] columns = { NetBulbColumns.NAME_COLUMN, NetBulbColumns.DEVICE_ID_COLUMN, BaseColumns._ID };

	public CursorAdapter dataSource;
	
	public TextView selected, longSelected; // updated on long click
	private int selectedPos = -1;
	private NetworkManagedSherlockFragmentActivity parrentActivity;

	ArrayList<String> bulbNameList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// We need to use a different list item layout for devices older than
		// Honeycomb
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
				: android.R.layout.simple_list_item_1;

		getLoaderManager().initLoader(BULBS_LOADER, null, this);

		dataSource = new SimpleCursorAdapter(this.getActivity(), layout, null,
				columns, new int[] { android.R.id.text1 }, 0);

		setListAdapter(dataSource);
		
		// Inflate the layout for this fragment
		View myView = inflater.inflate(R.layout.bulb_view, container, false);
		
		return myView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parrentActivity = (NetworkManagedSherlockFragmentActivity) activity;
	}

	@Override
	public void onStart() {
		super.onStart();
		getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
	}

	public void invalidateSelection() {
		// Set the previous selected item as checked to be unhighlighted when in
		// two-pane layout
		if (selected != null && selectedPos > -1)
			getListView().setItemChecked(selectedPos, false);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		longSelected = (TextView) ((AdapterView.AdapterContextMenuInfo) menuInfo).targetView;

		android.view.MenuInflater inflater = this.getActivity()
				.getMenuInflater();
		inflater.inflate(R.menu.context_bulb, menu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		if (longSelected == null)
			return false;

		switch (item.getItemId()) {

		case R.id.contextgroupmenu_rename: // <-- your custom menu item id here
			AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

			
			String[] selectionArgs = {""+info.id};
			Cursor c = parrentActivity.getContentResolver().query(NetBulbColumns.URI, columns, NetBulbColumns._ID + " = ?", selectionArgs, null);
			String deviceId = c.getString(c.getColumnIndex(NetBulbColumns.DEVICE_ID_COLUMN));
			
			
			EditBulbDialogFragment ngdf = new EditBulbDialogFragment();
			Bundle args = new Bundle();
			args.putString(InternalArguments.NET_BULB_DEVICE_ID, deviceId);
			ngdf.setArguments(args);
			ngdf.show(getFragmentManager(),
					InternalArguments.FRAG_MANAGER_DIALOG_TAG);

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		selected = ((TextView) (v));
		selectedPos = position;

		// Notify the parent activity of selected item
		int[] iPos = { position + 1 };
		parrentActivity.setGroup(iPos, selected.getText().toString());
		
		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(selectedPos, true);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) {
		/*
		 * Takes action based on the ID of the Loader that's being created
		 */
		switch (loaderID) {
		case BULBS_LOADER:
			// Returns a new CursorLoader
			return new CursorLoader(getActivity(), // Parent activity context
					DatabaseDefinitions.NetBulbColumns.URI, // Table
					columns, // Projection to return
					null, // No selection clause
					null, // No selection arguments
					null // Default sort order
			);
		default:
			// An invalid id was passed in
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		/*
		 * Moves the query results into the adapter, causing the ListView
		 * fronting this adapter to re-display
		 */
		dataSource.changeCursor(cursor);
		registerForContextMenu(getListView());
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		/*
		 * Clears out the adapter's reference to the Cursor. This prevents
		 * memory leaks.
		 */
		// unregisterForContextMenu(getListView());
		dataSource.changeCursor(null);
	}
}
