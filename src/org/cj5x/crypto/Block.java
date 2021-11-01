package org.cj5x.crypto;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;

public class Block {
	@Expose
	private int index;
	@Expose
	private long timestamp;
	@Expose
	private String data;
	@Expose
	private String previousHash;
	@Expose
	private int nonce;
	@Expose
	private String hash;
	private int difficulty;
	
	public Block(int index, long timestamp, String data, String previousHash) {
		this.setData(data);
		this.setIndex(index);
		this.setPreviousHash(previousHash);
		this.setTimestamp(timestamp);
		this.setNonce(0);
		this.setDifficulty(4);
		
		this.setHash(this.generateHash());
	}
	
	/**
	 * Proof of work!
	 */
	public void mine() {
		String prefix = StringUtils.repeat("0", this.getDifficulty());
		
		while(!this.getHash().startsWith(prefix)) {
			this.setNonce(this.getNonce()+1);
			
			this.setHash(this.generateHash());
		}
	}

	public String generateHash() {
		String hash = DigestUtils.sha256Hex(this.buildMessage());
		
		return hash;
	}
	
	private String buildMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getIndex());
		sb.append(this.getTimestamp());
		sb.append(this.getNonce());
		sb.append(this.getPreviousHash());
		sb.append(this.getData());
		
		return sb.toString();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
}