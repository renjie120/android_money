package com.renjie;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.renjie.tool.MoneyDAO;

/**
 * 普通的主界面.测试打电话和打开网页的intent程序.
 * 
 * @author lsq
 * 
 */
public class NewHomePage extends TabActivity implements OnCheckedChangeListener { 
	private MoneyDAO moneyDb;
	AlertDialog menuDialog;// menu菜单Dialog 
 
	public static final String TAB_ITEM_1 = "firstpage";
	public static final String TAB_ITEM_2 = "diary";
	public static final String TAB_ITEM_3 = "report";
	public static final String TAB_ITEM_4 = "config";
	public static final String TAB_ITEM_5 = "more";
	private RadioGroup group;
	private TabHost tabHost; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.homepage2);
 
		moneyDb = new MoneyDAO(this, 1);

		group = (RadioGroup) findViewById(R.id.main_radio);
		group.setOnCheckedChangeListener(this);

		tabHost = this.getTabHost();
		TabSpec tab1 = tabHost.newTabSpec(TAB_ITEM_1);
		TabSpec tab2 = tabHost.newTabSpec(TAB_ITEM_2);
		TabSpec tab3 = tabHost.newTabSpec(TAB_ITEM_3);
		// TabSpec tab4 = tabHost.newTabSpec(TAB_ITEM_4);
		// TabSpec tab5 = tabHost.newTabSpec(TAB_ITEM_5);
		tab1.setIndicator(TAB_ITEM_1).setContent(
				new Intent(NewHomePage.this, SaveMoney.class));
		tab2.setIndicator(TAB_ITEM_3).setContent(
				new Intent(NewHomePage.this, MoneyList.class));
		tab3.setIndicator(TAB_ITEM_2).setContent(
				new Intent(NewHomePage.this, SaveConfig.class));
		// tab4.setIndicator(TAB_ITEM_4).setContent(
		// new Intent(NewHomePage.this, SaveConfig.class));
		// tab5.setIndicator(TAB_ITEM_5).setContent(
		// new Intent(NewHomePage.this, SaveMoney.class));
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		tabHost.addTab(tab3);
		// tabHost.addTab(tab4);
		// tabHost.addTab(tab5);
	}

	public String getToday() {
		final Calendar today = Calendar.getInstance();
		int myyear = today.get(Calendar.YEAR);
		int mymonth = today.get(Calendar.MONTH) + 1;
		int myday = today.get(Calendar.DAY_OF_MONTH);
		return new StringBuilder().append(myyear).append("-").append(mymonth)
				.append("-").append(myday).toString();
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_button1:
			tabHost.setCurrentTabByTag(TAB_ITEM_1);
			break;
		case R.id.radio_button2:
			tabHost.setCurrentTabByTag(TAB_ITEM_2);
			break;
		case R.id.radio_button3:
			tabHost.setCurrentTabByTag(TAB_ITEM_3);
			break;
		default:
			break;
		}
	}
}