package com.greenhouse.bankapp;

public class SavingsAccount {
	private final String firstName;
	private final String lastName;
	private final int idNumber;
	private final String phoneNumber;
	private final String accountNumber;
	private final double balance;
	private final String address;

	public SavingsAccount(String firstName, String lastName, int idNumber, String phoneNumber, String accountNumber,
			double balance, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.idNumber = idNumber;
		this.phoneNumber = phoneNumber;
		this.accountNumber = accountNumber != null ? accountNumber : Bank.generateAccountNumber("SA");
		this.balance = (balance == 0) ? 500.00 : balance;
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName()	{
		return lastName;
	}

	public int getIdNumber() {
		return idNumber;
	}

	public String getPhoneNumber()	{
		return phoneNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	public String getAddress() {
		return address;
	}
}