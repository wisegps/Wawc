package com.wise.data;
/**
 * 收藏地点
 * @author honesty
 */
public class AdressData {
	private int _id;
	private String name;
	private String adress;
	private String phone;
	private int distance = -1;
	private double Lat;
	private double Lon;
	private boolean is_collect;
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
	
    public boolean isIs_collect() {
        return is_collect;
    }
    public void setIs_collect(boolean is_collect) {
        this.is_collect = is_collect;
    }
    @Override
    public String toString() {
        return "AdressData [_id=" + _id + ", name=" + name + ", adress="
                + adress + ", phone=" + phone + ", distance=" + distance
                + ", Lat=" + Lat + ", Lon=" + Lon + "]";
    }	
}