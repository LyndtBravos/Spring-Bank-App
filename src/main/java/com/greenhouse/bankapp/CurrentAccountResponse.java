package com.greenhouse.bankapp;

public class CurrentAccountResponse {

	private final String firstName;
	private final String lastName;
	private final int idNumber;
	private final String phoneNumber;
	private final String accountNumber;
	private final double balance;
	private final String address;

	public CurrentAccountResponse(String firstName, String lastName, int idNumber, String phoneNumber, String accountNumber,
						  double balance, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.idNumber = idNumber;
		this.phoneNumber = phoneNumber;
		this.accountNumber = (accountNumber == null || accountNumber.trim().isEmpty() ? Bank.generateAccountNumber("CA") : accountNumber);
		this.balance = balance;
		this.address = address;
	}
}
