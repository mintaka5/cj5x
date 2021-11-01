package org.cj5x.crypto;

import java.time.Instant;
import java.util.ArrayList;

import org.cj5x.crypto.transaction.Transaction;

public class Blockchain {
	private ArrayList<Block> chain = new ArrayList<Block>();

	private ArrayList<Transaction> transactions = new ArrayList<Transaction>();

	public Blockchain() {
		// GENESIS
		Block block = new Block(1, Instant.now().getEpochSecond(), "0xGENESIS: value is soul", "0");
		block.mine();
		
		this.getChain().add(block);
		// GENESIS
	}
	
	/**
	 * Takes transactions, mints each one to a block, and adds to the chain
	 * @throws Exception
	 */
	public void mint() throws Exception {
		if(!this.getTransactions().isEmpty()) {
			int txSize = this.getTransactions().size();
			
			for(int i=0; i<txSize; i++) {
				Block lastBlock = this.lastBlock();
				
				Block block = new Block(lastBlock.getIndex()+1, Instant.now().getEpochSecond(), this.getTransactions().get(i).toString(), lastBlock.getHash());
				block.mine();
				
				this.getChain().add(block);
			}
		}
	}

	public Block lastBlock() throws Exception {
		if (this.getChain().isEmpty()) {
			//throw new Exception("Chain is empty. Cannot get last BLOCK");
		}

		return this.getChain().get(this.getChain().size() - 1);
	}

	public ArrayList<Block> getChain() {
		return chain;
	}

	public void setChain(ArrayList<Block> chain) {
		this.chain = chain;
	}

	/**
	 * mine all blocks to ensure PoW
	 */
	public void mine() {
		int chainSize = this.getChain().size();
		
		for (int i = 1; i < chainSize; i++) {
			Block current = this.getChain().get(i);
			Block previous = this.getChain().get(i - 1);

			previous.mine();

			current.setPreviousHash(previous.getHash());
			current.mine();
		}
	}

	/**
	 * Checks for proper links from block to block, linking previousHash and hash
	 * 
	 * @return Boolean
	 */
	public Boolean isVaild() {

		for (int i = 1; i < this.getChain().size(); i++) {
			Block current = this.getChain().get(i);
			Block previous = this.getChain().get(i - 1);

			// compare registered hash with calculated one.
			if (!current.getHash().equals(current.generateHash())) {
				return false;
			}

			// compare previous hash to registered hash
			if (!previous.getHash().equals(current.getPreviousHash())) {
				return false;
			}
		}

		return true;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}
}
