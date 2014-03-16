package com.renjie;

import com.renjie.tool.Tool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * 登录系统主界面.
 * 
 * @author lsq
 * 
 */
public class Login extends BaseActivity {
	private Button loginBtn;
	private Button quitBtn;
	private EditText usernameText;
	private CheckBox showPass;
	private EditText passwordText;
	private static boolean debug = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		usernameText = (EditText) findViewById(R.id.usertext);
		passwordText = (EditText) findViewById(R.id.passtext);
		loginBtn = (Button) findViewById(R.id.loginbtn);
		showPass = (CheckBox) findViewById(R.id.showpass);
		quitBtn = (Button) findViewById(R.id.quitbtn);
		// 设置密码默认为不可见
		passwordText.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		// 调用绑定事件的私有方法。
		prepareListener();

//		if (debug)
//			alert("测试状态！");
	}

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

		quitBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				leaveApp();
			}
		});

		loginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(Tool.CONFIG,
						0);
				String us = usernameText.getText().toString();
				String pas = passwordText.getText().toString();
				if (settings.getString(Tool.USERNAME, "user").equals(us)) {
					if (debug || settings.getString(Tool.PASS, "1").equals(pas)) {
						Intent openUrl = new Intent();
						openUrl.setClass(Login.this, NewHomePage.class);
						startActivity(openUrl);
						Login.this.finish();
					} else {
						alert(getString(R.string.error_pass));
					}
				} else {
					alert(getString(R.string.error_user));
				}
			}
		});
	}
}