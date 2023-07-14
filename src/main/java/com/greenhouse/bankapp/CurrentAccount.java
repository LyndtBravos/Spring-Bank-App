package com.greenhouse.bankapp;

public class CurrentAccount {

	private String firstName;
	private String lastName;
	private int idNumber;
	private String phoneNumber;
	private String accountNumber;
	private double balance;
	private String address;

	public CurrentAccount(String firstName, String lastName, int idNumber, String phoneNumber, String accountNumber,
			double balance, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.idNumber = idNumber;
		this.phoneNumber = phoneNumber;
		this.accountNumber = (accountNumber == null || accountNumber.trim().isEmpty() ? Bank.generateAccountNumber("CA") : accountNumber);
		this.balance = balance;
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
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

	@Override
	public String toString()
	{
		return "CurrentAccount{" + "firstName='" + firstName + '\'' + ",\nlastName='" + lastName + '\'' + ",\nidNumber=" + idNumber
				+ ",\nphoneNumber='" + phoneNumber + '\'' + ",\naccountNumber='"
				+ accountNumber + '\'' + ",\nbalance=" + balance + ",\naddress='" + address + '\'' + '}';
	}
}