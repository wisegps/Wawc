package com.wise.data;

public class IllegalCity {
	private String cityName;  //违章城市
	private String cityCode;   //违章城市代码
	private String abbr;     //城市简称
	private String engine;   //是否需要发动机号
	private String engineno;   //需要发动机号多少位
	private String classa;    
	private String vehiclenum;  //是否需要车架号
	private String vehiclenumno;  //需要车架号多少位
	private String regist;   //是否需要登记证号
	private String registno;  //需要登记证号多少位
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getAbbr() {
		return abbr;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	public String getEngine() {
		return engine;
	}
	public void setEngine(String engine) {
		this.engine = engine;
	}
	public String getEngineno() {
		return engineno;
	}
	public void setEngineno(String engineno) {
		this.engineno = engineno;
	}
	public String getClassa() {
		return classa;
	}
	public void setClassa(String classa) {
		this.classa = classa;
	}
	public String getRegist() {
		return regist;
	}
	public void setRegist(String regist) {
		this.regist = regist;
	}
	public String getRegistno() {
		return registno;
	}
	public void setRegistno(String registno) {
		this.registno = registno;
	}
	public String getVehiclenum() {
		return vehiclenum;
	}
	public void setVehiclenum(String vehiclenum) {
		this.vehiclenum = vehiclenum;
	}
	public String getVehiclenumno() {
		return vehiclenumno;
	}
	public void setVehiclenumno(String vehiclenumno) {
		this.vehiclenumno = vehiclenumno;
	}
}
