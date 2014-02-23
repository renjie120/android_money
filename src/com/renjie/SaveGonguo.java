package com.renjie;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.renjie.tool.MoneyDAO;

/**
 * 保存功过信息的界面.
 * 
 * @author lsq
 * 
 */
public class SaveGonguo extends BaseActivity {
	private EditText getMoneyText;
	private EditText getMoneyDescText;
	private Spinner sortSpinner;
	private Button dateBtn;
	private Button saveBtn;
	private int myyear;
	private int mymonth;
	private int myday;
	Intent intent;
	static final int DATE_DIALOG_ID = 0;
	private MoneyDAO myDb;
	private TableLayout table;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.money_index);
//		dateBtn = (Button) findViewById(R.id.chooseTime_btn);
//		saveBtn = (Button) findViewById(R.id.savebtn);
//		table = (TableLayout) findViewById(R.id.gongguo_table);
//		addRow();
//		// 实例化数据库
//		myDb = new MoneyDAO(this, 1);
//		// 调用绑定事件的私有方法。
//		prepareListener();
	}
 

	@Override
	void prepareListener() {
		// TODO Auto-generated method stub

	}
 
	
}
