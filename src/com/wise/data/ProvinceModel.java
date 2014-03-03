package com.wise.data;

import java.util.List;

public class ProvinceModel {
	private String provinceName = null;
	private String provinceLetter = null;
	private List<IllegalCity> IllegalCityList = null;
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getProvinceLetter() {
		return provinceLetter;
	}
	public void setProvinceLetter(String provinceLetter) {
		this.provinceLetter = provinceLetter;
	}
	public List<IllegalCity> getIllegalCityList() {
		return IllegalCityList;
	}
	public void setIllegalCityList(List<IllegalCity> illegalCityList) {
		IllegalCityList = illegalCityList;
	}
	@Override
	public String toString() {
		return "ProvinceModel [provinceName=" + provinceName
				+ ", provinceLetter=" + provinceLetter + "]";
	}
}
