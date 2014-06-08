package com.renjie;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.renjie.tool.Tool;

/**
 * 保存理财信息的主界面.
 * 
 * @author lsq
 * 
 */
public class ReminderInfo extends BaseActivity {
	@ViewInject(R.id.tip_content)
	private TextView tip_content;
	@ViewInject(R.id.tip_type)
	private TextView tip_type;
	@ViewInject(R.id.tip_time)
	private TextView tip_time;
	@ViewInject(R.id.tip_date)
	private TextView tip_date;
	@ViewInject(R.id.deletebtn)
	private Button deletebtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉title标题栏
		setContentView(R.layout.reminder_index);
		ViewUtils.inject(this); // 进行注入
		
		Bundle d = getIntent().getExtras();
		tip_content.setText(d.getString("tipContent"));
		tip_type.setText(d.getString("tipContent"));
		tip_date.setText(Tool.TIP_TYPES[Integer.parseInt(d.getString("tip_tp"))]);
		tip_time.setText(d.getString("time"));
	}

	@Override
	void prepareListener() {

	}
}
