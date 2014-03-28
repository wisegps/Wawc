package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.DevicesData;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class Fragment_devices extends Fragment{
    private static final int Get_data = 1;

    LinearLayout ll_content;
    GridView gv_activity_devices;
    TextView tv_activity_devices_serial, tv_activity_devices_sim,
            tv_activity_devices_status, tv_activity_devices_service_end_date,
            tv_activity_devices_hardware_version,
            tv_activity_devices_software_version;
    List<DevicesData> devicesDatas = new ArrayList<DevicesData>();
    DevicesAdapter devicesAdapter;
    boolean isJump = true;// false绑定终端，true加载
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ll_content = (LinearLayout) getActivity().findViewById(R.id.ll_content);
        ImageView iv_activity_devices_menu = (ImageView) getActivity().findViewById(R.id.iv_activity_devices_menu);
        iv_activity_devices_menu.setOnClickListener(onClickListener);
        TextView tv_activity_devices_renewals = (TextView) getActivity().findViewById(R.id.tv_activity_devices_renewals);
        tv_activity_devices_renewals.setOnClickListener(onClickListener);
        gv_activity_devices = (GridView) getActivity().findViewById(R.id.gv_activity_devices);
        tv_activity_devices_serial = (TextView) getActivity().findViewById(R.id.tv_activity_devices_serial);
        tv_activity_devices_sim = (TextView) getActivity().findViewById(R.id.tv_activity_devices_sim);
        tv_activity_devices_status = (TextView) getActivity().findViewById(R.id.tv_activity_devices_status);
        tv_activity_devices_service_end_date = (TextView) getActivity().findViewById(R.id.tv_activity_devices_service_end_date);
        tv_activity_devices_hardware_version = (TextView) getActivity().findViewById(R.id.tv_activity_devices_hardware_version);
        tv_activity_devices_software_version = (TextView) getActivity().findViewById(R.id.tv_activity_devices_software_version);

        Intent intent = getActivity().getIntent();
        isJump = intent.getBooleanExtra("isJump", true);
        System.out.println("isJump = " + isJump);
        if (!isJump) {// 绑定终端
            System.out.println("隐藏");
            ll_content.setVisibility(View.GONE);
            iv_activity_devices_menu.setImageResource(R.drawable.nav_back);
        } else {
            ll_content.setVisibility(View.VISIBLE);
            iv_activity_devices_menu.setImageResource(R.drawable.side_left);
        }
        GetDevicesDB();
        GetDevicesData();
        registerBroadcastReceiver();
    }
    
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_devices_menu:
                if (!isJump) {
                    getActivity().finish();
                } else {
                    ActivityFactory.A.LeftMenu();
                }
                break;
            case R.id.tv_activity_devices_renewals:
                getActivity().startActivity(new Intent(getActivity(), OrderServiceActivity.class));
                break;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_data:
                jsonDevice(msg.obj.toString());
                showGridView();
                JudgeDevice(msg.obj.toString());
                break;

            default:
                break;
            }
        }
    };

    private void showGridView() {
        if (devicesDatas.size() > 0) {
            showDevice(devicesDatas.get(0));
        }
        if (isJump) {
            System.out.println("加载后面文件");
            DevicesData devicesData = new DevicesData();
            devicesData.setSerial("");
            devicesData.setCheck(false);
            devicesData.setType(1);
            devicesDatas.add(devicesData);
        }

        devicesAdapter = new DevicesAdapter();
        gv_activity_devices.setAdapter(devicesAdapter);
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                120, getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams(((devicesDatas.size()
                * (px + 10) + 10) + 10), LayoutParams.WRAP_CONTENT);

        gv_activity_devices.setLayoutParams(params);
        gv_activity_devices.setColumnWidth(px);
        gv_activity_devices.setHorizontalSpacing(10);
        gv_activity_devices.setStretchMode(GridView.NO_STRETCH);
        gv_activity_devices.setNumColumns(devicesDatas.size());
        gv_activity_devices.setOnItemClickListener(onItemClickListener);
    }

    int index = 0;

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            if (!isJump) {// 绑定终端
                index = arg2;
                backRequest(arg2);
            } else {
                if (arg2 == (devicesDatas.size() - 1)) {
                    showDialog();
                } else {
                    for (int i = 0; i < devicesDatas.size(); i++) {
                        devicesDatas.get(i).setCheck(false);
                    }
                    showDevice(devicesDatas.get(arg2));
                    devicesDatas.get(arg2).setCheck(true);
                    devicesAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void showDevice(DevicesData devicesData) {
        tv_activity_devices_serial.setText(devicesData.getSerial());
        tv_activity_devices_sim.setText(devicesData.getSim());
        tv_activity_devices_status.setText(devicesData.getStatus());
        tv_activity_devices_service_end_date.setText(devicesData
                .getService_end_date().substring(0, 10));
        tv_activity_devices_hardware_version.setText(devicesData
                .getHardware_version());
        tv_activity_devices_software_version.setText(devicesData
                .getSoftware_version());

    }

    boolean isHaveDevices = false;

    private void GetDevicesDB() {
        if(Variable.devicesDatas.size() == 0){
            isHaveDevices = false;
            ll_content.setVisibility(View.GONE);
        }else{
            isHaveDevices = true;
            ll_content.setVisibility(View.VISIBLE);
            devicesDatas.addAll(Variable.devicesDatas);
            showGridView();
        }
    }

    public void GetDevicesData() {
        // TODO 我的终端
        String url = Constant.BaseUrl + "customer/" + Variable.cust_id
                + "/device?auth_code=" + Variable.auth_code;
        new Thread(new NetThread.GetDataThread(handler, url, Get_data)).start();
    }

    /**
     * 判断更新还是插入
     * 
     * @param result
     */
    private void JudgeDevice(String result) {
        if (isHaveDevices) {// 更新
            UpdateDevice(result, "Devices");
        } else {// 插入
            InsertDevice(result, "Devices");
        }
    }

    private void UpdateDevice(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Content", result);
        dbExcute.UpdateDB(getActivity(), values, Title);
    }

    private void InsertDevice(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Cust_id", Variable.cust_id);
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(getActivity(), values, Constant.TB_Base);
    }

    /**
     * 解析终端数据
     * 
     * @param result
     */
    private void jsonDevice(String result) {
        try {
            devicesDatas.clear();
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DevicesData devicesData = new DevicesData();
                devicesData.setDevice_id(jsonObject.getString("device_id"));
                devicesData.setHardware_version(jsonObject
                        .getString("hardware_version"));
                devicesData.setSerial(jsonObject.getString("serial"));
                devicesData.setService_end_date(jsonObject
                        .getString("service_end_date"));
                devicesData.setSim(jsonObject.getString("sim"));
                devicesData.setSoftware_version(jsonObject
                        .getString("software_version"));
                devicesData.setStatus(jsonObject.getString("status"));
                devicesData.setType(0);
                devicesDatas.add(devicesData);
            }
            Variable.devicesDatas.clear();
            Variable.devicesDatas.addAll(devicesDatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class DevicesAdapter extends BaseAdapter {
        private static final int VALUE_CAR = 0;
        private static final int VALUE_ADD = 1;
        LayoutInflater mInflater = LayoutInflater.from(getActivity());

        @Override
        public int getCount() {
            return devicesDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return devicesDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            ViewHolder holder = null;
            ViewAdd viewAdd = null;
            if (convertView == null) {
                switch (type) {
                case VALUE_CAR:
                    convertView = mInflater.inflate(R.layout.item_cars, null);
                    holder = new ViewHolder();
                    holder.tv_item_carnumber = (TextView) convertView
                            .findViewById(R.id.tv_item_carnumber);
                    holder.iv_item_cars = (ImageView) convertView
                            .findViewById(R.id.iv_item_cars);
                    holder.ll_item_cars = (LinearLayout) convertView
                            .findViewById(R.id.ll_item_cars);
                    convertView.setTag(holder);
                    break;
                case VALUE_ADD:
                    convertView = mInflater.inflate(R.layout.item_add, null);
                    viewAdd = new ViewAdd();
                    convertView.setTag(viewAdd);
                    break;
                }
            } else {
                switch (type) {
                case VALUE_CAR:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case VALUE_ADD:
                    viewAdd = (ViewAdd) convertView.getTag();
                    break;
                }
            }
            switch (type) {
            case VALUE_CAR:
                DevicesData devicesData = devicesDatas.get(position);
                holder.tv_item_carnumber.setText(devicesData.getSerial());
                holder.iv_item_cars.setImageResource(R.drawable.icon_terminal);
                if (devicesData.isCheck()) {
                    holder.ll_item_cars
                            .setBackgroundResource(R.drawable.bg_car_logo);
                } else {
                    holder.ll_item_cars.setBackgroundResource(R.color.white);
                }
                break;
            case VALUE_ADD:

                break;
            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return devicesDatas.get(position).getType();
        }

        private class ViewHolder {
            TextView tv_item_carnumber;
            ImageView iv_item_cars;
            LinearLayout ll_item_cars;
        }

        private class ViewAdd {
        }
    }

    private void backRequest(int index) {
        Intent intent = new Intent();
        intent.putExtra("DeviceId", devicesDatas.get(index).getDevice_id());
        intent.putExtra("Serial", devicesDatas.get(index).getSerial());
        getActivity().setResult(7, intent);
        getActivity().finish();
    }

    private void showDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("列表框")
                .setItems(new String[] { "添加终端", "订购终端" },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                if (which == 0) {
                                    getActivity()
                                            .startActivity(new Intent(
                                                    getActivity(),
                                                    DevicesAddActivity.class));
                                } else {
                                    getActivity().startActivity(new Intent(
                                            getActivity(),
                                                    OrderDeviceActivity.class));
                                }
                            }
                        }).setNegativeButton("确定", null).show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.A_UpdateDevice);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constant.A_UpdateDevice)){
                GetDevicesData();
            }
        }
    };
}