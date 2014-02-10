package com.wise.data;

import android.R.string;

/**
 * 车辆信息(车牌，车标)
 * @author keven.cheng
 */
public class TimeData {
	public String Date;
	public String Year;
	public String Month;
    public String getDate() {
        return Date;
    }
    public void setDate(String date) {
        Date = date;
    }
    public String getYear() {
        return Year;
    }
    public void setYear(String year) {
        Year = year;
    }
    public String getMonth() {
        return Month;
    }
    public void setMonth(String month) {
        Month = month;
    }
    @Override
    public String toString() {
        return "TimeData [Date=" + Date + ", Year=" + Year + ", Month=" + Month
                + "]";
    }	
}