package com.greenhouse.bankapp;

public class UserAccountsResponse {

	private final int userID;
	private final int currentID;
	private final int savingsID;

	public UserAccountsResponse(int id, int currentID, int savingsID) {
		this.userID = id;
		this.currentID = currentID;
		this.savingsID = savingsID;
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

}
