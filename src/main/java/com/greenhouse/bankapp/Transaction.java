package com.greenhouse.bankapp;

public class Transaction {
	private String reference;
	private double amount;
	private char status;
	private int userAccountID;

	public Transaction(String reference, double amount, char status, int userAccountID)
	{
		this.reference = reference;
		this.amount = amount;
		this.status = status;
		this.userAccountID = userAccountID;
	}

	public String getReference()
	{
		return reference;
	}
	
	public int getUserID() {
		return userAccountID;
	}

	public String getAmountPrint() {
		return getStatus() + "R" + amount;
	}

	public double getAmount() {
		return amount;
	}

	public char getStatus() {
		return status;
	}

	@Override
	public String toString()
	{
		return "Transaction{" + "reference: '" + getReference() + '\'' + ", amount: " + getAmountPrint() + '}';
	}
}