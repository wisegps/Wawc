package com.wise.data;

public class AdressData {
	private int _id;
	private String name;
	private String adress;
	private String phone;
	private int distance = -1;
	private double Lat;
	private double Lon;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public double getLat() {
		return Lat;
	}
	public void setLat(double lat) {
		Lat = lat;
	}
	public double getLon() {
		return Lon;
	}
	public void setLon(double lon) {
		Lon = lon;
	}	
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	@Override
	public String toString() {
		return "AdressData [name=" + name + ", adress=" + adress + ", phone="
				+ phone + ", Lat=" + Lat + ", Lon=" + Lon + "]";
	}	
}