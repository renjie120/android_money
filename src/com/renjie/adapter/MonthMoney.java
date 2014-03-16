package com.renjie.adapter;

import java.util.List;

public class MonthMoney implements IMoneyData{
	private String month;
	private double money;
	private String isClosed;
	private String level;
	private List<DayMoney> list;
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public String getIsClosed() {
		return isClosed;
	}
	public void setIsClosed(String isClosed) {
		this.isClosed = isClosed;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public List<DayMoney> getList() {
		return list;
	}
	public void setList(List<DayMoney> list) {
		this.list = list;
	}
	@Override
	public String getTime() {
		// TODO Auto-generated method stub
		return month;
	}
	@Override
	public String getMoneyType() {
		// TODO Auto-generated method stub
		return null;
	}
}
