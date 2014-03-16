package com.renjie.adapter;

import java.util.List;

public class YearMoney implements IMoneyData{
	private String year;
	private double money;
	private String isClosed;
	private String level;
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
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

	public List<MonthMoney> getList() {
		return list;
	}

	public void setList(List<MonthMoney> list) {
		this.list = list;
	}

	private List<MonthMoney> list;

	@Override
	public String getTime() {
		// TODO Auto-generated method stub
		return year;
	}
	@Override
	public String getMoneyType() {
		// TODO Auto-generated method stub
		return null;
	}
}
