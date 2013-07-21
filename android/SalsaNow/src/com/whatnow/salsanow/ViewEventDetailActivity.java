package com.whatnow.salsanow;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.whatnow.salsanow.map.data.EventMapObject;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.annotation.TargetApi;
import android.os.Build;

public class ViewEventDetailActivity extends SherlockActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_event_detail);
		// Show the Up button in the action bar.
		Bundle bundle = getIntent().getExtras();
		EventMapObject event = bundle.getParcelable(Constants.EVENT_INFO_PARCEL);
		
		TextView tvDescription = (TextView) findViewById(R.id.tv_eventDetailDescription);
		tvDescription.setText(event.getDescription());
		TextView tvName = (TextView) findViewById(R.id.tv_eventDetailName);
		tvName.setText(event.getName());
		
		ToggleButton tbAttending = (ToggleButton) findViewById(R.id.tb_evtDetail_attend);
		tbAttending.setChecked(event.isAttending());
		
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar()
	{
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.view_event_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
