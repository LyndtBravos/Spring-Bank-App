package com.greenhouse.bankapp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EndPoint {

	@RequestMapping
	public String sayHello() {
		return "Hello World from Spring Boot";
	}

	@RequestMapping("/goodbye")
	public String sayBye() {
		return "Goodbye, from Spring Boot & Brian";
	}

	@RequestMapping(value = "/getCurrentAccount/{currentID}")
	public CurrentAccount getCurrentAccount(@PathVariable int currentID) {
		return new DatabaseOperations().getCurrentAccount(currentID);
	}

	@RequestMapping(value = "/getSavingsAccount/{savingsID}")
	public SavingsAccount getSavingsAccount(@PathVariable int savingsID) {
		return new DatabaseOperations().getSavingsAccount(savingsID);
	}

	@RequestMapping(value = "/getClient/{userID}", method = RequestMethod.GET,
			consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserAccountsResponse getUserAccount(@PathVariable int userID) {
		return new DatabaseOperations().getClient(userID);
	}

	@RequestMapping(value = "/getAllClients")
	public List<UserAccounts> getUserAccounts() {
		return new DatabaseOperations().getClients();
	}

	@RequestMapping(value = "/getClientTransactions/{userID}/", method = RequestMethod.GET)
	public List<Transaction> getTransactions(@PathVariable int userID) {
		return new DatabaseOperations().getTransactions(userID);
	}

	@RequestMapping(value = "/getCredit/{accountID}", method = RequestMethod.GET)
	public Credit getCredit(@PathVariable int accountID) {
		return new DatabaseOperations().getCredit(accountID);
	}

	@RequestMapping(value = "/withdraw/{accountID}/{amount}", method = RequestMethod.POST)
	public String withdraw(@PathVariable int accountID, @PathVariable double amount) {
		return new Bank().withdraw(accountID, amount);
	}

	@RequestMapping(value = "/deposit/{accountID}/{amount}", method = RequestMethod.POST)
	public String deposit(@PathVariable int accountID, @PathVariable double amount) {
		return new Bank().deposit(accountID, amount);
	}

	@RequestMapping(value = "/moveMoneyToCA/{accountID}/{amount}", method = RequestMethod.POST)
	public String moveMoneyToCA(@PathVariable int accountID, @PathVariable double amount) {
		return new Bank().moveMoneyToCA(accountID, amount);
	}

	@RequestMapping(value = "/moveMoneyToSA/{accountID}/{amount}", method = RequestMethod.POST)
	public String moveMoneyToSA(@PathVariable int accountID, @PathVariable double amount) {
		return new Bank().moveMoneyToSA(accountID, amount);
	}

	@RequestMapping(value = "/transferMoney/{accountID}/{amount}/{toAccountNumber}", method = RequestMethod.POST)
	public String transferMoney(@PathVariable int accountID, @PathVariable double amount, @PathVariable String toAccountNumber) {
		return new Bank().transferMoney(accountID, amount, toAccountNumber);
	}

	@RequestMapping(value = "/getDebitOrderList/{accountID}", method = RequestMethod.GET)
	public List<DebitOrder> getDebitOrderList(@RequestBody @PathVariable int accountID){
		return new DatabaseOperations().getDebitOrderList(accountID);
	}

	@RequestMapping(value = "/addTransaction", method = RequestMethod.POST, consumes = "application/json")
	public String addTransaction(@RequestBody Transaction ts) {
		return new DatabaseOperations().addTransaction(ts);
	}

	@RequestMapping(value = "/addNewClient",
			method = RequestMethod.POST, consumes = "application/json")
	public String addNewClient(@RequestBody CurrentAccount user) {
		return new DatabaseOperations().addUser(user);
	}

	@RequestMapping(value = "/switchDebitOrderStatus",
			method=RequestMethod.POST, consumes = "application/json")
	public String switchDebitOrderStatus(@RequestBody DebitOrder order) {
		return new DatabaseOperations().switchDebitOrderStatus(order);
	}

	@RequestMapping(value = "/addCredit/{accountID}/{amount}", method = RequestMethod.POST)
	public List<String> addCredit(@PathVariable int accountID,@PathVariable double amount) {
		return Bank.getCredit(accountID, amount);
	}

	@RequestMapping(value = "/addDebitOrder", method = RequestMethod.POST, consumes = "application/json")
	public String addDebitOrder(@RequestBody DebitOrder order) {
		return new DatabaseOperations().addDebitOrder(order);
	}

	@RequestMapping(value = "/updateCurrentBalance/{id}/{amount}", method = RequestMethod.PUT)
	public String updateCurrentAccountBalance(@PathVariable int id, @PathVariable double amount) {
		return new DatabaseOperations().updateCurrentAccountBalance(id, amount);
	}

	@RequestMapping(value = "/updateSavingsBalance/{id}/{amount}", method = RequestMethod.PUT)
	public String updateSavingsAccountBalance(@PathVariable int id, @PathVariable double amount) {
		return new DatabaseOperations().updateSavingsAccountBalance(id, amount);
	}

	@RequestMapping(value = "/payCredit/{accountID}/{amount}", method = RequestMethod.PUT)
	public String payCreditInstallment(@PathVariable int accountID, @PathVariable double amount) {
		return new DatabaseOperations().payCreditInstallment(accountID, amount);
	}

	@RequestMapping(value = "/payDebitOrder/{accountNumber}", method = RequestMethod.PUT)
	public String payDebitOrder(@PathVariable String accountNumber) {
		return new DatabaseOperations().payDebitOrder(accountNumber);
	}

	@RequestMapping(value = "/deActivateUser/{accountID}", method = RequestMethod.PUT)
	public String deleteUser(@PathVariable int accountID) {
		return new DatabaseOperations().deActivateUser(accountID);
	}
}