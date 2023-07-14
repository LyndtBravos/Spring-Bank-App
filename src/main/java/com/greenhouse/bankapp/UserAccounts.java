package com.greenhouse.bankapp;

public class UserAccounts {

	private final int userID;
	private final int currentID;
	private final int savingsID;
	private CurrentAccount current;
	private SavingsAccount savings;

	public UserAccounts(int id, int currentID, int savingsID) {
		this.userID = id;
		this.currentID = currentID;
		this.savingsID = savingsID;
		this.current = getCurrent();
		this.savings = getSavings();
	}

	public int getUserID() {
		return userID;
	}
	
	public int getCurrentID() {
		return currentID;
	}
	
	public int getSavingsID() {
		return savingsID;
	}
	
	public CurrentAccount getCurrent() {
		if(this.current == null)
			this.current = new DatabaseOperations().getCurrentAccount(this.currentID);
		return current;
	}

	public SavingsAccount getSavings() {
		if(savings == null)
			savings = new DatabaseOperations().getSavingsAccount(this.savingsID);
		return savings;
	}
}