package com.greenhouse.bankapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class DatabaseOperations {
	
	private Connection connection;
	
	public Connection connectToDatabase(){
		if(connection == null)
			connection = getConnection();
		return connection;
	}

	public Connection getConnection() {
		Connection con = null;
		try{
			String url = "jdbc:mysql://root@localhost:3306/bankingapp";
			String root = "root";
			String password = "psycho";
			con = DriverManager.getConnection(url, root, password);
		}catch(SQLException e){
			System.out.println("Something wrong happened when trying to connect to the database" + e.getMessage());
		}
		return con;
	}
	
	public CurrentAccount getCurrentAccount(int currentID) {
		CurrentAccount account = null;
		String sql = "SELECT * FROM currentaccount\r\n"
						+ "where idCurrentAccount = ?;";
		try (Connection con = connectToDatabase();
			 PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, currentID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String name = rs.getString(2);
				String surname = rs.getString(3);
				int idNumber = rs.getInt(4);
				String phoneNumber = rs.getString(5);
				String accountNumber = rs.getString(6);
				double balance = rs.getDouble(7);
				String address = rs.getString(8);
				account = new CurrentAccount(name, surname, idNumber, phoneNumber, accountNumber, balance, address);
			}
			rs.close();
		}catch(SQLException e) {
			System.out.println("Something went wrong with CurrentAccount retrieve method: " + e.getMessage());
		}
		return account;
	}
	
	public SavingsAccount getSavingsAccount(int savingsID) {
		SavingsAccount account = null;
		String sql = "SELECT * FROM savingsaccount\r\n"
						+ "where idSavingsAccount = ?;";
		try {
			Connection con = connectToDatabase();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, savingsID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String name = rs.getString(2);
				String lastName = rs.getString(3);
				int idNumber = rs.getInt(4);
				String phoneNumber = rs.getString(5);
				String accountNumber = rs.getString(6);
				double balance = rs.getDouble(7);
				String address = rs.getString(8);
				account = new SavingsAccount(name, lastName, idNumber, phoneNumber, accountNumber, balance, address);
			}
			rs.close();
			ps.close();
			con.close();
		}catch(SQLException e) {
			System.out.println("Something went wrong with SavingsAccount retrieve method: " + e.getMessage());
		}
		return account;
	}
	
	public UserAccountsResponse getClient(int id) {
		String sql = "SELECT * FROM useraccounts where id = ?;";
		UserAccountsResponse user = null;
		try (Connection con = connectToDatabase();
			 PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				int ID = rs.getInt(1);
				int currentId = rs.getInt(2);
				int savingsId = rs.getInt(3);
				user = new UserAccountsResponse(ID, currentId, savingsId);
			}
			rs.close();
		}catch(SQLException e) {
			System.out.println("Something went wrong with retrieving client: " + e.getMessage());
		}
		
		return user;
	}
	
	public List<UserAccounts> getClients(){
		List<UserAccounts> clients = new ArrayList<>();
		String sql = "SELECT * FROM bankingapp.useraccounts";
		try (Connection con = connectToDatabase()){
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				int id = rs.getInt(1);
				int idSavings = rs.getInt(2);
				int idCurrent = rs.getInt(3);
				UserAccounts ua = new UserAccounts(id, idSavings, idCurrent);
				clients.add(ua);
			}
			rs.close();
			ps.close();
		}catch(SQLException ex) {
			System.out.println("Getting query results for clients failed: " + ex.getMessage());
		}
		return clients;
	}
	
	public List<Transaction> getTransactions(int accountID){
		List<Transaction> list = new ArrayList<>();
		String sql = "select * from transaction\n"
				+ " where accountId = ?;";
		try (Connection con = connectToDatabase();
			 PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, accountID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String ref = rs.getString(2);
				double amount = rs.getDouble(3);
				char status = rs.getString(4).toCharArray()[0];
				Transaction ts = new Transaction(ref, amount, status, accountID);
				list.add(ts);
			}
			rs.close();
		}catch(SQLException e) {
			System.out.println("Something went wrong with getting list of transactions for client: " + e.getMessage()); 
		}
		return list;
	}
	
	public Credit getCredit(int accountID) {
		Credit credit = null;
		String sql = "SELECT * FROM bankingapp.credit \r\n"
				+ "where idUserAccount = ?;";

		try (Connection con = connectToDatabase();
			 PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, accountID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				credit = new Credit(rs.getDouble(2), rs.getInt(3), rs.getDouble(4), rs.getInt(5));
			}
			rs.close();
		}catch(SQLException e) {
			System.out.println("Something went wrong with retrieving credit info: " + e.getMessage());
		}
		return credit;
	}

	public String addCredit(int accountID, double amount) {
		String stringReturned;
		String sql = "INSERT INTO credit\r\n"
				+ "(`Amount`,\r\n"
				+ "`monthsLeft`,\r\n"
				+ "`installment`,\r\n"
				+ "`idUserAccount`)\r\n"
				+ "VALUES\r\n"
				+ "(?,?,?,?);";
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			Credit credit = getCredit(accountID);
			if(credit != null && credit.getAmount() > 0) {
				stringReturned = "You already owe the bank: R" + new DecimalFormat("0.00").format(credit.getAmount());
				return stringReturned;
			}
			ps.setDouble(1, amount);
			ps.setInt(2, 12);
			ps.setDouble(3, amount/12);
			ps.setInt(4, accountID);

			Transaction ts = new Transaction("Credited with: R" + new DecimalFormat("0.00").format(amount)
							, amount, '+', accountID);
			stringReturned = ts.getReference();
			addTransaction(ts);
		}catch(SQLException e) {
			stringReturned = "Something went wrong with credit insertion: " + e.getMessage();
		}
		return stringReturned;
	}

	public String payCreditInstallment(int accountID, double amount) {
		String stringReturned;
		String sql = "UPDATE credit SET \n"
						+ "Amount = ?,\n"
						+ "monthsLeft = ?\n"
						+ "WHERE idUserAccount = ?\n"
						+ "and amount = ?;";
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			Credit credit = getCredit(accountID);
			if(credit != null && credit.getAmount() > 0) {
				if(credit.getAmount() < amount) {
					stringReturned = "You only owe us: R" + credit.getAmount() + ", settle this amount or less, please!";
					return stringReturned;
				}
				double newAmountOwed = credit.getAmount() - amount;
				int monthsLeftDecremented = credit.getMonthsLeft() - 1;
				ps.setDouble(1, newAmountOwed);
				monthsLeftDecremented = Math.min(monthsLeftDecremented, 0);
				ps.setInt(2, monthsLeftDecremented);
				ps.setInt(3, credit.getUserAccountID());
				ps.setDouble(4, credit.getAmount());
				stringReturned = "You've paid: R" + new DecimalFormat("0.00").format(amount);
			}else {
				stringReturned = "You don't owe us any amount";
			}
		}catch(SQLException e) {
			stringReturned = "Something went wrong with updating credit record: " + e.getMessage();
		}
		return stringReturned;
	}
	
	public List<DebitOrder> getDebitOrderList(int userID) {
		String sql = "SELECT * FROM bankingapp.debitorder\r\n"
				+ "where userID = ?;";
		List<DebitOrder> list = new ArrayList<>();

		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, userID);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				double amount = rs.getDouble(2);
				boolean isActive = rs.getBoolean(3);
				int contractLength = rs.getInt(4);
				String company = rs.getString(5);
				DebitOrder order = new DebitOrder(amount, isActive, contractLength, company, userID);
				list.add(order);
			}
		}catch(SQLException e) {
			System.out.println("Something went wrong with retrieving Debit Order list");
		}
		return list;
	}

	public String addDebitOrder(DebitOrder order) {
		String stringReturned = "";
		List<DebitOrder> list = getDebitOrderList(order.getUserID());
		for(DebitOrder anotherOrder : list) {
			if(order.getCompany().equalsIgnoreCase(anotherOrder.getCompany())) {
				stringReturned = "You already have a Debit Order from this company";
				return stringReturned;
			}
		}
		String sql = "INSERT INTO debitorder\r\n"
				+ "(`amount`,\r\n"
				+ "`isActive`,\r\n"
				+ "`contractLength`,\r\n"
				+ "`company`,\r\n"
				+ "`userID`)\r\n"
				+ "VALUES\r\n"
				+ "(?,?,?,?,?);";

		try (Connection con = getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)){
			ps.setDouble(1, order.getAmount());
			ps.setBoolean(2, true);
			ps.setInt(3, order.getContractLength());
			ps.setString(4, order.getCompany());
			ps.setInt(5, order.getUserID());
			ps.executeUpdate();
			stringReturned = "Hooray, debit order successfully added";
		}catch(SQLException e) {
			stringReturned = "Something went wrong with Debit Order insertion: " + e.getMessage();
		}
		return stringReturned;
	}

	public String switchDebitOrderStatus(DebitOrder order) {
		String stringReturned;
		List<DebitOrder> list = getDebitOrderList(order.getUserID());
		String sql = "UPDATE debitorder SET\n"
				+ "isActive = ? WHERE userID = ? and company = ?;";

		if(list.isEmpty()) {
			stringReturned = "You've got no Debit Orders to cancel";
		}else {
			for(DebitOrder anotherOrder : list) {
				if(order.getCompany().equalsIgnoreCase(anotherOrder.getCompany())) {
					try(Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(sql)) {
						ps.setBoolean(1, !anotherOrder.isActive());
						ps.setInt(2, order.getUserID());
						ps.setString(3, order.getCompany());
						ps.executeUpdate();
						stringReturned = "You've switched your contract active status with: " + order.getCompany()
												+ ", it's now -> " + !anotherOrder.isActive();
						return stringReturned;
					}catch(SQLException e) {
						stringReturned = "Something went wrong with terminating the Debit Order! Try again, please.";
						return stringReturned;
					}
				}
			}
			stringReturned = "Debit Order doesn't exist on the system";
		}
		return stringReturned;
	}
	
	public String payDebitOrder(String accountNumber) {
		String stringReturned = "Ran, nicely done!!!";
		UserAccounts account = Bank.getUserAccount(accountNumber);
		List<DebitOrder> list = null;
		if(account != null)
			list = getDebitOrderList(account.getUserID());
		if(list == null)
			stringReturned = "No debit order active on this account";
		double debitTotal = 0;
		double currentBalance = account.getCurrent().getBalance();
		for(DebitOrder order: list)
			if(order.isActive())
				debitTotal += order.getAmount();
		if(debitTotal < currentBalance)
			updateCurrentAccountBalance(account.getUserID(), currentBalance-debitTotal);
		else
			stringReturned = "Debit Order bounced back due to not enough funds";
		return stringReturned;
	}
	
	public String addTransaction(Transaction ts) {
		String sql = "INSERT INTO transaction\r\n"
				+ "(`Reference`,\r\n"
				+ "`Amount`,\r\n"
				+ "`Status`,\r\n"
				+ "`accountID`)\r\n"
				+ "VALUES (?,?,?,?);";
		String stringReturned;
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, ts.getReference());
			ps.setDouble(2, ts.getAmount());
			ps.setString(3, String.valueOf(ts.getStatus()));
			ps.setInt(4, ts.getUserID());
			ps.executeUpdate();
			stringReturned = "Transaction added successfully";
		}catch(SQLException e) {
			stringReturned = "Something went wrong with Transaction addition: " + e.getMessage();
		}
		return stringReturned;
	}
	
	public String addUser(CurrentAccount account) {
		String stringReturned;
		String sql = "INSERT INTO `bankingapp`.`useraccounts`\n" +
				"(`idSavings`,\n" +
				"`idCurrent`,\n" +
				"`isActive`)\n" +
				"VALUES\n" +
				"(?,?,?);";

		try (Connection con = getConnection()){
			PreparedStatement ps = con.prepareStatement(sql);

			addSavingsAccount(new SavingsAccount(account.getFirstName(), account.getLastName(), account.getIdNumber(),
												 account.getPhoneNumber(), null, 0, account.getAddress()));
			int id = addCurrentAccount(account);

			ps.setInt(1, id);
			ps.setInt(2, id);
			ps.setBoolean(3, true);
			ps.executeUpdate();
			stringReturned = "New UserAccount addition was a success";
			sql = "SELECT * FROM useraccounts where idCurrent = ?;";
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				id = rs.getInt(1);
			Transaction ts = new Transaction("Joining Bonus: 500", 500, '+', id);
			addTransaction(ts);
			ps.close();
		}catch(SQLException e) {
			stringReturned = "Something wrong happened with UserAccount addition: " + e.getMessage();
		}
		return stringReturned;
	}
	
	public int addCurrentAccount(CurrentAccount current) {
		int id = 0;
		String sql = "INSERT INTO currentaccount\r\n"
				+ "(`name`,\r\n"
				+ "`surname`,\r\n"
				+ "`idNumber`,\r\n"
				+ "`phoneNumber`,\r\n"
				+ "`accountNumber`,\r\n"
				+ "`balance`,\r\n"
				+ "`address`)\r\n"
				+ "VALUES\r\n"
				+ "(?,?,?,?,?,?,?);";
		try (Connection con = getConnection()){
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, current.getFirstName());
			ps.setString(2, current.getLastName());
			ps.setInt(3, current.getIdNumber());
			ps.setString(4, current.getPhoneNumber());
			ps.setString(5, current.getAccountNumber());
			ps.setDouble(6, current.getBalance());
			ps.setString(7, current.getAddress());
			ps.executeUpdate();
			System.out.println("CurrentAccount addition was a success");
			sql = "SELECT * FROM currentaccount order by idCurrentAccount desc limit 1;";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				id = rs.getInt(1);
			rs.close();
			ps.close();
		}catch(SQLException e) {
			System.out.println("Something went wrong with CurrentAccount addition: " + e.getMessage());
		}
		return id;
	}
	
	public String addSavingsAccount(SavingsAccount savings) {
		String stringReturned;
		String sql = "INSERT INTO savingsaccount\r\n"
				+ "(`name`,\r\n"
				+ "`surname`,\r\n"
				+ "`idNumber`,\r\n"
				+ "`phoneNumber`,\r\n"
				+ "`accountNumber`,\r\n"
				+ "`balance`,\r\n"
				+ "`address`)\r\n"
				+ "VALUES\r\n"
				+ "(?,?,?,?,?,?,?);";
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, savings.getFirstName());
			ps.setString(2, savings.getLastName());
			ps.setInt(3, savings.getIdNumber());
			ps.setString(4, savings.getPhoneNumber());
			ps.setString(5, savings.getAccountNumber());
			ps.setDouble(6, savings.getBalance());
			ps.setString(7, savings.getAddress());
			ps.executeUpdate();
			stringReturned = "SavingsAccount addition was a success";
		}catch(SQLException e) {
			stringReturned = "Something went wrong with SavingsAccount addition: " + e.getMessage();
		}
		return stringReturned;
	}
	
	public String updateCurrentAccountBalance(int accountID, double amount) {
		String stringReturned;
		String sql = "UPDATE currentaccount\r\n"
				+ "SET\r\n"
				+ "`balance` = ?\r\n"
				+ "WHERE `idCurrentAccount` = ?;";
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			ps.setDouble(1, amount);
			ps.setInt(2, accountID);
			ps.executeUpdate();
			stringReturned = "CurrentAccount balance update was a success";
		}catch(SQLException e) {
			stringReturned = "Something went wrong with CurrentAccount balance update: " + e.getMessage();
		}
		return stringReturned;
	}
	
	public String updateSavingsAccountBalance(int accountID, double amount) {
		String stringReturned;
		String sql = "UPDATE savingsaccount\r\n"
				+ "SET\r\n"
				+ "`balance` = ?\r\n"
				+ "WHERE `idSavingsAccount` = ?;";
		try (Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);){
			ps.setDouble(1, amount);
			ps.setInt(2, accountID);
			ps.executeUpdate();
			stringReturned = "SavingsAccount balance update was a success";
		}catch(SQLException e) {
			stringReturned = "Something went wrong with SavingsAccount balance update: " + e.getMessage();
		}
		return stringReturned;
	}
	
	public String deActivateUser(int userID) {
		String stringReturned;
		String sql = "UPDATE useraccounts \n"
						+ "SET \n"
						+ "isActive = ? \n"
						+ "WHERE ID = ?;";
		try(Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			ps.setBoolean(1, false);
			ps.setInt(2, userID);
			stringReturned = "Deleting user was a success";
		}catch(SQLException e){
			stringReturned = "Something went wrong with deleting this user: " + e.getMessage();
		}
		return stringReturned;
	}
}