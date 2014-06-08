package com.renjie.tool;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "reminder")
public class Reminder extends EntityBase {
	// 有效
	public static final String VALID = "1";
	// 无效
	public static final String INVALID = "2";
	// 提醒过
	public static final String REMINDED = "3";
	@Column(column = "user")
	private String user;// 用户
	@Column(column = "reminderType")
	private int reminderType;// 提醒类型
	@Column(column = "hour")
	private String hour;// 提醒小时
	@Column(column = "year")
	private String year;// 提醒年份
	@Column(column = "month")
	private String month;// 提醒月份
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(column = "day")
	private String day;// 提醒天数
	@Column(column = "weekDay")
	private String weekDay;// 提醒星期
	@Column(column = "minute")
	private String minute;// 提醒分钟
	@Column(column = "flg")
	private String flg;// 提醒状态
	@Column(column = "content")
	private String content;// 提醒状态
	@Column(column = "lastRemindedTime")
	private String lastRemindedTime;// 上次提醒时间

	public String getLastRemindedTime() {
		return lastRemindedTime;
	}

	public void setLastRemindedTime(String lastRemindedTime) {
		this.lastRemindedTime = lastRemindedTime;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public int getReminderType() {
		return reminderType;
	}

	public void setReminderType(int reminderType) {
		this.reminderType = reminderType;
	}
 
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
