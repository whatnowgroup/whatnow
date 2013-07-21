package com.whatnow.salsanow.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.whatnow.salsanow.map.data.EventMapObject;

public class EventDao {
//	public static final String WS_URL = "http://192.168.43.82:9000";
	
//	public static final String WS_URL = "http://123.243.70.36:9000";
	public static final String WS_URL = "http://ec2-54-252-187-159.ap-southeast-2.compute.amazonaws.com:9000";
//	public static final String LOCATION_ENDPOINT = "locations/";
	public static final String LOCATION_ENDPOINT = "/events";

	public List<EventMapObject> getMapEventData() {
		URL jsonUrl;
		try {
			jsonUrl = new URL(WS_URL + LOCATION_ENDPOINT);
			URLConnection connection = jsonUrl.openConnection();
			BufferedReader streamReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			StringBuilder sb = new StringBuilder();
			String line = streamReader.readLine();
			while (line != null) {
				sb.append(line);
				line = streamReader.readLine();
			}

			ArrayList<EventMapObject> eventObjectList = new ArrayList<EventMapObject>();

			JSONArray mapObjectList = new JSONArray(sb.toString());
			for (int i = 0; i < mapObjectList.length(); i++) {
				eventObjectList
						.add(readEventMap(mapObjectList.getJSONObject(i)));
			}

			return eventObjectList;

		} catch (JSONException e) {
			Log.d("loadMarkers", "JSON parse exception", e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("loadMarkers", "Cannot load markers", e);
			e.printStackTrace();
		}
		
		return null;

	}
	
	private EventMapObject readEventMap(JSONObject object) throws JSONException {
		EventMapObject value = new EventMapObject();
		if (object.has("name"))
			value.setName(object.getString("name"));
		else
			value.setName(object.getString("event_name"));

		value.setLongitude(Double.parseDouble(object.getString("longitude")));
		value.setLatitude(Double.parseDouble(object.getString("latitude")));
		value.setDescription(object.getString("description"));
		value.setShortDescription(object.getString("shortDescription"));
		value.setAddress(object.getString("address"));
		value.setAttending(object.getBoolean("attending"));
		value.setRating(object.getDouble("rating"));
		return value;
	}

}
