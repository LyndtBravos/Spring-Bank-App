package com.greenhouse.bankapp;

public class DebitOrder {

	private double amount;
	private boolean isActive;
	private int contractLength;
	private String company;
	private int userID;

	public DebitOrder(double amount, boolean isActive, int contractLength, String company, int userID) {
		this.amount = amount;
		this.isActive = isActive;
		this.contractLength = contractLength;
		this.company = company;
		this.userID = userID;
	}

	public double getAmount() {
		return amount;
	}

	public boolean isActive() {
		return isActive;
	}

	public int getContractLength() {
		return contractLength;
	}

	public String getCompany() {
		return company;
	}

	public int getUserID() {
		return userID;
	}
}