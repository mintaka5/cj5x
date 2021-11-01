package org.cj5x.main;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.html;
import static j2html.TagCreator.main;

import java.io.IOException;

import org.cj5x.crypto.Blockchain;
import org.cj5x.crypto.Wallet;
import org.cj5x.crypto.transaction.Transaction;
import org.cj5x.main.node.CorsFilter;
import org.cj5x.main.util.MimeTypes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import spark.Spark;

public class Node {
	private String name = "The Node";
	private String host = "127.0.0.1";
	private int port = 8000;
	private Blockchain blockchain;
	private Wallet wallet;

	/**
	 * A blockchain node to be used for mining, distributing work
	 * @param host Hostname
	 * @param port Host's port numer
	 * @param name Custom name of the node
	 * @throws IOException
	 */
	public Node(String host, int port, String name) throws IOException {
		this.setName(name);
		this.setHost(host);
		this.setPort(port);
		
		init();
	}

	/**
	 * Initialize the service
	 */
	public void init() {
		Spark.port(getPort());
		Spark.threadPool(8, 2, 10000);

		// TODO  CORS is locked down by default in Spark, opened for now
		CorsFilter.apply();
		
		Spark.get("/", (req, res) -> {
			return html(
					body(
						main(attrs("#main.content"), div("Henlo! ^.^"))
						)
					).render();
		});

		Spark.get("/chain", (req, res) -> {
			res.type(MimeTypes.Application.JSON);

			return new Gson().toJson(this.getBlockchain().getChain());
		});

		Spark.post("/transaction", (req, res) -> {
			JsonObject payload = new Gson().fromJson(req.body(), JsonObject.class);
			
			String from = payload.get("from").getAsString();
			String to = payload.get("to").getAsString();
			double amount = payload.get("amount").getAsDouble();

			Transaction tx = new Transaction(from, to, amount);

			this.getBlockchain().getTransactions().add(tx);

			res.type(MimeTypes.Application.JSON);

			return new Gson().toJson(tx);
		});

		Spark.get("/transactions/list", (req, res) -> {
			res.type(MimeTypes.Application.JSON);

			return new Gson().toJson(this.getBlockchain().getTransactions());
		});

		Spark.get("/wallet/new", (req, res) -> {
			Wallet w = new Wallet();
			setWallet(w);

			res.type(MimeTypes.Application.JSON);

			return getWallet().toString();
		});
		
		Spark.get("/wallet/export", (req, res) -> {
			res.type(MimeTypes.Text.PLAIN);
			
			Wallet w = getWallet();
			setWallet(null);
			
			return w.toPem();
		});

		Spark.post("/mint", (req, res) -> {
			this.getBlockchain().mint();

			this.getBlockchain().getTransactions().clear();

			return new Gson().toJson(this.getBlockchain().getChain());
		});
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Blockchain getBlockchain() {
		return blockchain;
	}

	public void setBlockchain(Blockchain blockchain) {
		this.blockchain = blockchain;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
}
