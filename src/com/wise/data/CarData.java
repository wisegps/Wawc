package com.wise.data;

/**
 * 车辆信息(车牌，车标)
 * @author keven.cheng
 */
public class CarData {
	public String CarNumber;
	public int CarLogo;
	public boolean isCheck;
	public String getCarNumber() {
		return CarNumber;
	}
	public void setCarNumber(String carNumber) {
		CarNumber = carNumber;
	}
	public int getCarLogo() {
		return CarLogo;
	}
	public void setCarLogo(int carLogo) {
		CarLogo = carLogo;
	}	
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	@Override
	public String toString() {
		return "CarData [CarNumber=" + CarNumber + ", CarLogo=" + CarLogo + "]";
	}	
}