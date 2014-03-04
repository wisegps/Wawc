package com.wise.data;
/**
 * 我的终端
 * @author honesty
 */

public class DevicesData{
    String Device_id;
    String Hardware_version;
    String Serial;
    String Service_end_date;
    String Sim;
    String Software_version;
    String Status;
    boolean isCheck;
    int Type;
    
    public int getType() {
        return Type;
    }
    public void setType(int type) {
        Type = type;
    }
    public String getDevice_id() {
        return Device_id;
    }
    public void setDevice_id(String device_id) {
        Device_id = device_id;
    }
    public String getHardware_version() {
        return Hardware_version;
    }
    public void setHardware_version(String hardware_version) {
        Hardware_version = hardware_version;
    }
    public String getSerial() {
        return Serial;
    }
    public void setSerial(String serial) {
        Serial = serial;
    }
    public String getService_end_date() {
        return Service_end_date;
    }
    public void setService_end_date(String service_end_date) {
        Service_end_date = service_end_date;
    }
    public String getSim() {
        return Sim;
    }
    public void setSim(String sim) {
        Sim = sim;
    }
    public String getSoftware_version() {
        return Software_version;
    }
    public void setSoftware_version(String software_version) {
        Software_version = software_version;
    }
    public String getStatus() {
        return Status;
    }
    public void setStatus(String status) {
        Status = status;
    }        
    public boolean isCheck() {
        return isCheck;
    }
    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
    @Override
    public String toString() {
        return "devicesData [Device_id=" + Device_id
                + ", Hardware_version=" + Hardware_version + ", Serial="
                + Serial + ", Service_end_date=" + Service_end_date
                + ", Sim=" + Sim + ", Software_version=" + Software_version
                + ", Status=" + Status + "]";
    }       
}