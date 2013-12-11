package com.wise.wawc;

import java.util.ArrayList;
import com.wise.data.EnergyItem;
import com.wise.extend.EnergyCurveView;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class VehicleStatusActivity extends Activity{
	private EnergyCurveView erenergyCurve;
    private DisplayMetrics dm = new DisplayMetrics();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle_status);
		erenergyCurve = (EnergyCurveView) findViewById(R.id.erenergycurve);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        erenergyCurve.setWindowsWH(dm);
        ArrayList<EnergyItem> energys = new ArrayList<EnergyItem>();
        energys.add(new EnergyItem("1", 9.0f, "无"));
        energys.add(new EnergyItem("2", 8.0f, "无"));
        energys.add(new EnergyItem("3", 7.0f, "无"));
        energys.add(new EnergyItem("4", 6.5f, "无"));
        energys.add(new EnergyItem("5", 7.8f, "无"));
        energys.add(new EnergyItem("6", 8.0f, "无"));
        energys.add(new EnergyItem("7", 7.0f, "无"));
        erenergyCurve.setData(energys);
        erenergyCurve.initPoints(energys);
	}
}