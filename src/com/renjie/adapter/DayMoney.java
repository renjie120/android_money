package com.renjie.adapter;

public class DayMoney implements IMoneyData {
	private String day;
	private double money;
	private String isClosed;
	private String moneyType;
	private String level;

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
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

	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	@Override
	public String getTime() {
		return day;
	}

	@Override
	public String getMoneyType() {
		// TODO Auto-generated method stub
		return moneyType;
	}
}
