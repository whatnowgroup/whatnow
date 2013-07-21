package com.whatnow.salsanow.map.data;

import android.os.Parcel;
import android.os.Parcelable;

public class EventMapObject implements Parcelable
{
	private String name;
	private double latitude;
	private double longitude;
	private String description;
	private String shortDescription;
	
	private boolean attending;
	private String address;
	private double rating;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getShortDescription()
	{
		return shortDescription;
	}
	public void setShortDescription(String shortDescription)
	{
		this.shortDescription = shortDescription;
	}
	
	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	public EventMapObject()
	{
		
	}

	public EventMapObject(Parcel in)
	{
		// TODO Auto-generated constructor stub
		name = in.readString();
		description = in.readString();
		shortDescription = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
		rating = in.readDouble();
		address = in.readString();
		boolean[] temp = new boolean[1];
		in.readBooleanArray(temp);
		attending = temp[0];
		
	}
	
	@Override
	public void writeToParcel(Parcel in, int flags)
	{
		in.writeString(name);
		in.writeString(description);
		in.writeString(shortDescription);
		in.writeDouble(latitude);
		in.writeDouble(longitude);
		
		in.writeDouble(rating);
		in.writeString(address);
		in.writeBooleanArray(new boolean[] { attending });
		
	}

    public boolean isAttending()
	{
		return attending;
	}
	public void setAttending(boolean attending)
	{
		this.attending = attending;
	}

	public String getAddress()
	{
		return address;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}

	public double getRating()
	{
		return rating;
	}
	public void setRating(double rating)
	{
		this.rating = rating;
	}

	public static Parcelable.Creator<EventMapObject> CREATOR = new Parcelable.Creator<EventMapObject>()
	{
		@Override
		public EventMapObject createFromParcel(Parcel in)
		{
			return new EventMapObject(in);
		}

		@Override
		public EventMapObject[] newArray(int size)
		{
			return new EventMapObject[size];
		}

	};
	
	
}
