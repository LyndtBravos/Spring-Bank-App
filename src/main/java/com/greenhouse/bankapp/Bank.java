package com.greenhouse.bankapp;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Bank{

	DatabaseOperations database = new DatabaseOperations();
	
	public static String generateAccountNumber(String account)	{
		Random random = new Random();
		for (int i = 0; i < 10; i++)
		{
			int n = random.nextInt(10);
			if(i == 0 && n == 0)
				n = random.nextInt(10);
			account = account.concat(Integer.toString(n));
		}

		return account;
	}

	public static List<Transaction> getTransactions(UserAccounts account) {
		return new DatabaseOperations().getTransactions(account.getUserID());
	}

	public String withdraw(int accountID, double amount) {
		String stringReturned;
		UserAccountsResponse uar = database.getClient(accountID);
		CurrentAccount account = getUserAccount(accountID).getCurrent();
		double balance = account.getBalance();
		if(balance - 10 > amount) {
			double newBalance = account.getBalance() - amount;
			Transaction ts = new Transaction("Withdrawal, Balance: " +
													 new DecimalFormat("0.00").format(newBalance), amount, '-', uar.getUserID());
			stringReturned = ts.getReference() + " from: " + account.getFirstName() + " " + account.getLastName() + "'s account!";
			database.updateCurrentAccountBalance(uar.getCurrentID(), newBalance);
			database.addTransaction(ts);
		}else stringReturned = "Amount exceeds balance";
		return stringReturned;
	}

	public String deposit(int accountID, double amount) {
		String stringReturned;
		UserAccountsResponse uar = database.getClient(accountID);
		CurrentAccount account = getUserAccount(accountID).getCurrent();
		double newBalance = account.getBalance() + amount;
		Transaction ts = new Transaction("Depositing: R" +
												 new DecimalFormat("0.00").format(amount)
												+ ", new Balance: " + newBalance, amount, '+', uar.getUserID());
		stringReturned = ts.getReference();
		database.updateCurrentAccountBalance(uar.getCurrentID(), newBalance);
		database.addTransaction(ts);
		return stringReturned;
	}

	public String moveMoneyToSA(int accountID, double amount) {
		String stringReturned;
		UserAccounts account = getUserAccount(accountID);
		CurrentAccount current = account.getCurrent();
		SavingsAccount savings = account.getSavings();
		double currentBalance = current.getBalance();
		if(currentBalance >= amount){
			double savingsBalance = savings.getBalance();
			savingsBalance += amount;
			Transaction ts = new Transaction("Moving Money to Savings Account: Current Account Balance = R" +
													 new DecimalFormat("0.00").format(currentBalance-amount),
											 		amount, '+', account.getUserID());
			database.updateCurrentAccountBalance(account.getCurrentID(), currentBalance-amount);
			database.updateSavingsAccountBalance(account.getSavingsID(), savingsBalance);
			database.addTransaction(ts);
			ts = new Transaction("Getting Money from Current Account: Savings Account Balance = R" +
										 new DecimalFormat("0.00").format(savingsBalance),
								 		amount, '-', account.getUserID());
			stringReturned = ts.getReference() + " was a success!";
			database.addTransaction(ts);
		}else stringReturned = "You don't have enough funds to move this much";
		return stringReturned;
	}

	public String moveMoneyToCA(int accountID, double amount) {
		String stringReturned;
		UserAccounts account = getUserAccount(accountID);
		CurrentAccount current = account.getCurrent();
		SavingsAccount savings = account.getSavings();
		double savingsBalance = savings.getBalance();
		if(savingsBalance >= amount){
			double currentBalance = current.getBalance();
			savingsBalance -= amount;
			Transaction ts = new Transaction("Getting R" + amount + " from Savings Account, Current Account Balance: " +
													 new DecimalFormat("0.00").format(current.getBalance()+amount),
											 		amount, '+', account.getUserID());
			database.addTransaction(ts);
			ts = new Transaction("Moving R" + amount +" to Current Account, Savings Account Balance: " +
										 new DecimalFormat("0.00").format(savingsBalance), amount, '-', account.getUserID());
			stringReturned = ts.getReference() + " from: " + current.getFirstName() + " " + current.getLastName() + "'s account!";
			database.addTransaction(ts);
			database.updateSavingsAccountBalance(account.getSavingsID(), savingsBalance);
			database.updateCurrentAccountBalance(account.getCurrentID(), currentBalance+amount);
		}else stringReturned = "You don't have enough funds to move this much";
		return stringReturned;
	}

	public String transferMoney(int accountID, double amount, String toObject){
		String stringReturned;
		UserAccounts fromAccount = getUserAccount(accountID);
		UserAccounts toAccount = getUserAccount(toObject);
		CurrentAccount fromCurrent = fromAccount.getCurrent();
		if(toAccount != null){
			CurrentAccount toCurrent = toAccount.getCurrent();
			double myBalance = fromCurrent.getBalance();
			if(myBalance > amount){
				double newToAccountBalance = toCurrent.getBalance()+amount;
				myBalance -= amount;
				myBalance = myBalance - (myBalance * 0.05);
				database.updateCurrentAccountBalance(fromAccount.getCurrentID(), myBalance);
				Transaction ts = new Transaction("Sending money to " +
														 toCurrent.getAccountNumber() + ", Balance: " +
														 new DecimalFormat("0.00").format(myBalance)
														, amount, '-', fromAccount.getUserID());
				stringReturned = ts.getReference();
				database.addTransaction(ts);
				database.updateCurrentAccountBalance(toAccount.getCurrentID(), newToAccountBalance);
				ts = new Transaction("Received money from "
											 + fromCurrent.getAccountNumber() + ", Balance: " +
											 new DecimalFormat("0.00").format(newToAccountBalance)
											, amount, '+', toAccount.getUserID());
				database.addTransaction(ts);
			}else stringReturned = "You don't have enough funds to make this transaction";
		}else stringReturned = "That account doesn't exist on the system";
		return stringReturned;
	}

	public static List<String> getCredit(int accountID, double amount) throws IllegalArgumentException {
		List<String> stringsReturned = new ArrayList<>();
		UserAccounts user = getUserAccount(accountID);
		DecimalFormat df = new DecimalFormat("0.00");
		DatabaseOperations databaseInstance = new DatabaseOperations();
		if(user != null){
			double highestAmount = Bank.getTransactions(user).stream()
					.max(Comparator.comparingDouble(Transaction::getAmount))
					.orElseThrow(() -> new IllegalArgumentException("The list is empty")).getAmount();
			highestAmount /= 2;
			stringsReturned.add("You qualify to get credit of R" + df.format(highestAmount));
			if(highestAmount >= amount){
				double currentBalance = user.getCurrent().getBalance();
				double installment = amount / 12;

				String result = databaseInstance.addCredit(user.getUserID(), amount);
				stringsReturned.add(result);
				if(!result.trim().startsWith("Credited"))
					return stringsReturned;

				databaseInstance.updateCurrentAccountBalance(user.getCurrentID(), currentBalance+amount);
				Transaction ts = new Transaction("Credited with R"
						   + amount, amount, '+', user.getUserID());
				stringsReturned.add(ts.getReference());
				databaseInstance.addTransaction(ts);
				stringsReturned.add("You'll be liable to pay us: R" + df.format(installment) + " for the next 12 months");
			}else stringsReturned.add("Amount exceeds your credit limit!");
		}else stringsReturned.add("Client doesn't exist on the system!");
		return stringsReturned;
	}

	public static UserAccounts getUserAccount(String accountNumber) throws IllegalArgumentException{
		UserAccounts user;
		List<UserAccounts> clients = new DatabaseOperations().getClients();

		if(accountNumber.length() != 12)
			throw new IllegalArgumentException("Please provide a valid account number with 12 characters!");

		if(accountNumber.startsWith("CA")) {
			user = clients.stream().filter(s -> s.getCurrent().getAccountNumber().equals(accountNumber)).toList().get(0);
		}else if(accountNumber.startsWith("SA")){
			user = clients.stream().filter(s -> s.getSavings().getAccountNumber().equals(accountNumber)).toList().get(0);
		}else{
			throw new IllegalArgumentException("Please insert a valid Current/Savings account number");
		}
		return user;
	}

	public static UserAccounts getUserAccount(int userAccountID) throws IllegalArgumentException{
		return new DatabaseOperations().getClients()
						.stream()
						.filter(l -> l.getUserID() == userAccountID)
						.findAny()
						.orElseThrow(() -> new IllegalArgumentException("Account ID passed brought back no UserAccount account results"));
	}
}
