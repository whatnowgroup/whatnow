package com.whatnow.salsanow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whatnow.salsanow.map.data.EventMapObject;

import android.annotation.SuppressLint;
import android.app.DownloadManager.Request;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends FragmentActivity {

	public static final String WS_URL = "http://ec2-54-218-30-210.us-west-2.compute.amazonaws.com:9000/events"; 
	private ArrayList<EventMapObject> eventObjectList = new ArrayList<EventMapObject>(); 
	private GoogleMap mMap;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();		
		
		initMap();
		bindListeners();
		
		Session.openActiveSession(this, true, new Session.StatusCallback() {

		    // callback when session changes state
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		    	if (exception != null)
		    	{
		    		Log.d("onCreate", exception.getMessage(), exception);
		    	}
		    	if (session.isOpened())
		    	{
		    		loadMarkers();
		    	}
		    }
		  });
	}
	
	private void bindListeners() {
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}

	private void loadMarkers()
	{
		try {
			JSONArray mapObjectList = new JSONArray(readJsonData());
			for (int i = 0; i < mapObjectList.length(); i++)
			{
				eventObjectList.add(readEventMap(mapObjectList.getJSONObject(i)));
			}
				
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("loadMarkers", "JSON parse exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("loadMarkers", "Cannot load markers", e);
			e.printStackTrace();
		}

		plotMarkers();
	}
	
	private void initMap() {
		CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(-33.86, 151.20), 15);
		mMap.moveCamera(camera);
	}

	private void plotMarkers() {
		
		for (EventMapObject event : eventObjectList)
		{
			MarkerOptions options = new MarkerOptions();
			
			options.position(new LatLng(event.getLatitude(), event.getLongitude()));
			options.title(event.getName());
			mMap.addMarker(options);
		}
	}

	private EventMapObject readEventMap(JSONObject object) throws JSONException
	{
		EventMapObject value = new EventMapObject();
		value.setName(object.getString("eventName"));
		
		value.setLongitude(Double.parseDouble(object.getString("longtitude")));
		value.setLatitude(Double.parseDouble(object.getString("latitude")));
		
		return value;
	}
	
	private String readJsonData() throws IOException
	{
		URL jsonUrl = new URL(WS_URL);
		URLConnection connection = jsonUrl.openConnection();
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		StringBuilder sb = new StringBuilder();
		String line = streamReader.readLine();
		while (line != null)
		{
			sb.append(line);
			line = streamReader.readLine();
		}
		
		return sb.toString();
				
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
