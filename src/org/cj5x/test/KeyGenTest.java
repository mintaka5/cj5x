package org.cj5x.test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Base58;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KeyGenTest {

	public KeyGenTest() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException {
		Security.addProvider(new BouncyCastleProvider());
		
		KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "BC");

		ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");

		generator.initialize(ecSpec);

		KeyPair pair = generator.generateKeyPair();

		PublicKey pubKey = pair.getPublic();

		PrivateKey privKey = pair.getPrivate();

		ECPrivateKey ePriv = (ECPrivateKey) privKey;
		// is stored by the digital wallets
		String privStr = adjustTo64(ePriv.getS().toString(16));
		
		
		System.out.println("Private key: s[" + privStr.length() + "]: " + privStr);
		
		// ECDSA public key
		ECPublicKey ePub = (ECPublicKey) pubKey;
		ECPoint pt = ePub.getW();
		String sx = adjustTo64(pt.getAffineX().toString(16));
		String sy = adjustTo64(pt.getAffineY().toString(16));
		String bcPub = "04" + sx + sy;
		
		System.out.println("Public key: " + bcPub);
		
		// execute sha-256 and ripemd-160 hashes
		
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] s1 = sha.digest(bcPub.getBytes("UTF-8"));
		
		System.out.println(" sha: " + bytesToHex(s1));
		
		MessageDigest rmd = MessageDigest.getInstance("RipeMD160", "BC");
		byte[] r1 = rmd.digest(s1);
		
		// add a 0x00 version byte at the beginning of the hash
		byte[] r2 = new byte[r1.length + 1];
		r2[0] = 0;
		for(int i=0; i<r1.length; i++) r2[i+1] = r1[i];
		
		System.out.println(" rmd: " + bytesToHex(r2));
		
		// repeat sha-256 hash twice
		byte[] s2 = sha.digest(r2);
		
		System.out.println(" sha: " + bytesToHex(s2));
		
		byte[] s3 = sha.digest(s2);
		
		System.out.println(" sha: " + bytesToHex(s3));
		
		// first 4 bytes of the 2nd hash are used as address checksum and attached to RIPEMD160 hash
		byte[] a1 = new byte[25];
		for(int i=0; i<r2.length; i++) a1[i] = r2[i];
		for(int i=0; i<5; i++) a1[20+i] = s3[i];
		
		// encode address using base58
		String b58 = Base58.encode(a1);
		
		System.out.println(" adr: " + b58);
	}
	
	public String bytesToHex(byte[] b) {
	    return new BigInteger(1, b).toString(16);
	}

	private String adjustTo64(String s) {
		int l = s.length();
		
		if(l < 62 || l > 64) {
			throw new IllegalArgumentException("Not a valid key: " + s);
		}
		
		return StringUtils.leftPad(s, 64, "0");
	}
}
