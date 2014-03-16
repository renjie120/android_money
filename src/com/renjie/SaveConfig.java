package com.renjie;

import com.renjie.tool.Tool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 保存设置信息
 * 
 * @author lsq
 * 
 */
public class SaveConfig extends BaseActivity {
	private EditText usernameText;
	private EditText passwordText;
	private EditText remoteIpText;
	private EditText remotePortText;
	private CheckBox showPass;
	private Button returnBtn;
	private Button saveBtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myconfig);
		usernameText = (EditText) findViewById(R.id.untext);
		// 得到远程服务器的ip
		remoteIpText = (EditText) findViewById(R.id.remoteIp);
		remotePortText = (EditText) findViewById(R.id.remotePort);
		passwordText = (EditText) findViewById(R.id.passtext);
		passwordText.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		returnBtn = (Button) findViewById(R.id.returnbtn);
		showPass = (CheckBox) findViewById(R.id.showpass);

		// 将保存的ip地址默认显示出来!
		SharedPreferences settings = getSharedPreferences(Tool.CONFIG, 0);
		remoteIpText.setText(settings.getString(Tool.REMOTEIP,
				getText(R.string.config_inremoteIp).toString()));
		remotePortText.setText(settings.getString(Tool.PORT,
				getText(R.string.config_inremotePort).toString()));
		// 调用绑定事件的私有方法。
		prepareListener();
	}

	public void goSaveMoney() {
		Intent openUrl = new Intent();
		openUrl.setClass(SaveConfig.this, SaveMoney.class);
		startActivity(openUrl);
		SaveConfig.this.finish();
	}

	// 调用绑定事件的私有方法。
	protected void prepareListener() {
		showPass.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (showPass.isChecked()) {
					// 设置密码框可见
					passwordText
							.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
				} else {
					// 设置密码不可见
					passwordText
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
				}
			}

		});

		returnBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				setContentView(R.layout.money_index);
			}
		});

		// 保存配置。
		saveBtn = (Button) findViewById(R.id.savebtn);
		saveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String userStr = usernameText.getText().toString();
				String passStr = passwordText.getText().toString();
				String remoteIp = remoteIpText.getText().toString();
				String remotePort = remotePortText.getText().toString();
				SharedPreferences settings = getSharedPreferences(Tool.CONFIG,
						0);
				// 保存用户名
				if (userStr != null && !"".equals(userStr)) {
					settings.edit().putString(Tool.USERNAME, userStr).commit();
				}
				// 保存密码
				if (passStr != null && !"".equals(passStr)) {
					settings.edit().putString(Tool.PASS, passStr).commit();
				}
				// 保存远程ip
				if (remoteIp != null && !"".equals(remoteIp)) {
					settings.edit().putString(Tool.REMOTEIP, remoteIp).commit();
				}
				// 保存远程端口
				if (remotePort != null && !"".equals(remotePort)) {
					settings.edit().putString(Tool.PORT, remotePort).commit();
				}
				// 提示保存成功
				Toast.makeText(SaveConfig.this, getText(R.string.config_ok),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
