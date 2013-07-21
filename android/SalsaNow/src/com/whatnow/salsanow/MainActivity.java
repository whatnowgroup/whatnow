package com.whatnow.salsanow;

import java.util.HashMap;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whatnow.salsanow.dao.EventDao;
import com.whatnow.salsanow.map.data.EventMapObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

public class MainActivity extends SherlockFragmentActivity
{
	private List<EventMapObject> eventObjectList;
	private GoogleMap mMap;

	private EventDao eventDao = new EventDao();
	private HashMap<Marker, EventMapObject> markerEventMap = new HashMap<Marker, EventMapObject>();

	private class EventListAsync extends
			AsyncTask<Void, Void, List<EventMapObject>>
	{

		@Override
		protected List<EventMapObject> doInBackground(Void... arg0)
		{
			// TODO Auto-generated method stub
			return eventDao.getMapEventData();
		}

		@Override
		protected void onPostExecute(List<EventMapObject> result)
		{
			// TODO Auto-generated method stub
			plotMarkers(result);
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));

		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		initMap();
		bindListeners();
			
		Session.openActiveSession(this, true, new Session.StatusCallback()
		{

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state,
					Exception exception)
			{
				if (exception != null)
				{
					Log.d("onCreate", exception.getMessage(), exception);
				}
				if (session.isOpened())
				{
				}
			}
		});

		EventListAsync eventListAsync = new EventListAsync();
		eventListAsync.execute();

	}

	private void bindListeners()
	{
//		mMap.setInfoWindowAdapter(new InfoWindowAdapter()
//		{
//
//			@Override
//			public View getInfoWindow(Marker marker)
//			{
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public View getInfoContents(Marker marker)
//			{
//				// TODO Auto-generated method stub
//				return null;
//			}
//		});

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
		{
			
			@Override
			public void onInfoWindowClick(Marker marker)
			{
				Intent viewDetailIntent = new Intent(MainActivity.this, ViewEventDetailActivity.class);
				viewDetailIntent.putExtra(Constants.EVENT_INFO_PARCEL, getEventFromMarker(marker));
				startActivity(viewDetailIntent);				
//				initPopup();
				
			}

		});
	}

	private void initPopup()
	{
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View popup_layout = inflater.inflate(R.layout.event_info_view,
//				(ViewGroup) findViewById(R.id.event_popup_element));
//		final PopupWindow pwnd = new PopupWindow(popup_layout, 300, 400, true);
//		pwnd.showAtLocation(popup_layout, Gravity.CENTER, 0, 0);
//		Button closeButton = (Button) popup_layout
//				.findViewById(R.id.close_button);
//		closeButton.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View arg0)
//			{
//				// TODO Auto-generated method stub
//
//				pwnd.dismiss();
//			}
//		});
		// pwnd.showAsDropDown(findViewById(R.id.map), 0, 0);
	}

	private void initMap()
	{
		CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(
				-33.8666, 151.207264), 15);
		mMap.moveCamera(camera);
	}

	private EventMapObject getEventFromMarker(Marker marker)
	{
		return markerEventMap.get(marker);
	}

	private void plotMarkers(List<EventMapObject> objectList)
	{
		if (objectList == null)
			return;

		for (EventMapObject event : objectList)
		{
			MarkerOptions options = new MarkerOptions();

			options.position(new LatLng(event.getLatitude(), event
					.getLongitude()));
			options.title(event.getName());
			Marker marker = mMap.addMarker(options);
			markerEventMap.put(marker, event);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	public boolean onCreateOptionsMenu1(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
