package com.wise.data;

public class BrankModel {
	private String vehicleBrank = null;
	private String vehicleLetter = null;
	private String brankId = null;
	private String logoUrl = null;
	
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getBrankId() {
		return brankId;
	}
	public void setBrankId(String brankId) {
		this.brankId = brankId;
	}
	public String getVehicleBrank() {
		return vehicleBrank;
	}
	public void setVehicleBrank(String vehicleBrank) {
		this.vehicleBrank = vehicleBrank;
	}
	public String getVehicleLetter() {
		return vehicleLetter;
	}
	public void setVehicleLetter(String vehicleLetter) {
		this.vehicleLetter = vehicleLetter;
	}
    @Override
    public String toString() {
        return "BrankModel [vehicleBrank=" + vehicleBrank + ", vehicleLetter="
                + vehicleLetter + "]";
    }
	
}
