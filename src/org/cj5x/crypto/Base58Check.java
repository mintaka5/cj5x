package org.cj5x.crypto;

import java.math.BigInteger;

public class Base58Check {
	private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
	private static final BigInteger ALPHABET_SIZE = BigInteger.valueOf(ALPHABET.length());

	public static String rawBytesToBase58(byte[] data) {
		StringBuilder sb = new StringBuilder();
		
		BigInteger num = new BigInteger(1, data);
		
		while(num.signum() != 0) {
			BigInteger[] quotrem = num.divideAndRemainder(ALPHABET_SIZE);
			sb.append(ALPHABET.charAt(quotrem[1].intValue()));
			num = quotrem[0];
		}
		
		for(int i=0; i<data.length && data[i] == 0; i++) {
			sb.append(ALPHABET.charAt(0));
		}
		
		return sb.reverse().toString();
	}
}
