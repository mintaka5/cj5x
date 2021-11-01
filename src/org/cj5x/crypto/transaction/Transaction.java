package org.cj5x.crypto.transaction;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Transaction {
	private String from;
	private String to;
	private double amount;
	
	public Transaction() {}
	
	/**
	 * Transaction instance
	 * @param String from Sender wallet address
	 * @param String to Receiver wallet address
	 * @param double d Amount
	 */
	public Transaction(String from, String to, double d) {
		this.setAmount(d);
		this.setFrom(from);
		this.setTo(to);
	}
	
	public JsonElement toJson() {
		Gson gson = new Gson();
		return gson.toJsonTree(this);
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double d) {
		this.amount = d;
	}
}
