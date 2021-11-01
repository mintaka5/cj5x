package org.cj5x.main;

import java.io.IOException;

import org.cj5x.crypto.Blockchain;
import org.cj5x.main.node.ui.Main;

import spark.Spark;

public class App {
	private Blockchain appChain;

	public static void main(String[] args) {
		Main window = new Main();
		try {
			App app = new App();
			window.setApplication(app);
			
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public App() {
		Blockchain blockchain = new Blockchain();
		setAppChain(blockchain);
	}
	
	public void initialize() throws IOException {
		Node node = new Node("127.0.0.1", 8000, "cj5x");
		node.setBlockchain(getAppChain());
	}

	public Blockchain getAppChain() {
		return appChain;
	}

	public void setAppChain(Blockchain appChain) {
		this.appChain = appChain;
	}

	public void stop() {
		Spark.stop();
		System.exit(0);
	}
}
