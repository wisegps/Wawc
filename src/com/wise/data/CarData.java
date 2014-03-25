package com.wise.data;

import android.R.string;

/**
 * 车辆信息(车牌，车标)
 * @author keven.cheng
 */
public class CarData {	
    public int obj_id;
    /**
     * 品牌
     */
    public String car_brand;
    private String car_brand_id;
    /**
     * 车型
     */
    public String car_series;
    private String car_series_id;
    public String car_type;
    private String car_type_id;
    public String engine_no;
    public String frame_no;
    private String vio_city_name;  //违章城市名
    private String gas_no;
    private String insurance_tel;
    private String maintain_tel;
    /**
     * 保险公司
     */
    public String insurance_company;
    /**
     * 保险时间
     */
    public String insurance_date;
    /**
     * 年检日期
     */
    public String annual_inspect_date;
    /**
     * 保养
     */
    public String maintain_company;
    public String maintain_last_mileage;
    public String maintain_next_mileage;
    public String buy_date;    
    
	public String obj_name;
	private String maintain_last_date;
	private String regNo;
	private String vio_location = "";   //违章城市代码
	private String logoPath;
	private String device_id;
	private String serial;
	public boolean isCheck;  //是否选中
	public int Type;		//布局控制
	public String Adress;  //车辆位置
	public String gps_time; //定位时间
	public String Lat; 
	public String Lon;
	public String getCar_brand_id() {
		return car_brand_id;
	}
	public void setCar_brand_id(String car_brand_id) {
		this.car_brand_id = car_brand_id;
	}
	public String getCar_series_id() {
		return car_series_id;
	}
	public void setCar_series_id(String car_series_id) {
		this.car_series_id = car_series_id;
	}
	public String getCar_type_id() {
		return car_type_id;
	}
	public void setCar_type_id(String car_type_id) {
		this.car_type_id = car_type_id;
	}
	public String getVio_city_name() {
		return vio_city_name;
	}
	public void setVio_city_name(String vio_city_name) {
		this.vio_city_name = vio_city_name;
	}
	public String getGas_no() {
		return gas_no;
	}
	public void setGas_no(String gas_no) {
		this.gas_no = gas_no;
	}
	public String getInsurance_tel() {
		return insurance_tel;
	}
	public void setInsurance_tel(String insurance_tel) {
		this.insurance_tel = insurance_tel;
	}
	public String getMaintain_tel() {
		return maintain_tel;
	}
	public void setMaintain_tel(String maintain_tel) {
		this.maintain_tel = maintain_tel;
	}
	public int getType() {
        return Type;
    }
    public void setType(int type) {
        Type = type;
    }
    public String getLogoPath() {
		return logoPath;
	}
	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}
	public int getObj_id() {
        return obj_id;
    }
    public void setObj_id(int obj_id) {
        this.obj_id = obj_id;
    }    
    public String getObj_name() {
        return obj_name;
    }
    public void setObj_name(String obj_name) {
        this.obj_name = obj_name;
    }
    public String getCar_brand() {
        return car_brand;
    }
    public void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
    }
    public String getCar_series() {
        return car_series;
    }
    public void setCar_series(String car_series) {
        this.car_series = car_series;
    }
    public String getCar_type() {
        return car_type;
    }
    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }
    public String getEngine_no() {
        return engine_no;
    }
    public void setEngine_no(String engine_no) {
        this.engine_no = engine_no;
    }
    public String getFrame_no() {
        return frame_no;
    }
    public void setFrame_no(String frame_no) {
        this.frame_no = frame_no;
    }
    public String getInsurance_company() {
        return insurance_company;
    }
    public void setInsurance_company(String insurance_company) {
        this.insurance_company = insurance_company;
    }
    public String getInsurance_date() {
        return insurance_date;
    }
    public void setInsurance_date(String insurance_date) {
        this.insurance_date = insurance_date;
    }
    public String getAnnual_inspect_date() {
        return annual_inspect_date;
    }
    public void setAnnual_inspect_date(String annual_inspect_date) {
        this.annual_inspect_date = annual_inspect_date;
    }
    public String getMaintain_company() {
        return maintain_company;
    }
    public void setMaintain_company(String maintain_company) {
        this.maintain_company = maintain_company;
    }
    public String getMaintain_last_mileage() {
        return maintain_last_mileage;
    }
    public void setMaintain_last_mileage(String maintain_last_mileage) {
        this.maintain_last_mileage = maintain_last_mileage;
    }
    public String getMaintain_next_mileage() {
        return maintain_next_mileage;
    }
    public void setMaintain_next_mileage(String maintain_next_mileage) {
        this.maintain_next_mileage = maintain_next_mileage;
    }
    public String getBuy_date() {
        return buy_date;
    }
    public void setBuy_date(String buy_date) {
        this.buy_date = buy_date;
    }	
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	
	
    public String getMaintain_last_date() {
		return maintain_last_date;
	}
	public void setMaintain_last_date(String maintain_last_date) {
		this.maintain_last_date = maintain_last_date;
	}	
    public String getAdress() {
        return Adress;
    }
    public void setAdress(String adress) {
        Adress = adress;
    }    
    public String getLat() {
        return Lat;
    }
    public void setLat(String lat) {
        Lat = lat;
    }
    public String getLon() {
        return Lon;
    }
    public void setLon(String lon) {
        Lon = lon;
    }
    public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getVio_location() {
		return vio_location;
	}
	public void setVio_location(String vio_location) {
		this.vio_location = vio_location;
	}	
	public String getDevice_id() {
        return device_id;
    }
    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
    public String getSerial() {
        return serial;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }    
    public String getGps_time() {
        return gps_time;
    }
    public void setGps_time(String gps_time) {
        this.gps_time = gps_time;
    }
    @Override
    public String toString() {
        return "CarData [obj_id=" + obj_id + ", car_brand=" + car_brand
                + ", car_brand_id=" + car_brand_id + ", car_series="
                + car_series + ", car_series_id=" + car_series_id
                + ", car_type=" + car_type + ", car_type_id=" + car_type_id
                + ", engine_no=" + engine_no + ", frame_no=" + frame_no
                + ", vio_city_name=" + vio_city_name + ", gas_no=" + gas_no
                + ", insurance_tel=" + insurance_tel + ", maintain_tel="
                + maintain_tel + ", insurance_company=" + insurance_company
                + ", insurance_date=" + insurance_date
                + ", annual_inspect_date=" + annual_inspect_date
                + ", maintain_company=" + maintain_company
                + ", maintain_last_mileage=" + maintain_last_mileage
                + ", maintain_next_mileage=" + maintain_next_mileage
                + ", buy_date=" + buy_date + ", obj_name=" + obj_name
                + ", maintain_last_date=" + maintain_last_date + ", regNo="
                + regNo + ", vio_location=" + vio_location + ", logoPath="
                + logoPath + ", device_id=" + device_id + ", serial=" + serial
                + ", isCheck=" + isCheck + ", Type=" + Type + ", Adress="
                + Adress + ", gps_time=" + gps_time + ", Lat=" + Lat + ", Lon="
                + Lon + "]";
    }        
}