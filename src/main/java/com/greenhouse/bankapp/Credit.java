package com.greenhouse.bankapp;

public class Credit{

	private double amount;
	private int monthsLeft;
	private double installment;
	private int userAccountID;

	public Credit(double amount, int userAccountID) {
		this.amount = amount;
		this.monthsLeft = 12;
		this.installment = this.amount/monthsLeft;
		this.userAccountID = userAccountID;
	}

	public Credit(double amount, int monthsLeft, double installment, int userAccountID) {
		this(amount, userAccountID);
		this.amount = amount;
		this.monthsLeft = monthsLeft;
		this.installment = installment;
		this.userAccountID = userAccountID;
	}

	public double getAmount() {
		return amount;
	}

	public int getMonthsLeft() {
		return monthsLeft;
	}

	public double getInstallment() {
		return installment;
	}

	public int getUserAccountID() {
		return userAccountID;
	}
}